package ai.achaialabs.helios

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform