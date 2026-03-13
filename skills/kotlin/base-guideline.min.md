# Junie Project Guidelines

When working on this project, follow these rules:

## Planning Process

1. **When given a task:**
   - Create a detailed plan first
   - Save the plan to `JUNIE.md` (the main file)
   - **Stop after saving** and wait for the explicit prompt to continue
   - Ask for explicit approval before implementing
   - After finishing a task clear the `JUNIE.md` file

2. **Once approved:**
   - Execute the plan without asking for secondary confirmations unless you need to execute something with terminal then you can ask as normal
   - Proceed through implementation phases systematically

## Special Cases

- **Time-intensive tasks:** Ask for explicit approval before starting implementation
- **Immediate implementation:** Some tasks will be explicitly stated as "implement right away" in the prompt
- **Skip planning:** Only skip the planning phase if explicitly told "go straight to implementation" or similar wording
- **Do not modify `BaseViewModel` or `RootScreen`**

## Core Principles
- **Language:** Always use Kotlin for Android development.
- **Tone:** Be concise and technical.
- **Framework:** Kotlin + Compose Multiplatform (targeting Android, iOS).
- **Language:** Kotlin (Strictly).
- **Paradigm:** Declarative UI, Unidirectional Data Flow.

## Documentation Index
Please refer to the specific documentation files in this directory for detailed rules:

1. **Architecture & Design:**
    - For questions regarding project structure, MVVM, or design patterns, strictly follow the rules defined in `.junie/architecture.md`.

2. **Coding Standards:**
    - For development, coding practices, and Kotlin-specific idioms, refer to `.junie/development.md`.

3. **Testing:**
    - When writing unit or UI tests, use the libraries and patterns defined in `.junie/testing.md`.

4. **App design**
    - When working UI elements, components or screens refer to `.junie/design.md`

## Universal Rules
- Always check `.junie/development.md` before refactoring legacy code.
- If you are unsure about the layer a file belongs to, check "File Organization" in `.junie/architecture.md`.
- If you are unsure how to name a class or function, refer to the existing examples to get the analogy.