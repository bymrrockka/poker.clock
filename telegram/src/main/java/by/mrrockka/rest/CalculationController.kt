package by.mrrockka.rest

import by.mrrockka.repo.GameRepo
import by.mrrockka.service.CalculationService
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

        games.forEach { game ->
            try {
                calculationService.calculate(game)
            } catch (ex: IllegalStateException) {
                errors.add(ex.message ?: "Exception occurred")
            }
        }
        return """
            Recalculated ${games.size} games.
            Error count: ${errors.size}
            Errors output: 
            - ${errors.joinToString("\n- ")}
            """.trimIndent()
    }
}