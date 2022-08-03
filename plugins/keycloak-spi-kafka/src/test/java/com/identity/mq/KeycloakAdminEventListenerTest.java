package com.identity.mq;

import com.identity.domain.IIdentityEvent;
import com.identity.domain.IdentityEvent;
import com.identity.services.MalformedUserException;
import org.junit.Test;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

import static org.junit.Assert.assertNotNull;

public class KeycloakAdminEventListenerTest {
    private final AdminEvent adminEvent = new AdminEvent();
    private final KeycloakAdminEventListener listener = new KeycloakAdminEventListener( adminEvent);

    @Test
    public void basics(){
        assertNotNull(listener);
    }

    @Test
    public void shouldReturnInstanceOfTypeIndentityEvent() throws MalformedUserException {
        adminEvent.setResourceType(ResourceType.USER);
        adminEvent.setResourcePath("users/f623e3a4-a7a4-4a34-b602-41f5d47a08d8");
        adminEvent.setOperationType(OperationType.CREATE);
        AuthDetails authDetails = new AuthDetails();
        authDetails.setIpAddress("10.0.0.1");
        authDetails.setRealmId("Something");
        authDetails.setUserId("f623e3a4-a7a4-4a34-b602-41f5d47a08d8");
        adminEvent.setTime(1659510293983L);
        adminEvent.setAuthDetails(authDetails);
        IIdentityEvent identityEvent = listener.getIdentityEvent(adminEvent);
        assertNotNull(identityEvent);
        System.out.println(identityEvent instanceof IdentityEvent);
    }
}
