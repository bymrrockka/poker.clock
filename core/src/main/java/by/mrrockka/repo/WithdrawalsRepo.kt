package by.mrrockka.repo

import org.jetbrains.exposed.dao.id.EntityID
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Repository
@Transactional
open class WithdrawalsRepo {
    fun findByPerson(personId: UUID): List<BigDecimal> {
        TODO("Not yet implemented")
    }

}