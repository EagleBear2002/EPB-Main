package com.epb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for launching external programs
 */
public class ProgramLauncher {

    /**
     * Launch an external program
     * @param programPath Full path to the executable or script
     * @param displayName Name of the program for logging purposes
     */
    public void launchProgram(String programPath, String displayName) {
        if (programPath == null || programPath.trim().isEmpty()) {
            System.err.println("Program path is empty or null");
            return;
        }

        File programFile = new File(programPath);
        if (!programFile.exists()) {
            System.err.println("Program not found: " + programPath);
            return;
        }

        try {
            ProcessBuilder processBuilder;
            
            // Determine OS and build command accordingly
            String osName = System.getProperty("os.name").toLowerCase();
            
            if (osName.contains("win")) {
                // Windows
                processBuilder = new ProcessBuilder(programPath);
            } else if (osName.contains("mac")) {
                // macOS
                processBuilder = new ProcessBuilder("open", programPath);
            } else {
                // Linux and others
                processBuilder = new ProcessBuilder(programPath);
            }

            // Prevent child process from blocking when stdout/stderr buffers fill up.
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);

            // Set working directory to program's location
            processBuilder.directory(programFile.getParentFile());

            // Start the process in background (don't wait for it to finish)
            Process process = processBuilder.start();
            System.out.println("Launched " + displayName + " (PID: " + process.pid() + ")");

        } catch (IOException e) {
            System.err.println("Failed to launch " + displayName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Launch a Python script
     * @param scriptPath Full path to the Python script
     * @param displayName Name of the program for logging purposes
     */
    public void launchPythonScript(String scriptPath, String displayName) {
        if (scriptPath == null || scriptPath.trim().isEmpty()) {
            System.err.println("Script path is empty or null");
            return;
        }

        File scriptFile = new File(scriptPath);
        if (!scriptFile.exists()) {
            System.err.println("Script not found: " + scriptPath);
            return;
        }

        try {
            List<String> command = new ArrayList<>();
            String osName = System.getProperty("os.name").toLowerCase();

            if (osName.contains("win")) {
                command.add("python");
            } else {
                command.add("python3");
            }
            command.add(scriptPath);

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(scriptFile.getParentFile());
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);

            Process process = processBuilder.start();
            System.out.println("Launched Python script " + displayName + " (PID: " + process.pid() + ")");

        } catch (IOException e) {
            System.err.println("Failed to launch Python script " + displayName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
