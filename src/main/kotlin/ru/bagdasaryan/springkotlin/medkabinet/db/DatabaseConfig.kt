package ru.bagdasaryan.springkotlin.medkabinet.db

import org.jetbrains.exposed.sql.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class DatabaseConfig {

    @Bean
    @Primary
    fun db(dataSource: DataSource): Database {
        return Database.connect(dataSource)
    }
}
