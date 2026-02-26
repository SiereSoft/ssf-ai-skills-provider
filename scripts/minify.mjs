import pkg from 'tiktoken';
import fs from 'fs';
import { execSync } from 'child_process';

// 1. Safely handle the CommonJS import and initialize the tokenizer
const encodeText = (text) => {
    // Some versions export encode directly
    if (pkg.encode) return pkg.encode(text);
    // Standard versions require getting the specific model encoding
    if (pkg.get_encoding) {
        const enc = pkg.get_encoding("cl100k_base");
        return enc.encode(text);
    }
    // Safe fallback just in case the library fails to load properly
    return { length: Math.ceil(text.length / 4) };
};

const BUDGET = 500;

function minifySkill(markdown) {
    return markdown
        // Remove HTML/Markdown comments (encoded so the chat window doesn't hide it)
        .replace(/\x3C!--[\s\S]*?--\x3E/g, '')
        // Replace conversational boilerplate
        .replace(/Please make sure to /gi, 'Must ')
        .replace(/It is highly recommended that you /gi, 'Should ')
        .replace(/I want you to /gi, 'You must ')
        // Remove excessive newlines
        .replace(/\n{3,}/g, '\n\n')
        // Trim trailing spaces
        .replace(/[ \t]+$/gm, '')
        .trim();
}

function processFiles(files) {
    let hasErrors = false;

    files.forEach(filePath => {
        // Skip files that are already minified or aren't in the skills/ folder
        if (!filePath.endsWith('.md') || filePath.endsWith('.min.md') || !filePath.includes('skills/')) {
            return;
        }

        console.log(`\n🤖 Analyzing ${filePath}...`);

        try {
            const rawMarkdown = fs.readFileSync(filePath, 'utf-8');
            const optimizedContent = minifySkill(rawMarkdown);

            // 2. Use our safe encoding wrapper
            const tokenCount = encodeText(optimizedContent).length;
            const minFilePath = filePath.replace(/\.md$/, '.min.md');

            // Write the minified file
            fs.writeFileSync(minFilePath, optimizedContent, 'utf-8');

            // Stage the new .min.md file so it gets committed alongside the original
            execSync(`git add "${minFilePath}"`);

            if (tokenCount > BUDGET) {
                console.warn(`⚠️ [WARNING] Context Bloat in: ${minFilePath}`);
                console.warn(`   Tokens: ${tokenCount} / Budget: ${BUDGET}`);
                console.warn(`   Action: Consider splitting this skill into smaller domains.`);
            } else {
                console.log(`✅ Optimized ${minFilePath} (${tokenCount} tokens).`);
            }
        } catch (error) {
            console.error(`❌ Error processing ${filePath}:`, error.message);
            hasErrors = true;
        }
    });

    if (hasErrors) process.exit(1);
}

// Get files passed as arguments from the pre-commit hook
const filesToProcess = process.argv.slice(2);
if (filesToProcess.length > 0) {
    processFiles(filesToProcess);
}