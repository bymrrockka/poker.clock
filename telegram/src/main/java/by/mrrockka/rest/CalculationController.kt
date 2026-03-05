package by.mrrockka.rest

import by.mrrockka.repo.GameRepo
import by.mrrockka.service.CalculationService
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

interface CalculationController {
    fun recalculateGames(): String
}

@RestController
class CalculationControllerImpl(
        private val gameRepo: GameRepo,
        private val calculationService: CalculationService,
) : CalculationController {

    @PostMapping("/internal/games/recalculate")
    override fun recalculateGames(): String {
        val errors = mutableListOf<String>()
        val games = gameRepo.findAll()

        transaction {
            games.forEach { game ->
                try {
                    calculationService.calculate(game)
                } catch (ex: IllegalStateException) {
                    errors.add("Game id ${game.id} with: ${ex.message}")
                }
            }
        }
        return """
            Recalculated ${games.size - errors.size} games.
            Failed to recalc: ${errors.size}
            Errors output: 
            - ${errors.joinToString("\n- ")}
            
            """.trimIndent()
    }
}