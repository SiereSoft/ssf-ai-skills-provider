# Contributing to Android AI Skill Bootstrap

We welcome contributions! Whether it's adding a new AI skill or improving the CLI tool, your help makes Android development better for everyone.

## Contributing a New Skill

Skills are the core of this project. To add a new skill:

1.  **Draft your skill**: Create a markdown file in the `skills/` directory.
    *   Markdown files must be concise, definitive, and formatted specifically for LLM ingestion.
    *   Use XML tags (e.g., `<rule>`, `<example>`) or clear bullet points to define instructions.
2.  **Update `versions.json`**: Add your skill to the registry with a unique name and version.
3.  **Submit a Pull Request**: Our CI will automatically validate that your skill file exists and the JSON is correctly formatted.

## Improving the CLI Tool

To improve the `ssf-provider` script:

1.  **Strict Requirements**: PRs must be pure Kotlin Script (`.main.kts`), have zero external dependencies other than standard libraries (and `kotlinx-serialization-json`), and pass POSIX terminal standards.
2.  **No Side Effects**: The system must not modify project build files (`build.gradle`, etc.) or native source code. It only manages markdown skills.
3.  **Test your changes**: Before submitting, ensure the script still handles interactive and non-interactive downloads correctly.

## Code of Conduct

Please follow our [Code of Conduct](CODE_OF_CONDUCT.md) in all interactions.
