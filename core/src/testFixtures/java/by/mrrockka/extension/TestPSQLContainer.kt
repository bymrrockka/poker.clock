package by.mrrockka.extension

import org.testcontainers.postgresql.PostgreSQLContainer

class TestPSQLContainer {

    companion object {
        const val version: String = "16.1"
        const val dbName: String = "pokerclock"
        const val username = "itest"
        const val password = "itest123"

        val container = PostgreSQLContainer("postgres:$version")
                .withDatabaseName(dbName)
                ?.withUsername(username)
                ?.withPassword(password)
                ?: error("Can't start postgres container")

    }
}
