package pl.jsql.api;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import pl.jsql.api.security.interceptor.SecurityInterceptor;

import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
public class JsqlApplication extends SpringDataWebConfiguration {

    public JsqlApplication(ApplicationContext context, ObjectFactory<ConversionService> conversionService) {
        super(context, conversionService);
    }

    public static void main(String[] args) {
        SpringApplication.run(JsqlApplication.class, args);
    }

    @Value("${global.dateFormat}")
    private String dateFormat;

    @Bean
    public FastDateFormat simpleDateFormat() {
        return FastDateFormat.getInstance(dateFormat, TimeZone.getTimeZone("UTC"));
    }

    @Bean
    SecurityInterceptor securityInterceptor() {
        return new SecurityInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor())
                .addPathPatterns("/api/**");
    }


    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

}
