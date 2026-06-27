package com.caicongyang.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class FileUploadHelperTest {

    @Test
    void shouldCreateHelperWithBaseDir(@TempDir Path tempDir) {
        FileUploadHelper helper = new FileUploadHelper(tempDir.toString());
        assertNotNull(helper);
    }
}
