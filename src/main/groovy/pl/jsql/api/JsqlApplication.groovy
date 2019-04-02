package pl.jsql.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class JsqlApplication {

    static void main(String[] args) {
        SpringApplication.run(JsqlApplication.class, args)
    }

}
