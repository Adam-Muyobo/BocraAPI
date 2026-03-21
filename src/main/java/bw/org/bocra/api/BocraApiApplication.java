/*
 * Boots the BOCRA API application and enables configuration property scanning.
 */
package bw.org.bocra.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BocraApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BocraApiApplication.class, args);
    }

}
