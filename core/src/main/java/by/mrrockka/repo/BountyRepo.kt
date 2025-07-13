package by.mrrockka.repo

import by.mrrockka.domain.Bounty
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
open class BountyRepo {
    fun findByPerson(personId: UUID): List<Bounty> {
        TODO("Not yet implemented")
    }

}