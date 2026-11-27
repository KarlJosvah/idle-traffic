# 🚗 Idle-Traffic-Naga

![Status: WIP](https://img.shields.io/badge/status-work_in_progress-orange?style=for-the-badge)
![Stage: Alpha](https://img.shields.io/badge/stage-alpha-red?style=for-the-badge)

> [!WARNING]
> **Active Development / Work in Progress** — Architecture and features are actively evolving.

---

## 🌟 Overview

---

## ✨ Key Features

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK):** Version 8 or higher.
- **Engine Library:** Download `naga-engine.jar` from the [java-naga-2d-engine](https://github.com/KarlJosvah/java-naga-2d-engine.git) releases and place it inside the `lib/` directory.

### Dependency Setup (Maven Central Dependencies)

Since `.jar` files in `lib/` are excluded via `.gitignore`, run the following `curl` commands to download the required Maven Central dependencies into `lib/`:

```bash
# Download Batik & XMLGraphics dependencies into lib/ (--create-dirs creates lib if missing)
curl --create-dirs -Lo lib/batik-all-1.19.jar https://repo1.maven.org/maven2/org/apache/xmlgraphics/batik-all/1.19/batik-all-1.19.jar
curl --create-dirs -Lo lib/xml-apis-1.4.01.jar https://repo1.maven.org/maven2/xml-apis/xml-apis/1.4.01/xml-apis-1.4.01.jar
curl --create-dirs -Lo lib/xml-apis-ext-1.3.04.jar https://repo1.maven.org/maven2/xml-apis/xml-apis-ext/1.3.04/xml-apis-ext-1.3.04.jar
curl --create-dirs -Lo lib/xmlgraphics-commons-2.11.jar https://repo1.maven.org/maven2/org/apache/xmlgraphics/xmlgraphics-commons/2.11/xmlgraphics-commons-2.11.jar
```

### Running the Application

Once dependencies are placed in `lib/`, run the project using the build/run script or commands derived from `run.bat.example`:

1. Copy `run.bat.example` to `run.bat`:

    ```cmd
    copy run.bat.example run.bat
    ```

2. Execute the script:
    ```cmd
    run.bat
    ```

---

## 🛠️ Project Structure

```text
idle-traffic/
├── lib/                    # External library dependencies (.jar) - excluded via .gitignore
├── src/
│   ├── core/               # Game state management (PlayState, etc.)
│   ├── game/
│   │   └── traffic/
│   │       ├── entity/     # Board, Tile, Piece entity definitions
│   │       └── helpers/    # Layout builders and rect math
│   └── tools/              # Utility helpers
├── run.bat.example         # Template script for compiling and running
└── README.md
```
