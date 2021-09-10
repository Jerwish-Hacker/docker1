package com.identity.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.keycloak.events.Event;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmProvider;

import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedTaskListener;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class KeycloakEventListener implements Callable<RecordMetadata>, ManagedTask {

	private final Event event;
	private final KeycloakSession session;
	private ObjectMapper objectMapper = new ObjectMapper();

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
		RecordMetadata metadata = null;
			RealmProvider realmProvider = session.realms();
		String type = event.getType().name().toLowerCase();
		log.info("event type :: {}",type);
		metadata = KeycloakEventProducer.get()
					.send(new ProducerRecord<>("com.identity."+ type +".events.t",
							objectMapper.writeValueAsString(event)))
					.get();
		log.info("metadata :: {}",metadata);
		return metadata;
	}
}
