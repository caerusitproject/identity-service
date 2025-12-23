package com.caerus.identity.messaging;

import com.caerus.identity.dto.ForgotPasswordEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ForgotPasswordEventPublisher {

    private final KafkaTemplate<String, ForgotPasswordEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.notification-events}")
    private String topic;

    public void publish(ForgotPasswordEvent event) {
        kafkaTemplate.send(topic, event.getEmail(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish forgot password event for userId={}",
                                event.getUserId(), ex);
                    } else {
                        log.info("Forgot password event published to topic={}, offset={}",
                                topic,
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
