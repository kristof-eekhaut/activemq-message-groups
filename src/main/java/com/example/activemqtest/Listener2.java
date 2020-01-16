package com.example.activemqtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
public class Listener2 extends AbstractListener {

    private static final Logger LOGGER = LogManager.getLogger(Listener2.class);

    public Listener2(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @JmsListener(destination = "Consumer.TestConsumer.VirtualTopic.TestTopic", concurrency = "10-50")
    public void onMessage(Message message) {
        handleMessage(message, LOGGER);
    }
}
