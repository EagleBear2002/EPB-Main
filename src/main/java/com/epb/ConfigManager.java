package com.epb;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration manager for EPB System GUI
 * Handles saving and loading program paths
 */
public class ConfigManager {
    private static final String CONFIG_FILE_NAME = "epb-gui.properties";
    private static final String PROGRAMS_PREFIX = "program.";

    private Properties properties;
    private File configFile;

    public ConfigManager() {
        this.properties = new Properties();
        this.configFile = new File(System.getProperty("user.home"), CONFIG_FILE_NAME);
        loadConfig();
    }

    /**
     * Load configuration from file
     */
    private void loadConfig() {
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
                System.out.println("Configuration loaded from: " + configFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to load configuration: " + e.getMessage());
            }
        }
    }

    /**
     * Save configuration to file
     */
    public void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "EPB System GUI Configuration");
            System.out.println("Configuration saved to: " + configFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }

    /**
     * Save program path
     * @param programName Name of the program
     * @param path Path to the program
     */
    public void saveProgramPath(String programName, String path) {
        properties.setProperty(PROGRAMS_PREFIX + programName, path);
        saveConfig();
    }

    /**
     * Get program path
     * @param programName Name of the program
     * @return Path to the program or null if not found
     */
    public String getProgramPath(String programName) {
        return properties.getProperty(PROGRAMS_PREFIX + programName);
    }

    /**
     * Get all program paths
     * @return Map of program names to paths
     */
    public Map<String, String> getAllProgramPaths() {
        Map<String, String> paths = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(PROGRAMS_PREFIX)) {
                String programName = key.substring(PROGRAMS_PREFIX.length());
                paths.put(programName, properties.getProperty(key));
            }
        }
        return paths;
    }
}
