package com.example.activemqtest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class Publisher {

    private static final Logger LOGGER = LogManager.getLogger(Publisher.class);

    public static final String TOPIC = "VirtualTopic.TestTopic";

    @Autowired
    private ConnectionFactory connectionFactory;

    private JmsTemplate jmsTemplate;

    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
    }

    public void sendMessage(String content, String groupId) {
        jmsTemplate.send(TOPIC, session -> createMessage(content, groupId, session));
    }

    private Message createMessage(String content, String groupId, Session session) throws JMSException {
        Message message = session.createTextMessage(content);

        if (groupId != null) {
            message.setStringProperty("JMSXGroupID", groupId);
        }

        LOGGER.info("Published message: {}", message);

        return message;
    }
}
