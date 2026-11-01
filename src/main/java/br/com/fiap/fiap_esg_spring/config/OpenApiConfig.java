package br.com.fiap.fiap_esg_spring.config;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        String dataHora = LocalDateTime.now()
                .minusHours(3)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return new OpenAPI()
                .info(new Info()
                        .title("FIAP ESG API")
                        .version("1.0.0")
                        .description("API do projeto FIAP ESG com Spring Boot e MongoDB. " +
                                "Servidor staging iniciado em: " + dataHora)
                )
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                        .addList("basicAuth"));
    }
}