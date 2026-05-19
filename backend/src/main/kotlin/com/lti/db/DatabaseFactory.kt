package com.lti.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {

    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)
    private lateinit var dataSource: HikariDataSource

    fun init(config: ApplicationConfig) {
        val db = config.config("database")

        dataSource = HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = db.property("jdbcUrl").getString()
                driverClassName = db.property("driver").getString()
                username = db.property("user").getString()
                password = db.property("password").getString()
                maximumPoolSize = 5
                poolName = "lti-db-pool"
            },
        )

        Database.connect(dataSource)
        logger.info("Database pool initialized (jdbcUrl={})", db.property("jdbcUrl").getString())
    }

    fun verifyConnection(): Result<Unit> = runCatching {
        transaction {
            exec("SELECT 1")
        }
    }

    fun close() {
        if (::dataSource.isInitialized && !dataSource.isClosed) {
            dataSource.close()
            logger.info("Database pool closed")
        }
    }
}
