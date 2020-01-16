package com.example.activemqtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import static java.util.Objects.requireNonNull;

public abstract class AbstractListener implements MessageListener {

    private final ObjectMapper objectMapper;

    protected AbstractListener(ObjectMapper objectMapper) {
        this.objectMapper = requireNonNull(objectMapper);
    }

    protected void handleMessage(Message message, Logger logger) {

        try {
            final String jmsMessageID = message.getJMSMessageID();
            final Integer redeliverCount = message.getIntProperty("JMSXDeliveryCount");
            final String messageGroup = message.getStringProperty("JMSXGroupID");
            final String content = ((TextMessage) message).getText();
            final MsgContent msgContent = objectMapper.readValue(content, MsgContent.class);

            logger.info("Received message {} (Message ID: {}, Redeliver count: {}, Message group: {})", msgContent.getText(), jmsMessageID, redeliverCount, messageGroup);

            if (redeliverCount <= msgContent.getFailures()) {
                throw new RuntimeException("Simulated failure for message: " + msgContent.getText());
            }

            logger.info("Message successfully processed: " + msgContent.getText());

        } catch (Exception e) {
            throw new RuntimeException("Exception while processing message: " + message, e);
        }
    }
}
