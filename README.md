# SSF AI Skill Provider

Eliminate AI hallucinations in Android development by enforcing strict architectural guardrails.

The **SSF AI Skill Provider** is a lightweight CLI tool that fetches and installs "agent skills"—contextual markdown rules and architectural guidelines—directly into your local project. It ensures that your AI agents (Claude, Cursor, Windsurf, etc.) follow your project's specific standards without guessing.

## Features

* **Zero-Configuration:** A standalone Bash script. No Kotlin compiler, Gradle syncs or heavy binaries required.
* **Agent-Aware:** Automatically routes Markdown rules to the correct hidden directories (e.g., `.cursor/rules/`, `.windsurf/rules/`).
* **Native MCP Integration:** Natively speaks JSON-RPC 2.0 over stdio, allowing agents to execute tools directly (e.g., `download_skill`) rather than just reading documentation.
* **Token Economy & Optimization:** Optional auto-minification of Markdown rules to reduce context window overhead and token costs.
* **Integrated Security:** Built-in validation layer and skill auditing to ensure downloaded scripts are safe and follow organizational standards.
* **Version Controlled:** Uses a centralized `versions.json` registry to ensure your local AI rules are always up to date.
* **Deterministic Fallbacks:** Strict error handling for offline environments. No silent failures.

## Why this exists?

AI agents are powerful but prone to hallucinations when they don't have project-specific context. This tool bridges that gap by providing a single command to download verified architectural rules directly into the hidden directories where your agents look for instructions.

## Installation

Run this single command to install `ssf-agent` globally on your machine:

```bash
curl -sSL https://raw.githubusercontent.com/SiereSoft/ssf-ai-skills-provider/develop/ssf-agent -o ssf-agent && chmod +x ssf-agent && sudo mv ssf-agent /usr/local/bin/ssf-agent
```

*Note: Requires `curl` to be installed on your system.*

## Usage

### 1. Configure Default Agents
Set up your preferred AI agents once to avoid repeated prompts:
```bash
ssf-agent select
```

### 2. Simple Download
Download a skill to your configured agents:
```bash
ssf-agent download mvi-architecture
```

### 3. Specify Agent(s) via Flag
Bypass the configuration by specifying one or more agents (comma-separated):
```bash
ssf-agent download compose-guidelines --agent=cursor,claude
```

### 4. Update Skills
If a remote skill is updated (new version in `versions.json`), running the download command again will automatically update your local copy for all configured agents.

### 5. Self-Update
Keep `ssf-agent` itself up to date:
```bash
ssf-agent update
```

### 6. MCP Server Mode
Because `ssf-agent` now natively speaks JSON-RPC over stdio, you can plug it directly into tools like Claude Desktop or Cursor as an MCP (Model Context Protocol) server.

#### For Claude Desktop
Add the script to your `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "ssf-provider": {
      "command": "/usr/local/bin/ssf-agent",
      "args": ["mcp"]
    }
  }
}
```

#### For Cursor
1. Go to **Cursor Settings** > **Features** > **MCP**.
2. Click **+ Add New MCP Server**.
3. Name: `ssf-provider`
4. Type: `command`
5. Command: `/usr/local/bin/ssf-agent mcp`

## Supported AI Agents

The tool automatically maps skills to the correct directory for these agents:

| Agent | CLI Flag | Target Directory |
| :--- | :--- | :--- |
| **Claude Code** | `claude` | `.claude/skills/` |
| **Claude Code (alt)** | `claude-code` | `.claude/skills/` |
| **Cursor** | `cursor` | `.cursor/rules/` |
| **OpenCode** | `opencode` | `.opencode/skills/` |
| **Roo Code** | `roo` | `.roo/rules/` |
| **Kilo** | `kilo` | `.kilocode/rules/` |
| **Windsurf** | `windsurf` | `.windsurf/rules/` |
| **Warp** | `warp` | `.warp/rules/` |
| **Zencoder** | `zencoder` | `.zencoder/rules/` |
| **Codex** | `codex` | `.codex/skills/` |
| **Junie** | `junie` | `.junie/` |
| **Gemini CLI** | `gemini` | `.gemini/skills/` |
| **Antigravity** | `antigravity` | `.agent.antigravity/skills/` |
| ... and many more (see CLI help) | | |

## License

This project is licensed under the MIT License.
