package ru.bagdasaryan.springkotlin.medkabinet.exposed

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager
import org.jetbrains.exposed.sql.transactions.TransactionManager
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
