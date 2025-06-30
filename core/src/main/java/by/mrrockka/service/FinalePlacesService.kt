package by.mrrockka.service

import by.mrrockka.domain.finaleplaces.FinalePlaces
import by.mrrockka.mapper.FinalePlacesMapper
import by.mrrockka.repo.finalplaces.FinalePlacesRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.util.*

@Service
@RequiredArgsConstructor
class FinalePlacesService {
    private lateinit var finalePlacesRepository: FinalePlacesRepository
    private lateinit var finalePlacesMapper: FinalePlacesMapper

    //todo: refactor optionals
    fun getByGameId(gameId: UUID): FinalePlaces? {
        return finalePlacesRepository.findByGameId(gameId)
                .map { entity -> finalePlacesMapper.toDomain(entity) }
                .orElse(null)
    }

    fun getAllByPersonId(personId: UUID): MutableList<FinalePlaces> {
        return finalePlacesRepository.findAllByPersonId(personId).stream()
                .map { entity -> finalePlacesMapper.toDomain(entity) }
                .toList()
    }

    fun store(gameId: UUID, finalePlaces: FinalePlaces) {
        finalePlacesRepository.save(finalePlacesMapper.toEntity(gameId, finalePlaces))
    }
}
