package pl.jsql.api.config

import org.springframework.beans.factory.ObjectFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.ConversionService
import org.springframework.data.web.config.SpringDataWebConfiguration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import pl.jsql.api.security.interceptor.SecurityInterceptor

@Configuration
@EnableWebMvc
class JsqlConfig extends SpringDataWebConfiguration {

    JsqlConfig(ApplicationContext context, ObjectFactory<ConversionService> conversionService) {
        super(context, conversionService)
    }

    @Bean
    SecurityInterceptor securityInterceptor() {
        return new SecurityInterceptor()
    }


    @Override
    void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor())
                .addPathPatterns("/api/**")
    }

}
