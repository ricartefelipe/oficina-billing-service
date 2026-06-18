package br.com.oficina.billing.adapters.out.messaging;

import br.com.oficina.billing.application.port.out.BillingEventPublisherPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RabbitTemplate.class)
class BillingRabbitMqEventPublisher implements BillingEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    BillingRabbitMqEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String routingKey, Object event) {
        rabbitTemplate.convertAndSend(BillingRabbitMqConfig.EXCHANGE, routingKey, event);
    }
}
