package cz.ondrejvane.griphub;

import cz.ondrejvane.griphub.config.AsyncSyncConfiguration;
import cz.ondrejvane.griphub.config.EmbeddedSQL;
import cz.ondrejvane.griphub.config.JacksonConfiguration;
import cz.ondrejvane.griphub.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { GripHubApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class })
@EmbeddedSQL
public @interface IntegrationTest {
}
