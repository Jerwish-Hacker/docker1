package com.identity.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identity.domain.IIdentityEvent;
import com.identity.domain.IdentityEvent;
import com.identity.domain.UnSupportedEvent;
import com.identity.services.UserResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.keycloak.events.Event;

import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedTaskListener;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
public class KeycloakEventListener implements Callable<RecordMetadata>, ManagedTask {

	private final Event event;
	private final UserResourceService userService = new UserResourceService();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public ManagedTaskListener getManagedTaskListener() {
		return new KeycloakManagedTaskListener();
	}

	@Override
	public Map<String, String> getExecutionProperties() {
		// Do not add any execution properties
		return null;
	}

	@Override
	public RecordMetadata call() throws Exception {
		log.debug("Running task asynchronously");
		log.info("event :: {}",event.getDetails());

		IIdentityEvent identityEvent = getIdentityEvent(event);
		if(identityEvent instanceof IdentityEvent){
			return getMetadata("com.identity.common.events.t");
		} else {
			String topic = new StringBuilder("com.misc.identity.common.")
					.append (event.getType().name().toLowerCase())
					.append (".events.t").toString();

			return getMetadata(topic);
		}
	}

	private RecordMetadata getMetadata(String topic) throws InterruptedException, ExecutionException, JsonProcessingException {
		return KeycloakEventProducer.get()
				.send(new ProducerRecord<>(topic,
						objectMapper.writeValueAsString(event)))
				.get();
	}

	public IIdentityEvent getIdentityEvent(Event event) {
		log.info("Identifying compatible transformer for an event of type :: {} & details {} ", event.getType(), event.getDetails());

		switch (event.getType()){
			case REGISTER:
				return userService.register(event);
			default: {
				log.error("Un Supported ResourceType {}", event.getType());
				return new UnSupportedEvent();
			}
		}

	}

}
