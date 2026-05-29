package ai.achaialabs.promptr.promptrApp.domain.model

data class HomeHero(

    val id: String,

    val title: String,

    val description: String? = null,

    val imageUrl: String,

    val action: HeroAction,

    val schedule: HeroSchedule? = null
)

fun HomeHero.isActive(
    currentTimeMillis: Long
): Boolean {

    val schedule = schedule ?: return true

    return currentTimeMillis in
            schedule.startTimeMillis..
            schedule.endTimeMillis
}