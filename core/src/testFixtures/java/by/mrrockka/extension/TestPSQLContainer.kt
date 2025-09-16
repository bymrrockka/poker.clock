package by.mrrockka.extension

import org.testcontainers.containers.PostgreSQLContainer

class TestPSQLContainer : PostgreSQLContainer<TestPSQLContainer?>() {
    //  todo: found out that testcontainer creates two containers, one with default values another one with overriden - needs investigation
    companion object {
        const val version: String = "16.1"
        const val imageAndVersion: String = "postgres:$version"
        const val dbName: String = "pokerclock"
        const val username = "itest"
        const val password = "itest123"

        val container: TestPSQLContainer = TestPSQLContainer()
                .withDatabaseName(dbName)
                ?.withUsername(username)
                ?.withPassword(password)
                ?: error("Can't start postgres container")

    }
}
