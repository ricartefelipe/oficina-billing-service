package br.com.oficina.billing.adapters.out.messaging;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

import static org.mockito.Mockito.verify;

class BillingRabbitMqEventPublisherTest {

    @Test
    void publish_deveEncaminharParaRabbitTemplate() {
        RabbitTemplate template = Mockito.mock(RabbitTemplate.class);
        BillingRabbitMqEventPublisher publisher = new BillingRabbitMqEventPublisher(template);
        Object event = Map.of("osId", "abc");

        publisher.publish("orcamento.gerado", event);

        verify(template).convertAndSend(BillingRabbitMqConfig.EXCHANGE, "orcamento.gerado", event);
    }
}
