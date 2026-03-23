package com.epb;

import javax.swing.*;

/**
 * epb-main Application Entry Point
 */
public class EPBMainApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EPBMainWindow mainWindow = new EPBMainWindow();
            mainWindow.setVisible(true);
        });
    }
}
