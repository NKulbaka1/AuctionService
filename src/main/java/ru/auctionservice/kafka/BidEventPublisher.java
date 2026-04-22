package ru.auctionservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidEventPublisher {

    private static final String TOPIC = "new-bids";

    private final KafkaTemplate<String, BidPlacedEvent> kafkaTemplate;

    public void publish(BidPlacedEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.getLotId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish BidPlacedEvent for lotId={}, bidId={}: {}",
                                event.getLotId(), event.getBidId(), ex.getMessage());
                    } else {
                        log.debug("Published BidPlacedEvent for lotId={}, bidId={}, offset={}",
                                event.getLotId(), event.getBidId(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
