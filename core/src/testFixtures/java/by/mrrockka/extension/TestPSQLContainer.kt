package by.mrrockka.extension

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class TestPSQLContainer : PostgreSQLContainer<TestPSQLContainer>(DockerImageName.parse("postgres:$version")) {
    //  todo: found out that testcontainer creates two containers, one with default values another one with overriden - needs investigation
    companion object {
        const val version: String = "16.1"
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
