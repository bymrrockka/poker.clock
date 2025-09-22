package by.mrrockka.service.statistics

import by.mrrockka.domain.MessageMetadata

interface StatisticsService {
    fun statistics(metadata: MessageMetadata): String
}