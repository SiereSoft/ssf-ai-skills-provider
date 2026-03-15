# SSF AI Skill Provider

Eliminate AI hallucinations in Android development by enforcing strict architectural guardrails.

The **SSF AI Skill Provider** is a lightweight CLI tool that fetches and installs "agent skills"—contextual markdown rules and architectural guidelines—directly into your local project. It ensures that your AI agents (Claude, Cursor, Windsurf, etc.) follow your project's specific standards without guessing.

## Features

* **Zero-Sudo / Rootless:** Automatically migrates to user space (`~/.local/bin`) if installed in a system folder, ensuring future updates never require root privileges.
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

### Method 1: Rootless (Recommended)
Install `ssf-provider` directly into your user-space binary folder. This avoids the need for `sudo` now and in the future:

```bash
mkdir -p ~/.local/bin && curl -sSL https://raw.githubusercontent.com/SiereSoft/ssf-ai-skills-provider/main/ssf-provider -o ~/.local/bin/ssf-provider && chmod +x ~/.local/bin/ssf-provider
```
*Note: Ensure `~/.local/bin` is in your `PATH`.*

### Method 2: Global
If you prefer a global installation, you can still use the traditional method. `ssf-provider` will automatically offer to migrate to user-space during its first update to eliminate `sudo` friction:

```bash
curl -sSL https://raw.githubusercontent.com/SiereSoft/ssf-ai-skills-provider/main/ssf-provider -o ssf-provider && chmod +x ssf-provider && sudo mv ssf-provider /usr/local/bin/ssf-provider
```

*Note: Requires `curl` to be installed on your system.*

## Usage

### 1. Configure Default Agents
Set up your preferred AI agents once to avoid repeated prompts:
```bash
ssf-provider select
```

### 2. Simple Download
Download a skill to your configured agents:
```bash
ssf-provider download mvi-architecture
# or download the minified version to save tokens
ssf-provider download mvi-architecture --minified
```

### 3. Specify Agent(s) via Flag
Bypass the configuration by specifying one or more agents (comma-separated):
```bash
ssf-provider download compose-guidelines --agent=cursor,claude
```

### 4. Update Skills
If a remote skill is updated (new version in `versions.json`), running the download command again will automatically update your local copy for all configured agents.

### 5. Self-Update
Keep `ssf-provider` itself up to date:
```bash
ssf-provider update
```

### 6. MCP Server Mode
Because `ssf-provider` now natively speaks JSON-RPC over stdio, you can plug it directly into tools like Claude Desktop or Cursor as an MCP (Model Context Protocol) server.

#### For Claude Desktop
Add the script to your `claude_desktop_config.json`. We recommend using the rootless path:

```json
{
  "mcpServers": {
    "ssf-provider": {
      "command": "/Users/YOUR_USER/.local/bin/ssf-provider",
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
5. Command: `/Users/YOUR_USER/.local/bin/ssf-provider mcp`

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

## Minified skills and Git pre-commit hook

### Download a minified version of a skill
You can ask `ssf-provider` to fetch the pre-minified variant of a skill (when available) using the `--minified` flag:

```bash
ssf-provider download mvi-architecture --minified
# or the short form
ssf-provider download mvi-architecture -m
```

- You can combine this with `--agent`:
```bash
ssf-provider download compose-guidelines --minified --agent=cursor,claude
```
- If a minified file does not exist yet for a given skill, the tool will print a hint to retry without `--minified`.
- Even when downloading a minified file, it is saved locally using the standard filename from the registry (for example `mvi-architecture.md`).

### Enable auto-minification on commit (Git hook)
A lightweight pre-commit hook is included to automatically generate and stage `*.min.md` files for any changed skills in `skills/`.

1) Install the hook (one-time per repo clone):
```bash
npm run setup
```
This copies `scripts/pre-commit` into `.git/hooks/pre-commit` and makes it executable.

2) Commit as usual. When you stage and commit skill Markdown files (e.g. `skills/new-guideline.md`), the hook will:
- Generate or update the corresponding `skills/new-guideline.min.md` using `scripts/minify.mjs`.
- Automatically `git add` the new/updated `*.min.md` so it is included in the same commit.
- Abort the commit if minification fails.

Notes:
- The minifier currently warns when a minified skill exceeds the token budget of 500 tokens. Consider splitting the skill if you see a warning.
- Already-minified files (`*.min.md`) are ignored by the hook.

### Manually run the minifier
If you want to run the minifier yourself (outside of Git hooks):
```bash
node scripts/minify.mjs skills/mvi-architecture.md
# You can pass multiple files as arguments
node scripts/minify.mjs skills/mvi-architecture.md skills/compose-guidelines.md
```
This will write/update `*.min.md` files next to the originals and stage them if run inside a Git repository.

## License

This project is licensed under the MIT License.
