package by.mrrockka.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  @Bean
  public ObjectMapper objectMapper() {
    return new JsonMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }
}
