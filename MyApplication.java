import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
@OpenAPIDefinition(info = @Info(title = "MyApp API", version = "${info.app.version}", description = "MyApp API"))
@SecurityScheme(name = "bearerAuth", scheme = "bearer", bearerFormat = "JWT", type = SecuritySchemeType.HTTP)
@SecurityScheme(name = "apiKeyAuth", paramName = "X-API-KEY", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class MyApplication {
    @Autowired
    Environment env;

    public static void main(String[] args) {
        SpringApplication.run(CambiobancaApplication.class, args);

    }

    @EventListener({ApplicationReadyEvent.class})
    public void developerModeEnable() {
        String contextPath = env.getProperty("server.servlet.context-path") == null ? "" : env.getProperty("server.servlet.context-path");
        log.info("API list for developers:  http://localhost:{}{}/swagger-ui/index.html", env.getProperty("server.port"), contextPath);
        log.info("App. Version: {}", env.getProperty("info.app.version"));
    }


}
