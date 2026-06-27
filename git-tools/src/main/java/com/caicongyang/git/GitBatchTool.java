package com.caicongyang.git;

import jakarta.annotation.PreDestroy;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Batch Git operations using JGit.
 * Operates on a set of repository directories — either read from a config file
 * or discovered by scanning a parent directory for .git folders.
 */
public class GitBatchTool {

    private static final String GIT_DIR = ".git";

    /**
     * Clone multiple repositories into the target directory.
     *
     * @param repos       ordered map of repo name → clone URL
     * @param targetDir   directory under which each repo will be cloned
     * @return list of results per repo
     */
    public List<BatchResult> batchClone(Map<String, String> repos, Path targetDir) throws IOException {
        List<BatchResult> results = new ArrayList<>();
        Files.createDirectories(targetDir);
        for (Map.Entry<String, String> entry : repos.entrySet()) {
            String name = entry.getKey();
            String url = entry.getValue();
            Path dest = targetDir.resolve(name);
            try {
                Git.cloneRepository()
                        .setURI(url)
                        .setDirectory(dest.toFile())
                        .call()
                        .close();
                results.add(BatchResult.ok(name, "cloned → " + dest));
            } catch (GitAPIException e) {
                results.add(BatchResult.fail(name, "clone failed: " + e.getMessage()));
            }
        }
        return results;
    }

    /**
     * Pull the default branch for every Git repository found directly under
     * {@code parentDir}.
     */
    public List<BatchResult> batchPull(Path parentDir) throws IOException {
        List<Path> repos = discoverRepos(parentDir);
        List<BatchResult> results = new ArrayList<>();
        for (Path repoDir : repos) {
            String name = repoDir.getFileName().toString();
            try (Git git = Git.open(repoDir.toFile())) {
                git.pull().call();
                results.add(BatchResult.ok(name, "pulled"));
            } catch (GitAPIException e) {
                results.add(BatchResult.fail(name, "pull failed: " + e.getMessage()));
            }
        }
        return results;
    }

    /**
     * Check the working-tree status of every Git repository found directly
     * under {@code parentDir}.
     */
    public List<BatchResult> batchStatus(Path parentDir) throws IOException {
        List<Path> repos = discoverRepos(parentDir);
        List<BatchResult> results = new ArrayList<>();
        for (Path repoDir : repos) {
            String name = repoDir.getFileName().toString();
            try (Git git = Git.open(repoDir.toFile())) {
                Status status = git.status().call();
                boolean clean = status.isClean();
                results.add(BatchResult.ok(name, clean ? "clean" : "dirty"));
            } catch (GitAPIException e) {
                results.add(BatchResult.fail(name, "status failed: " + e.getMessage()));
            }
        }
        return results;
    }

    /**
     * List branches for every Git repository found directly under
     * {@code parentDir}. Returns a flat result per repo; the message embeds
     * branch names separated by commas.
     */
    public List<BatchResult> batchBranch(Path parentDir) throws IOException {
        List<Path> repos = discoverRepos(parentDir);
        List<BatchResult> results = new ArrayList<>();
        for (Path repoDir : repos) {
            String name = repoDir.getFileName().toString();
            try (Git git = Git.open(repoDir.toFile())) {
                List<Ref> refs = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
                String branches = refs.stream()
                        .map(r -> r.getName().replaceFirst("^refs/(heads|remotes/.*?)/", ""))
                        .collect(Collectors.joining(", "));
                results.add(BatchResult.ok(name, branches));
            } catch (GitAPIException e) {
                results.add(BatchResult.fail(name, "branch list failed: " + e.getMessage()));
            }
        }
        return results;
    }

    // ---- helpers ----

    /**
     * Read repo name→URL mappings from a plain-text config file.
     * One entry per line: {@code name=url}.
     */
    public Map<String, String> readReposFromConfig(Path configFile) throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        for (String line : Files.readAllLines(configFile)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            int eq = trimmed.indexOf('=');
            if (eq < 1) {
                continue;
            }
            String name = trimmed.substring(0, eq).trim();
            String url = trimmed.substring(eq + 1).trim();
            if (!name.isEmpty() && !url.isEmpty()) {
                map.put(name, url);
            }
        }
        return map;
    }

    /**
     * Discover immediate child directories of {@code parentDir} that contain a
     * {@code .git} subdirectory.
     */
    public List<Path> discoverRepos(Path parentDir) throws IOException {
        if (!Files.isDirectory(parentDir)) {
            return Collections.emptyList();
        }
        try (Stream<Path> entries = Files.list(parentDir)) {
            return entries
                    .filter(Files::isDirectory)
                    .filter(p -> Files.isDirectory(p.resolve(GIT_DIR)))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * Lightweight result container for batch operations.
     */
    public static class BatchResult {
        private final String repo;
        private final boolean success;
        private final String message;

        public BatchResult(String repo, boolean success, String message) {
            this.repo = repo;
            this.success = success;
            this.message = message;
        }

        public static BatchResult ok(String repo, String message) {
            return new BatchResult(repo, true, message);
        }

        public static BatchResult fail(String repo, String message) {
            return new BatchResult(repo, false, message);
        }

        public String getRepo() { return repo; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BatchResult)) return false;
            BatchResult that = (BatchResult) o;
            return success == that.success
                    && Objects.equals(repo, that.repo)
                    && Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(repo, success, message);
        }

        @Override
        public String toString() {
            return "BatchResult{repo='" + repo + "', success=" + success + ", message='" + message + "'}";
        }
    }
}
