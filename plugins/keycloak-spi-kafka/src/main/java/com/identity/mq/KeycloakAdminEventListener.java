package com.identity.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identity.domain.IIdentityEvent;
import com.identity.domain.IdentityEvent;
import com.identity.domain.UnSupportedEvent;
import com.identity.services.MalformedUserException;
import com.identity.services.UserResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;

import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedTaskListener;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
public class KeycloakAdminEventListener implements Callable<RecordMetadata>, ManagedTask {

    private final AdminEvent event;
    private final UserResourceService userService = new UserResourceService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public RecordMetadata call() throws Exception {
        log.debug("Running task asynchronously");
        log.info("admin event :: {}", event.getRepresentation());
        IIdentityEvent identityEvent = null;
        try {
            identityEvent = getIdentityEvent(event);
        } catch (MalformedUserException e) {
            e.printStackTrace();
            log.error("Not Writing this malformed event to MQ");
            identityEvent = new UnSupportedEvent();
        }
        if(identityEvent instanceof IdentityEvent){
            return getMetadata("com.user.identity.events.t");

        } else {
            String topic = new StringBuilder("com.misc.identity.")
                    .append (event.getResourceType().name().toLowerCase())
                    .append (".events.t").toString();

            return getMetadata(topic);
        }
    }

    private RecordMetadata getMetadata(String topic) throws InterruptedException, ExecutionException, JsonProcessingException {
        RecordMetadata metadata;
        return KeycloakEventProducer.get()
                .send(new ProducerRecord<>(topic,
                        objectMapper.writeValueAsString(event)))
                .get();
    }
    public IIdentityEvent getIdentityEvent(AdminEvent event) throws MalformedUserException {
        log.debug("Identifying compatible transformer for an ADMIN event of type: {} & details {}", event.getResourceType(), event.getRepresentation());

        switch (event.getResourceType()){
            case USER:
                return operationType(event);
            default: {
                log.error("Un Supported ResourceType {}", event.getResourceType());
                return new UnSupportedEvent();
            }
        }
    }

    private IIdentityEvent operationType(AdminEvent event) throws MalformedUserException {
        if (event.getOperationType() == OperationType.CREATE ||
                event.getOperationType() == OperationType.UPDATE ||
                event.getOperationType() == OperationType.DELETE )
        return userService.register(event);
        else return new UnSupportedEvent();
    }

    @Override
    public ManagedTaskListener getManagedTaskListener() {
        return new KeycloakManagedTaskListener();
    }

    @Override
    public Map<String, String> getExecutionProperties() {
        // Do not add any execution properties
        return null;
    }
}
