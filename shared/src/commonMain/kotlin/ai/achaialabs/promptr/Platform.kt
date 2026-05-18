package ai.achaialabs.promptr

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform