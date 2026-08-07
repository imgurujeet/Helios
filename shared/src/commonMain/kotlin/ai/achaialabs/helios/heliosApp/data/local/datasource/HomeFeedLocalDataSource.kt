package ai.achaialabs.helios.heliosApp.data.local.datasource

import ai.achaialabs.helios.heliosApp.data.local.dao.HomeFeedDao
import ai.achaialabs.helios.heliosApp.data.local.entity.HomeFeedEntity
import ai.achaialabs.helios.heliosApp.data.local.entity.HomePromptRelation
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import kotlinx.coroutines.flow.Flow

interface HomeFeedLocalDataSource {

    fun observeFeed(
        feedType: HomeFeedType
    ): Flow<List<HomePromptRelation>>

    suspend fun replaceFeed(
        feedType: HomeFeedType,
        promptIds: List<String>
    )

    suspend fun appendFeed(
        feedType: HomeFeedType,
        promptIds: List<String>,
        startPosition: Int
    )

    suspend fun clearFeed(
        feedType: HomeFeedType
    )
}

class HomeFeedLocalDataSourceImpl(
    private val homeFeedDao: HomeFeedDao
) : HomeFeedLocalDataSource {

    override fun observeFeed(
        feedType: HomeFeedType
    ): Flow<List<HomePromptRelation>> {
        return homeFeedDao.observeHomeFeed(feedType)
    }

    override suspend fun replaceFeed(
        feedType: HomeFeedType,
        promptIds: List<String>
    ) {
        homeFeedDao.clearFeed(feedType)

        val feeds = promptIds.mapIndexed { index, promptId ->
            HomeFeedEntity(
                feedType = feedType,
                position = index,
                promptId = promptId
            )
        }

        homeFeedDao.insertFeeds(feeds)
    }

    override suspend fun appendFeed(
        feedType: HomeFeedType,
        promptIds: List<String>,
        startPosition: Int
    ) {
        val feeds = promptIds.mapIndexed { index, promptId ->
            HomeFeedEntity(
                feedType = feedType,
                position = startPosition + index,
                promptId = promptId
            )
        }

        homeFeedDao.insertFeeds(feeds)
    }

    override suspend fun clearFeed(
        feedType: HomeFeedType
    ) {
        homeFeedDao.clearFeed(feedType)
    }
}