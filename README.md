<a name="readme-top"></a>

<div align="center">

  <h1 align="center">Socslingo</h1>

  <p align="center">
    A comprehensive language learning app with an intuitive interface, customizable flashcard lists, and interactive features designed to enhance your learning experience.
  </p>

</div>

<details>
<summary><kbd>Table of contents</kbd></summary>

#### TOC

- [👋🏻 Getting Started with Socslingo](#-getting-started-with-socslingo)
- [📦 Program Installation](#-program-installation)
- [✨ Feature Overview](#-feature-overview)
    - [`1` Language and Level Selection](#1-language-and-level-selection)
    - [`2` Pre-made Flashcard Lists](#2-pre-made-flashcard-lists)
    - [`3` Online Word Lists](#3-online-word-lists)
    - [`4` Custom Flashcard Lists](#4-custom-flashcard-lists)
    - [`5` List View Management](#5-list-view-management)
    - [`6` Mark as Learned](#6-mark-as-learned)
    - [`7` Randomised Flashcards](#7-randomised-flashcards)
    - [`8` Flashcard Flipping](#8-flashcard-flipping)
    - [`9` Progress Tracking`](#9-progress-tracking)
    - [`10` User Authentication`](#10-user-authentication)
    - [`11` Interactive Quiz Mode`](#11-interactive-quiz-mode)
- [🤝 Contributors](#-contributors)
- [🛠️ Why We Chose Liberica JDK 21 LTS Over Amazon Corretto](#️-why-we-chose-liberica-jdk-21-lts-over-amazon-corretto)
    - [Built-in JavaFX Support](#built-in-javafx-support)
    - [Broad Platform Compatibility](#broad-platform-compatibility)
    - [Cross-Platform Focus](#cross-platform-focus)
- [🔗 Links](#-links)
  - [📜 Credits](#-credits)

####

</details>

## 👋🏻 Getting Started with Socslingo

Welcome to Socslingo! Please note that Socslingo is currently under active development. To get started, follow the installation instructions below to set up the application on your local machine.

## 📦 Program Installation

### Prerequisites

Before installing Socslingo, ensure you have the following installed on your system:

1. **Java Development Kit (JDK) 21 LTS**

   - **Download:** [Liberica JDK 21](https://bell-sw.com/pages/downloads/)
   - **Installation:**
     - **Windows/macOS:** Follow the installer prompts.
     - **Linux:** You can use the following command for Debian-based systems:
       ```bash
       sudo apt update
       sudo apt install liberica-jdk-21-full
       ```

2. **Maven**

   Maven is used for project management and build automation.

   - **Installation:**
     - **Windows/macOS/Linux:** Follow the official [Maven installation guide](https://maven.apache.org/install.html).

3. **Git**

   To clone the Socslingo repository.

   - **Installation:**
     - **Windows/macOS/Linux:** Follow the official [Git installation guide](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/socslingo.git
   cd socslingo
   ```

2. **Install JavaFX SDK**

   Socslingo uses JavaFX for its user interface.

   ```bash
   sudo apt install openjfx
   ```

3. **Build the Project**

   Navigate to the project directory and build using Maven.

   ```bash
   mvn clean install
   ```

4. **Run the Application**

   Use Maven to execute the JavaFX application.

   ```bash
   mvn javafx:run
   ```

   *Alternatively*, you can run the compiled JAR file:

   ```bash
   java -jar target/socslingo-1.0-SNAPSHOT.jar
   ```

### Additional Configuration

- **Fonts and Resources:** Ensure that all font files and images are correctly placed in the `src/main/resources` directory as referenced in the CSS and FXML files.
- **Database Setup:** If Socslingo uses a local SQLite database, ensure that the database file is initialized. Refer to the database setup documentation if available.

## ✨ Feature Overview

### `1` Language and Level Selection
Choose your preferred language and proficiency level to tailor your learning experience.
{ image of feature }

### `2` Pre-made Flashcard Lists
Access a variety of pre-made flashcard lists designed to cover essential vocabulary and phrases.
{ image of feature }

### `3` Online Word Lists
Select from multiple online word lists to expand your vocabulary efficiently.
{ image of feature }

### `4` Custom Flashcard Lists
Create and manage your own custom flashcard lists to focus on areas personalized to your learning needs.
{ image of feature }

### `5` List View Management
Edit, delete, or add flashcards seamlessly within the list view interface.
{ image of feature }

### `6` Mark as Learned
Mark words as learned and track your progress with a dynamic counter that updates in real-time.
{ image of feature }

### `7` Randomised Flashcards
Engage with flashcards presented in a random order to enhance memory retention and recall.
{ image of feature }

### `8` Flashcard Flipping
Interact with your flashcards by clicking to flip and reveal translations, supporting active learning.
{ image of feature }

### `9` Progress Tracking
Monitor the number of words learned overall with intuitive progress indicators.
{ image of feature }

### `10` User Authentication
Register and log in to securely save your progress, ensuring personalized experiences across sessions.
{ image of feature }

### `11` Interactive Quiz Mode
Participate in Kahoot-style quizzes with answer selection for an engaging and competitive learning experience.
{ image of feature }

<div align="right">

[![][back-to-top]](#readme-top)

</div>

## 🤝 Contributors

- **Kwong Yau Kousuke Chan**
- **Ryoya Nagai**
- **Ethan Nordstrom**
- **Celine Gatzias**
- **Tai Tran**

## 🛠️ Why We Chose Liberica JDK 21 LTS Over Amazon Corretto

### Built-in JavaFX Support
Liberica JDK 21 LTS includes JavaFX, simplifying our setup and ensuring seamless integration, whereas Amazon Corretto requires separate management of JavaFX dependencies.

### Broad Platform Compatibility
Liberica JDK offers extensive support for various platforms including Windows (x86 64, x86 32, ARM64) and macOS (ARM and x86), ensuring our app runs smoothly across all targeted environments.

### Cross-Platform Focus
BellSoft’s commitment to cross-platform development makes Liberica the ideal choice for our non-cloud, multi-platform project, allowing us to deliver a consistent user experience.

## 🔗 Links

### 📜 Credits

- **Java:** https://www.java.com/
- **JavaFX:** https://openjfx.io/
- **SQLite:** https://www.sqlite.org/
- **Liberica JDK:** https://bell-sw.com/pages/downloads/
- **Maven:** https://maven.apache.org/
- **Git:** https://git-scm.com/

<div align="right">

[![][back-to-top]](#readme-top)

</div>

[back-to-top]: https://img.shields.io/badge/-BACK_TO_TOP-151515?style=flat-square
[cover]: https://gw.alipayobjects.com/zos/kitchen/8Ab%24hLJ5ur/cover.webp
