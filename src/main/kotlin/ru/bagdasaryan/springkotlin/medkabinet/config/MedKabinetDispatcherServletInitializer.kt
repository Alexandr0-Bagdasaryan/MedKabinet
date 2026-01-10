package ru.bagdasaryan.springkotlin.medkabinet.config

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer

class MedKabinetDispatcherServletInitializer : AbstractAnnotationConfigDispatcherServletInitializer() {
    override fun getRootConfigClasses(): Array<out Class<*>>? {
        return null
    }

    override fun getServletConfigClasses(): Array<out Class<*>>? {
        return arrayOf<Class<*>>(SpringConfig::class.java)
    }

    override fun getServletMappings(): Array<out String> {
        return arrayOf<String>("/")
    }

}