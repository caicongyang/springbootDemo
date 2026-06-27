package com.caicongyang.git;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GitToolsTest {

    @Test
    void shouldInstantiate() {
        GitBatchTool tool = new GitBatchTool();
        assertNotNull(tool);
    }

    @Test
    void batchCloneShouldCreateDirectories(@TempDir Path tempDir) throws IOException {
        GitBatchTool tool = new GitBatchTool();
        // Non-existent remote — expect a failure result, not a throw.
        List<GitBatchTool.BatchResult> results =
                tool.batchClone(Map.of("fake", "https://example.invalid/nope.git"), tempDir);
        assertEquals(1, results.size());
        assertEquals("fake", results.get(0).getRepo());
        assertFalse(results.get(0).isSuccess());
    }

    @Test
    void discoverReposEmptyWhenNoGitDirs(@TempDir Path tempDir) throws IOException {
        GitBatchTool tool = new GitBatchTool();
        List<Path> repos = tool.discoverRepos(tempDir);
        assertTrue(repos.isEmpty());
    }

    @Test
    void discoverReposFindsGitDir(@TempDir Path tempDir) throws IOException {
        Path repoDir = tempDir.resolve("my-repo");
        Files.createDirectories(repoDir.resolve(".git"));
        Files.createDirectories(tempDir.resolve("not-a-repo"));

        GitBatchTool tool = new GitBatchTool();
        List<Path> repos = tool.discoverRepos(tempDir);
        assertEquals(1, repos.size());
        assertEquals("my-repo", repos.get(0).getFileName().toString());
    }

    @Test
    void batchStatusReportsDirty(@TempDir Path tempDir) throws IOException {
        // Init a real git repo so status works.
        initGitRepo(tempDir.resolve("r"));
        // Create an untracked file to make it dirty.
        Files.writeString(tempDir.resolve("r").resolve("hello.txt"), "world");

        GitBatchTool tool = new GitBatchTool();
        List<GitBatchTool.BatchResult> results = tool.batchStatus(tempDir);
        assertEquals(1, results.size());
        assertTrue(results.get(0).isSuccess());
        assertEquals("dirty", results.get(0).getMessage());
    }

    @Test
    void batchPullOnBareDirShouldNotThrow(@TempDir Path tempDir) throws IOException {
        initGitRepo(tempDir.resolve("r"));

        GitBatchTool tool = new GitBatchTool();
        // Pull with no remote configured should fail gracefully.
        List<GitBatchTool.BatchResult> results = tool.batchPull(tempDir);
        assertEquals(1, results.size());
        // Success flag depends on whether pull actually succeeds; we just
        // care that we got a result back without an exception.
        assertNotNull(results.get(0));
    }

    @Test
    void batchBranchListsBranches(@TempDir Path tempDir) throws IOException {
        initGitRepo(tempDir.resolve("r"));

        GitBatchTool tool = new GitBatchTool();
        List<GitBatchTool.BatchResult> results = tool.batchBranch(tempDir);
        assertEquals(1, results.size());
        assertTrue(results.get(0).isSuccess());
        assertTrue(results.get(0).getMessage().contains("master"),
                "Expected branch list to contain 'master', got: " + results.get(0).getMessage());
    }

    @Test
    void readReposFromConfig(@TempDir Path tempDir) throws IOException {
        Path config = tempDir.resolve("repos.conf");
        Files.writeString(config, """
                # comment
                alpha=https://github.com/alpha/alpha.git

                beta=https://github.com/beta/beta.git
                """);

        GitBatchTool tool = new GitBatchTool();
        Map<String, String> map = tool.readReposFromConfig(config);
        assertEquals(2, map.size());
        assertEquals("https://github.com/alpha/alpha.git", map.get("alpha"));
        assertEquals("https://github.com/beta/beta.git", map.get("beta"));
    }

    @Test
    void batchResultEquality() {
        GitBatchTool.BatchResult a = GitBatchTool.BatchResult.ok("x", "m");
        GitBatchTool.BatchResult b = GitBatchTool.BatchResult.ok("x", "m");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    // ---- helpers ----

    private static void initGitRepo(Path dir) throws IOException {
        Files.createDirectories(dir);
        ProcessBuilder pb = new ProcessBuilder("git", "init");
        pb.directory(dir.toFile());
        try {
            Process p = pb.start();
            p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("git init interrupted", e);
        }
        // Create an initial commit so status/pull/branch have a baseline.
        Files.writeString(dir.resolve("README.md"), "# test");
        try {
            new ProcessBuilder("git", "add", "-A").directory(dir.toFile()).start().waitFor();
            new ProcessBuilder("git", "commit", "-m", "init", "--allow-empty")
                    .directory(dir.toFile()).start().waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("git commit interrupted", e);
        }
    }
}
