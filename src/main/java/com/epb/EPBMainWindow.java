package com.epb;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * epb-main Main Window
 * Provides a GUI interface to launch various EPB tools
 */
public class EPBMainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String WINDOW_TITLE = "epb-main";
    private static final int WINDOW_WIDTH = 560;
    private static final int WINDOW_HEIGHT = 600;

    private static final Color APP_BACKGROUND = new Color(238, 242, 249);
    private static final Color TITLE_TEXT = new Color(15, 35, 72);
    private static final Color SUBTITLE_TEXT = new Color(70, 84, 107);
    private static final Color BUTTON_BG = new Color(12, 76, 166);
    private static final Color BUTTON_HOVER_BG = new Color(7, 63, 140);
    private static final Color BUTTON_PRESSED_BG = new Color(6, 52, 118);
    private static final Color BUTTON_BORDER = new Color(6, 53, 120);
    private static final Color CARD_BORDER = new Color(198, 210, 228);

    private static final Map<String, String> DEFAULT_RELATIVE_PATHS = Map.of(
            "labelme.exe", "..\\Labelme.exe",
            "gold-data-visual.exe", "..\\gold-data-visual\\dist\\system\\sysytem.exe",
            "epb-detection.exe", "..\\epb-detection\\dist\\epb-system\\epb-system.exe",
            "epb-train.exe", "..\\epb-train\\dist\\*.exe"
    );

    private Map<String, String> programPaths;
    private ProgramLauncher launcher;
    private ConfigManager configManager;

    public EPBMainWindow() {
        initializeUI();
        initializeProgramPaths();
    }

    private void initializeUI() {
        // Set modern Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        applyGlobalFont(createDisplayFont(Font.PLAIN, 14));

        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 16));
        mainPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        mainPanel.setBackground(APP_BACKGROUND);

        // Title panel
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = createButtonsPanel();
        mainPanel.add(buttonsPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel titleLabel = new JLabel("EPB System");
        titleLabel.setFont(createDisplayFont(Font.BOLD, 28));
        titleLabel.setForeground(TITLE_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(titleLabel);

        titlePanel.add(Box.createVerticalStrut(6));

        JLabel subtitleLabel = new JLabel("一站式工具主菜单");
        subtitleLabel.setFont(createDisplayFont(Font.PLAIN, 14));
        subtitleLabel.setForeground(SUBTITLE_TEXT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(subtitleLabel);

        return titlePanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(4, 1, 0, 14));
        buttonsPanel.setBackground(APP_BACKGROUND);

        // Button definitions: [display_name, exe_name, trial_status]
        String[][] buttonConfigs = {
                {"图片标注", "labelme.exe", ""},
                {"数据可视化", "gold-data-visual.exe", ""},
                {"EPB 识别", "epb-detection.exe", ""},
                {"数据训练", "epb-train.exe", "【试用】"}
        };

        for (String[] config : buttonConfigs) {
            JButton button = createStyledButton(config[0], config[1], config[2]);
            buttonsPanel.add(button);
        }

        return buttonsPanel;
    }

    private JButton createStyledButton(String displayName, String exeName, String trial) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel model = getModel();
                Color fillColor = BUTTON_BG;
                int shadowOffset = 3;
                
                if (model.isPressed()) {
                    fillColor = BUTTON_PRESSED_BG;
                    shadowOffset = 1;
                } else if (model.isRollover()) {
                    fillColor = BUTTON_HOVER_BG;
                }

                // Draw subtle shadow when not pressed
                if (!model.isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 15));
                    g2.fillRoundRect(shadowOffset, shadowOffset, getWidth() - shadowOffset, getHeight() - shadowOffset, 14, 14);
                }

                // Draw main button background
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, getWidth() - shadowOffset, getHeight() - shadowOffset, 14, 14);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel model = getModel();
                int shadowOffset = model.isPressed() ? 1 : 3;
                g2.setColor(BUTTON_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - shadowOffset - 1, getHeight() - shadowOffset - 1, 14, 14);
                g2.dispose();
            }
        };

        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        button.setFont(createDisplayFont(Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setRolloverEnabled(true);
        button.setPreferredSize(new Dimension(0, 86));
        button.setBorder(new EmptyBorder(10, 12, 10, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add main label with vertical centering
        button.add(Box.createVerticalGlue());
        
        JLabel mainLabel = createShadowedLabel(displayName, createDisplayFont(Font.BOLD, 22), Color.WHITE);
        button.add(mainLabel);

        // Add trial label if applicable
        if (!trial.isEmpty()) {
            JLabel trialLabel = createShadowedLabel(trial, createDisplayFont(Font.PLAIN, 12), new Color(255, 244, 191));
            button.add(trialLabel);
        } else {
            button.add(Box.createVerticalStrut(8));
        }
        
        button.add(Box.createVerticalGlue());

        button.addActionListener(e -> launchProgram(displayName, exeName));
        return button;
    }

    private JLabel createShadowedLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                FontMetrics fm = g2.getFontMetrics(font);
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                // Draw subtle shadow
                g2.setColor(new Color(0, 0, 0, 80));
                g2.setFont(font);
                g2.drawString(text, x + 1, y + 1);

                // Draw main text
                g2.setColor(color);
                g2.setFont(font);
                g2.drawString(text, x, y);

                g2.dispose();
            }
        };
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private Font createDisplayFont(int style, int size) {
        String sample = "图片标注数据训练试用";
        String[] candidates = {
                "Microsoft YaHei UI",
                "Microsoft YaHei",
                "SimHei",
                "SimSun",
                "Noto Sans CJK SC",
                "Dialog"
        };

        for (String name : candidates) {
            Font font = new Font(name, style, size);
            if (font.canDisplayUpTo(sample) == -1) {
                return font;
            }
        }

        return new Font("Dialog", style, size);
    }

    private void applyGlobalFont(Font baseFont) {
        FontUIResource resource = new FontUIResource(baseFont);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, resource);
            }
        }
    }

    private void initializeProgramPaths() {
        configManager = new ConfigManager();
        programPaths = new HashMap<>();
        launcher = new ProgramLauncher();

        // Map program names to executable names
        String[] programs = {"labelme.exe", "gold-data-visual.exe", "epb-detection.exe", "epb-train.exe"};
        String appDir = new File(System.getProperty("user.dir")).getAbsolutePath();

        for (String program : programs) {
            // First, check if path is saved in config
            String savedPath = configManager.getProgramPath(program);
            if (savedPath != null && new File(savedPath).exists()) {
                programPaths.put(program, savedPath);
                continue;
            }

            // Resolve from fixed relative defaults
            String resolvedPath = resolveDefaultProgramPath(program, appDir);
            if (resolvedPath != null) {
                programPaths.put(program, resolvedPath);
                configManager.saveProgramPath(program, resolvedPath);
            }
        }
    }

    private String resolveDefaultProgramPath(String programName, String appDir) {
        String relativePath = DEFAULT_RELATIVE_PATHS.get(programName);
        if (relativePath == null || relativePath.isBlank()) {
            return null;
        }

        if (relativePath.contains("*")) {
            return resolveWildcardPath(relativePath, appDir);
        }

        File file = new File(appDir, relativePath);
        return file.exists() ? file.getAbsolutePath() : null;
    }

    private String resolveWildcardPath(String wildcardRelativePath, String appDir) {
        String normalized = wildcardRelativePath
                .replace("/", File.separator)
                .replace("\\", File.separator);

        int wildcardIndex = normalized.indexOf('*');
        if (wildcardIndex < 0) {
            File file = new File(appDir, normalized);
            return file.exists() ? file.getAbsolutePath() : null;
        }

        int separatorIndex = normalized.lastIndexOf(File.separator, wildcardIndex);
        String folderPart = separatorIndex >= 0 ? normalized.substring(0, separatorIndex) : ".";
        String suffix = normalized.substring(wildcardIndex + 1).toLowerCase();

        File folder = new File(appDir, folderPart);
        if (!folder.exists() || !folder.isDirectory()) {
            return null;
        }

        File[] matches = folder.listFiles((dir, name) -> {
            String lowerName = name.toLowerCase();
            return suffix.isEmpty() || lowerName.endsWith(suffix);
        });

        if (matches == null || matches.length == 0) {
            return null;
        }

        Arrays.sort(matches, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return matches[0].getAbsolutePath();
    }

    private void launchProgram(String displayName, String exeName) {
        String path = programPaths.get(exeName);

        if (path == null || path.isEmpty() || !new File(path).exists()) {
            String appDir = new File(System.getProperty("user.dir")).getAbsolutePath();
            String resolvedPath = resolveDefaultProgramPath(exeName, appDir);
            if (resolvedPath != null) {
                path = resolvedPath;
                programPaths.put(exeName, resolvedPath);
                configManager.saveProgramPath(exeName, resolvedPath);
            }
        }

        if (path == null || path.isEmpty()) {
            // Ask user to select the executable
            int result = showProgramNotFoundDialog(displayName, exeName);
            if (result == JFileChooser.APPROVE_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Executable files (*.exe)", "exe"));
                result = fileChooser.showOpenDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    path = selectedFile.getAbsolutePath();
                    programPaths.put(exeName, path);
                    // Save the selected path
                    configManager.saveProgramPath(exeName, path);
                }
            }
        }

        if (path != null && !path.isEmpty()) {
            launcher.launchProgram(path, displayName);
        }
    }

    private int showProgramNotFoundDialog(String displayName, String exeName) {
        String message = String.format(
                "未找到 %s (%s)。\n" +
                "请选择程序位置。",
                displayName, exeName
        );

        return JOptionPane.showConfirmDialog(
                this,
                message,
                "程序未找到",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
    }
}
