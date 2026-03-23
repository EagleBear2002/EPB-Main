# epb-main

Java Swing GUI for launching EPB System tools on Windows.

## 快速开始 / Quick Start

### 前置要求 / Requirements

- **Java 17+ JDK** (from https://adoptium.net/temurin/archive/)

### 运行 / Run

```batch
start.bat
```

This script will:

1. Check Java version (17+)
2. Build if needed (requires JDK)
3. Launch the GUI

## 工具配置 / Tool Configuration

Four tool buttons:

- **图片标注** (labelme.exe)
- **数据可视化** (gold-data-visual.exe)
- **EPB 识别** (epb-detection.exe)
- **数据训练** (epb-train.exe)

Place executables in the same directory as `epb.exe`, or select during first run.

## 源代码 / Source Code

```
src/main/java/com/epb/
├── EPBMainApplication.java    # Entry point
├── EPBMainWindow.java         # GUI
├── ProgramLauncher.java       # Program executor  
└── ConfigManager.java         # Configuration
```

## 故障排除 / Troubleshooting

**Q: "javac not found"**

- Install Java 17 JDK (not JRE)
- Add to PATH: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x\bin`
- Restart PowerShell

**Q: Build fails?**

- Ensure JDK 17+ is installed
- Run: `mvn clean package`

## 系统要求 / System Requirements

- Windows 7 or later
- Java 17 or later (JDK for building, JRE for running)
