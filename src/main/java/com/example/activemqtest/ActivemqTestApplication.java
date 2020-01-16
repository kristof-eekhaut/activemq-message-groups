package com.example.activemqtest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@SpringBootApplication
@RestController
@RequestMapping("/test")
public class ActivemqTestApplication {

	private static final Logger LOGGER = LogManager.getLogger(ActivemqTestApplication.class);

	@Autowired
	private Publisher publisher;
	@Autowired
	private ObjectMapper objectMapper;

	public static void main(String[] args) {
		SpringApplication.run(ActivemqTestApplication.class, args);
	}

	@Bean
	public ActiveMQConnectionFactoryCustomizer connectionFactory() {
		return factory -> {
			RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
			redeliveryPolicy.setUseExponentialBackOff(true);
			redeliveryPolicy.setMaximumRedeliveryDelay(10 * 60 * 1000L);
			redeliveryPolicy.setMaximumRedeliveries(RedeliveryPolicy.NO_MAXIMUM_REDELIVERIES);
			factory.setRedeliveryPolicy(redeliveryPolicy);
		};
	}

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public void test(@RequestBody TestData testData) throws Exception {

		LOGGER.info("Start test: " + testData.getTestName());

		for (int i = 0; i < testData.getNrOfMessages(); i++) {
			MsgContent msgContent = new MsgContent(testData.getTestName() + " - " + i, testData.getFailures());
			publisher.sendMessage(objectMapper.writeValueAsString(msgContent),
					testData.getGroupId().orElse(null));
		}
	}

	public static class TestData {

		private final String testName;
		private final int failures;
		private final String groupId;
		private final int nrOfMessages;

		@JsonCreator
		private TestData(@JsonProperty("testName") String testName,
						 @JsonProperty("failures") Integer failures,
						 @JsonProperty("groupId") String groupId,
						 @JsonProperty("nrOfMessages") Integer nrOfMessages) {
			this.testName = requireNonNull(testName);
			this.failures = Optional.ofNullable(failures).orElse(0);
			this.groupId = groupId;
			this.nrOfMessages = Optional.ofNullable(nrOfMessages).orElse(1);
		}

		public String getTestName() {
			return testName;
		}

		public int getFailures() {
			return failures;
		}

		public Optional<String> getGroupId() {
			return Optional.ofNullable(groupId);
		}

		public int getNrOfMessages() {
			return nrOfMessages;
		}
	}
}
