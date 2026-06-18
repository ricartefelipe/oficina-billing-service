package br.com.oficina.billing.application.port.out;

public interface BillingEventPublisherPort {
    void publish(String routingKey, Object event);
}
