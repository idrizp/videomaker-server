package dev.idriz.videomaker.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class EndpointSecurityTest {

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void testTTSWebhook() {
        
    }

}
