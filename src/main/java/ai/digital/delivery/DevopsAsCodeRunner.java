package ai.digital.delivery;

import io.fabric8.kubernetes.client.utils.Serialization;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;

public class DevopsAsCodeRunner {

    private String url = "http://d9c082f8bb54.ngrok.io";

    private String username = "admin";

    private String password = "admin";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.basicAuthentication(username, password).build();
    }

    @Bean
    public CommandLineRunner preview(RestTemplate restTemplate) throws Exception {

    }

    private <T> T loadYaml(Class<T> clazz, String yaml) {
        try (InputStream is = getClass().getResourceAsStream(yaml)) {
            return Serialization.unmarshal(is, clazz);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot find yaml on classpath: " + yaml);
        }
    }

}
