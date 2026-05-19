package com.lti

import com.lti.db.DatabaseFactory
import com.typesafe.config.ConfigFactory
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.*
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val status: String)

fun main() {
    embeddedServer(
        Netty,
        environment = applicationEngineEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load())
            connector {
                host = "0.0.0.0"
                port = 8080
            }
            module(Application::module)
        },
    ).start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init(environment.config)
    DatabaseFactory.verifyConnection().fold(
        onSuccess = { log.info("PostgreSQL connection successful") },
        onFailure = { log.error("PostgreSQL connection failed", it) },
    )

    environment.monitor.subscribe(ApplicationStopped) {
        DatabaseFactory.close()
    }

    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/health") {
            call.respond(HealthResponse(status = "ok"))
        }
    }
}
