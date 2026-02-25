# Agent-Grade Specification: Android AI Skill Bootstrap

## System Overview
The Android AI Skill Bootstrap is a lightweight, open-source CLI tool designed for Android developers to fetch and install AI agent "skills" (contextual guardrails and architectural rules) from a central GitHub repository. It bridges the gap between standardized Android development practices and autonomous AI agents by automatically placing downloaded markdown rules into the specific local directories required by different AI models. It uses a centralized remote versions.json registry to handle precise version control and updates.

## Behavioral Contract
*   When the user executes the download command (e.g., `./agent-boot download <skill-name>`) without specifying an agent flag, the system interactively prompts the user to select their target AI agent from the supported registry to determine the correct local directory path.
*   When the user executes the command with the agent flag (e.g., `./agent-boot download mvi-architecture --agent=cursor`), the system bypasses the interactive prompt and uses the specified agent.
*   When a valid skill name and agent are provided, the system first fetches `versions.json` from the remote GitHub repository to check the current version of the requested skill.
*   When fetching a new skill, the system downloads the corresponding raw markdown file from the remote GitHub repository, saves it strictly in `.md` format into the targeted agent's local folder, and updates a local tracking file (e.g., `.agent-boot-versions.json`) with the installed version.
*   When the requested skill already exists locally, the system compares the version in the remote `versions.json` against the local tracking file. It overwrites the local `.md` file and updates the local tracking file only if the remote version is strictly newer.
*   When the remote version is identical to (or older than) the local version, the system skips the download and outputs a "Skill <name> is already up to date" message.
*   When the network is unreachable, GitHub is unavailable, or a request times out, the system safely halts and outputs a standardized error message (e.g., `ERROR: Network unreachable. Cannot fetch skills.`).

## Explicit Non-Behaviors
*   The system must not modify `build.gradle`, `settings.gradle`, or any native Android source code files. Its strict boundary is to act only as a skill/rules provider, not a code generator.
*   The system must not attempt to execute, compile, or validate the contents of the downloaded skills.
*   The system must not silently fail or fallback to cached local versions when the network is down. Deterministic, explicitly visible network errors are required for terminal-monitoring agents to understand why a fetch failed.
*   The system must not guess or invent directory paths for agents not listed in the supported registry. If an unknown agent is passed, it must fail explicitly with an "Unsupported agent" error.

## Integration Boundaries

### 1. Remote GitHub Repository
*   **Data In:** `versions.json` (a key-value map of skill names to version strings/numbers) and Markdown text files (`.md`).
*   **Data Out:** HTTP GET requests.
*   **Failure State:** If the repository returns a 404 (skill or `versions.json` not found), 403 (rate limited), or if the network is disconnected, the system must immediately terminate the current operation and print a distinct, parseable error message to stdout/stderr.
*   **Development Twin:** During agent development, the system should point to a local mock server or a designated testing branch/folder in GitHub to avoid rate limits.

### 2. Local File System & Target Agents
*   **Data In:** Local read operations of `.agent-boot-versions.json` to check existing installed versions.
*   **Data Out:** Folder creation (if the target agent directory does not exist), file write operations for the `.md file, and updates to `.agent-boot-versions.json`.
*   **Failure State:** If the system lacks write permissions for the project root, it must output `ERROR: Insufficient directory permissions`.

## Supported Agent Registry

| Agent Name | CLI Flag (`--agent=`) | Target Directory |
| :--- | :--- | :--- |
| Claude Code | `claude` | `.claude/skills/` |
| Cursor | `cursor` | `.cursor/rules/` |
| OpenCode | `opencode` | `.opencode/skills/` |
| Roo Code | `roo` | `.roo/rules/` |
| Kilo Code | `kilo` | `.kilocode/rules/` |
| Windsurf | `windsurf` | `.windsurf/rules/` |
| Warp | `warp` | `.warp/rules/` |
| Zencoder | `zencoder` | `.zencoder/rules/` |
| Codex CLI | `codex` | `.codex/skills/` |
| Gemini CLI | `gemini` | `.gemini/skills/` |
| Antigravity | `antigravity` | `.agent/skills/` |
| Junie | `junie` | `.junie/` |

## Implementation Constraints
*   The tool must be implemented as a lightweight, standalone script (e.g., Bash/Zsh, PowerShell, or a standalone Kotlin Script `.main.kts`) that executes via standard POSIX-compliant CLI syntax without requiring a full Gradle sync.
*   It must run seamlessly on standard Android development environments (macOS, Linux, and Windows).
