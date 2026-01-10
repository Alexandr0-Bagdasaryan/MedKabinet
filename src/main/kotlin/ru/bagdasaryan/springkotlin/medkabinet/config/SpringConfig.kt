package ru.bagdasaryan.springkotlin.medkabinet.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring6.view.ThymeleafViewResolver



@Configuration
@ComponentScan("ru.bagdasaryan.springkotlin.medkabinet.controller")
@EnableWebMvc
class SpringConfig : WebMvcConfigurer {
    lateinit var context: ApplicationContext

    @Autowired
    fun SpringConfig(context: ApplicationContext) {
        this.context = context
    }

    @Bean
    fun templateResolver(): SpringResourceTemplateResolver {
        val templateResolver = SpringResourceTemplateResolver()
        templateResolver.setApplicationContext(context)
        templateResolver.setPrefix("/WEB-INF/views/")
        templateResolver.setSuffix(".html")
        return templateResolver
    }

    @Bean
    fun templateEngine(): SpringTemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.setTemplateResolver(templateResolver())
        templateEngine.setEnableSpringELCompiler(true)
        return templateEngine
    }

    override fun configureViewResolvers(resolverRegistry: ViewResolverRegistry) {
        val thymeleafViewResolver = ThymeleafViewResolver()
        thymeleafViewResolver.setTemplateEngine(templateEngine())
        resolverRegistry.viewResolver(thymeleafViewResolver)
    }
}