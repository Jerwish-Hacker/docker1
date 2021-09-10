package com.identity.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.*;

import static java.util.Objects.nonNull;

@Slf4j
public class KeycloakCustomEventListenerProviderFactory implements EventListenerProviderFactory {

	private ManagedExecutorService mes;
	private KeycloakEventListenerProvider keycloakEventListenerProvider;
	private List<EventType> excludedEvents;

	@Override
	public EventListenerProvider create(KeycloakSession session) {
		return keycloakEventListenerProvider;
	}

	@Override
	public void init(Config.Scope config) {
		log.info("Config read : {} ", config.get("kafkaKeySerializerClass"));
		String[] excludedStringEvents = config.getArray("exclude-events");
		if (nonNull(excludedStringEvents)) {
			excludedEvents = new ArrayList<>();
			for (String event : excludedStringEvents) {
				excludedEvents.add(EventType.valueOf(event));
			}
		}
		Properties props = new Properties();
		try {
			mapSerializer(config, props);
		} catch (ClassNotFoundException e) {
			log.error("Error loading class: {}", e.getMessage());
		}
		props(config, props);
		KeycloakEventProducer.create(props);
		log.debug("Excluded events list: {}", excludedEvents);

	}
	private void props(Config.Scope config, Properties props) {
		props.put(ProducerConfig.ACKS_CONFIG, config.get("kafkaAcks"));
		props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, config.getInt("kafkaDeliveryTimeoutMs"));
		props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, config.getInt("kafkaRequestTimeoutMs"));
		props.put(ProducerConfig.LINGER_MS_CONFIG, config.getInt("kafkaLingerMs"));
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, config.getInt("kafkaBatchSize"));
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, config.getInt("kafkaMemoryBuffer"));
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.get("kafkaBrokerBinder"));
	}

	private void mapSerializer(Config.Scope config, Properties props) throws ClassNotFoundException {
		Class<?> keyClass = Class.forName(
				Optional.of(config.get("kafkaKeySerializerClass"))
						.map(val -> val)
						.orElse("org.apache.kafka.common.serialization.StringSerializer"));

		Class<?> valueClass = Class.forName(
				Optional.of(config.get("kafkaKeySerializerClass"))
						.map(val -> val)
						.orElse("org.apache.kafka.common.serialization.StringSerializer"));
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keyClass);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueClass);
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
		try {
			InitialContext ctx = new InitialContext();
			this.mes = (ManagedExecutorService) ctx.lookup("java:jboss/ee/concurrency/executor/default");
			log.debug("Executor Service created successfully {}", mes);
			keycloakEventListenerProvider = new KeycloakEventListenerProvider(mes, excludedEvents, factory.create());
		} catch (NamingException e) {
			log.error("Error creating Executor Service (StackTrace)", e);
			RuntimeException ex = new RuntimeException();
			ex.initCause(e);
			throw ex;
		}
	}

	@Override
	public void close() {
		KeycloakEventProducer.get().close();
	}

	@Override
	public String getId() {
		return "kafka-event-listener";
	}
}
