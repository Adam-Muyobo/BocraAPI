/*
 * Verifies that the BOCRA API Spring application context boots with test-safe configuration.
 */
package bw.org.bocra.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "SPRING_DATASOURCE_URL=jdbc:h2:mem:bocra-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver",
        "SPRING_DATASOURCE_USERNAME=sa",
        "SPRING_DATASOURCE_PASSWORD=",
        "SPRING_FLYWAY_ENABLED=true",
        "SPRING_JPA_HIBERNATE_DDL_AUTO=validate",
        "JWT_SECRET=VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdKV1RTZWNyZXRGb3JUZXN0aW5nMTIzNDU2Nzg=",
        "JWT_EXPIRATION=3600000",
        "REFRESH_TOKEN_EXPIRATION=1209600000",
        "EMAIL_VERIFICATION_EXPIRATION=86400000"
})
class BocraApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
