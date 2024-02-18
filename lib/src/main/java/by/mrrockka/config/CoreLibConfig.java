package by.mrrockka.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

//todo: probably make it auto configuration
@SpringBootConfiguration
@ComponentScan({"by.mrrockka"})
public class CoreLibConfig {

  @Bean
  public ObjectMapper objectMapper() {
    return new JsonMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }
}
