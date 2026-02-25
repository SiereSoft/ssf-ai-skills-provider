#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.exitProcess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

val agentRegistry = mapOf(
    "claude" to ".claude/skills/",
    "claude-code" to ".claude/skills/",
    "cursor" to ".cursor/rules/",
    "opencode" to ".opencode/skills/",
    "roo" to ".roo/rules/",
    "kilo" to ".kilocode/rules/",
    "windsurf" to ".windsurf/rules/",
    "warp" to ".warp/rules/",
    "zencoder" to ".zencoder/rules/",
    "codex" to ".codex/skills/",
    "gemini" to ".gemini/skills/",
    "antigravity" to ".agent/skills/",
    "junie" to ".junie/"
)

val localTrackingFile = ".agent-boot-versions.json"
// For development/testing, can be overridden by environment variable
val remoteBaseUrl = System.getenv("AGENT_BOOT_REMOTE_URL") ?: "https://raw.githubusercontent.com/Siere/ssf-ai-skills-provider/main/"

@Serializable
data class SkillInfo(
    val version: String,
    val description: String? = null,
    val file: String
)

@Serializable
data class RemoteRegistry(
    val skills: Map<String, SkillInfo>
)

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printUsage()
        return
    }

    when (args[0]) {
        "download" -> handleDownload(args.drop(1))
        else -> {
            println("Unknown command: ${args[0]}")
            printUsage()
            exitProcess(1)
        }
    }
}

fun printUsage() {
    println("Usage: ./agent-boot download <skill-name> [--agent=<agent>]")
    println("Supported agents: ${agentRegistry.keys.joinToString(", ")}")
}

fun handleDownload(args: List<String>) {
    if (args.isEmpty()) {
        printUsage()
        exitProcess(1)
    }

    val skillName = args[0]
    var agentName = args.find { it.startsWith("--agent=") }?.substringAfter("--agent=")

    if (agentName == null) {
        agentName = promptForAgent()
    }

    val agentNameLower = agentName.lowercase()
    val targetDir = agentRegistry[agentNameLower] ?: run {
        println("ERROR: Unsupported agent '$agentName'")
        exitProcess(1)
    }

    val remoteRegistry = fetchRemoteRegistry()
    val skillInfo = remoteRegistry.skills[skillName] ?: run {
        println("ERROR: Skill '$skillName' not found in repository.")
        exitProcess(1)
    }

    val localVersions = loadLocalVersions()
    val currentLocalVersion = localVersions[skillName]

    if (currentLocalVersion != null && !isNewer(skillInfo.version, currentLocalVersion)) {
        println("Skill $skillName is already up to date.")
        return
    }

    downloadSkill(skillName, skillInfo.file, targetDir)
    updateLocalVersion(skillName, skillInfo.version)
    
    if (currentLocalVersion == null) {
        println("Successfully installed $skillName v${skillInfo.version} for $agentName.")
    } else {
        println("Skill updated to v${skillInfo.version}")
    }
}

fun promptForAgent(): String {
    println("Please select your target AI agent:")
    val agents = agentRegistry.keys.toList()
    agents.forEachIndexed { index, name ->
        println("${index + 1}) $name")
    }
    print("Selection (1-${agents.size}): ")
    val input = readLine()?.toIntOrNull()
    if (input == null || input !in 1..agents.size) {
        println("Invalid selection.")
        exitProcess(1)
    }
    return agents[input - 1]
}

fun fetchRemoteRegistry(): RemoteRegistry {
    val url = "$remoteBaseUrl/versions.json"
    val content = try {
        fetchUrl(url)
    } catch (e: Exception) {
        println("ERROR: Network unreachable. Cannot fetch skills.")
        exitProcess(1)
    }
    
    return try {
        Json { ignoreUnknownKeys = true }.decodeFromString<RemoteRegistry>(content)
    } catch (e: Exception) {
        println("ERROR: Failed to parse remote registry.")
        exitProcess(1)
    }
}

fun fetchUrl(urlString: String): String {
    val url = URL(urlString)
    val connection = if (urlString.startsWith("file:")) {
        url.openConnection()
    } else {
        (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5000
            readTimeout = 5000
        }
    }

    val responseCode = if (connection is HttpURLConnection) connection.responseCode else 200
    if (responseCode != 200) {
        throw Exception("HTTP $responseCode")
    }

    return connection.getInputStream().bufferedReader().use { it.readText() }
}

fun downloadSkill(skillName: String, fileName: String, targetDir: String) {
    val url = "$remoteBaseUrl/skills/$fileName"
    val content = try {
        fetchUrl(url)
    } catch (e: Exception) {
        println("ERROR: Failed to download skill file '$fileName'.")
        exitProcess(1)
    }

    val dir = File(targetDir)
    if (!dir.exists()) {
        if (!dir.mkdirs()) {
            println("ERROR: Insufficient directory permissions.")
            exitProcess(1)
        }
    }

    val targetFile = File(dir, fileName)
    try {
        targetFile.writeText(content)
    } catch (e: Exception) {
        println("ERROR: Insufficient directory permissions.")
        exitProcess(1)
    }
}

fun loadLocalVersions(): Map<String, String> {
    val file = File(localTrackingFile)
    if (!file.exists()) return emptyMap()
    
    return try {
        val json = Json.parseToJsonElement(file.readText()).jsonObject
        json.mapValues { it.value.jsonPrimitive.content }
    } catch (e: Exception) {
        emptyMap()
    }
}

fun updateLocalVersion(skillName: String, version: String) {
    val localVersions = loadLocalVersions().toMutableMap()
    localVersions[skillName] = version
    
    val json = JsonObject(localVersions.mapValues { JsonPrimitive(it.value) })
    try {
        val jsonString = Json { prettyPrint = true }.encodeToString(JsonObject.serializer(), json)
        File(localTrackingFile).writeText(jsonString)
    } catch (e: Exception) {
        println("ERROR: Insufficient directory permissions.")
        exitProcess(1)
    }
}

fun isNewer(remote: String, local: String): Boolean {
    // Simple semver comparison or string comparison
    // For simplicity, we can split by '.' and compare components
    val remoteParts = remote.split(".").mapNotNull { it.toIntOrNull() }
    val localParts = local.split(".").mapNotNull { it.toIntOrNull() }
    
    val length = maxOf(remoteParts.size, localParts.size)
    for (i in 0 until length) {
        val r = remoteParts.getOrElse(i) { 0 }
        val l = localParts.getOrElse(i) { 0 }
        if (r > l) return true
        if (r < l) return false
    }
    return false
}
