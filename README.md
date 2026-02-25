# Android AI Skill Bootstrap

Eliminate AI hallucinations in Android development by enforcing strict architectural guardrails.

The **Android AI Skill Bootstrap** is a lightweight CLI tool that fetches and installs "agent skills"—contextual markdown rules and architectural guidelines—directly into your local project. It ensures that your AI agents (Claude, Cursor, Windsurf, etc.) follow your project's specific standards without guessing.

## Features

* **Zero-Configuration:** A standalone Bash script. No Kotlin compiler, Gradle syncs or heavy binaries required.
* **Agent-Aware:** Automatically routes Markdown rules to the correct hidden directories (e.g., `.cursor/rules/`, `.windsurf/rules/`).
* **Version Controlled:** Uses a centralized `versions.json` registry to ensure your local AI rules are always up to date.
* **Deterministic Fallbacks:** Strict error handling for offline environments. No silent failures.

## Why this exists?

AI agents are powerful but prone to hallucinations when they don't have project-specific context. This tool bridges that gap by providing a single command to download verified architectural rules directly into the hidden directories where your agents look for instructions.

## Installation

Run this single command to install `agent-boot` globally on your machine:

```bash
curl -sSL https://raw.githubusercontent.com/Siere/ssf-ai-skills-provider/main/agent-boot -o agent-boot && chmod +x agent-boot && sudo mv agent-boot /usr/local/bin/agent-boot
```

*Note: Requires `curl` and `jq` to be installed on your system.*

## Usage

### 1. Simple Download
Download a skill and let the tool prompt you for the target agent:
```bash
agent-boot download mvi-architecture
```

### 2. Specify Agent via Flag
Bypass the prompt by specifying your agent:
```bash
agent-boot download compose-guidelines --agent=cursor
```

### 3. Update Skills
If a remote skill is updated (new version in `versions.json`), running the download command again will automatically update your local copy.

## Supported AI Agents

The tool automatically maps skills to the correct directory for these agents:

| Agent | CLI Flag | Target Directory |
| :--- | :--- | :--- |
| **Claude Code** | `claude` | `.claude/skills/` |
| **Cursor** | `cursor` | `.cursor/rules/` |
| **Roo Code** | `roo` | `.roo/rules/` |
| **Windsurf** | `windsurf` | `.windsurf/rules/` |
| **Junie** | `junie` | `.junie/` |
| **Gemini CLI** | `gemini` | `.gemini/skills/` |
| ... and many more (see CLI help) | | |

## License

This project is licensed under the MIT License.
