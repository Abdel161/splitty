package server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.passay.PasswordGenerator;

@Configuration
public class Config {

    /**
     * Gets a PasswordGenerator instance.
     *
     * @return PasswordGenerator instance.
     */
    @Bean
    public PasswordGenerator getPasswordGenerator() {
        return new PasswordGenerator();
    }

    /**
     * Gets an ObjectMapper instance.
     *
     * @return ObjectMapper instance.
     */
    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
