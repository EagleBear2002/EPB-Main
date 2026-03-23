package com.epb;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for ProgramLauncher
 */
public class ProgramLauncherTest {

    @Test
    public void testProgramLauncherExists() {
        ProgramLauncher launcher = new ProgramLauncher();
        assertNotNull(launcher);
    }

    @Test
    public void testInvalidPathHandling() {
        ProgramLauncher launcher = new ProgramLauncher();
        // Should handle gracefully without throwing exceptions
        launcher.launchProgram("invalid/path/program.exe", "Test Program");
    }

    @Test
    public void testEmptyPathHandling() {
        ProgramLauncher launcher = new ProgramLauncher();
        // Should handle gracefully
        launcher.launchProgram("", "Empty Path");
        launcher.launchProgram(null, "Null Path");
    }
}
