package br.com.oficina.billing.adapters.out.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingRabbitMqConfig {

    public static final String EXCHANGE = "oficina.events";
    public static final String DLX = "oficina.dlx";

    @Bean
    public TopicExchange mainExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange billingDlx() {
        return new TopicExchange(DLX, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter billingMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    private Queue durableWithDlq(String name) {
        return QueueBuilder.durable(name)
            .withArgument("x-dead-letter-exchange", DLX)
            .withArgument("x-dead-letter-routing-key", name + ".dlq")
            .build();
    }

    @Bean public Queue queueBillingOsAberta() { return durableWithDlq("billing-service.os.aberta"); }
    @Bean public Queue queueBillingExecucaoFinalizada() { return durableWithDlq("billing-service.execucao.finalizada"); }

    @Bean
    public Binding bindBillingOsAberta(Queue queueBillingOsAberta, TopicExchange mainExchange) {
        return BindingBuilder.bind(queueBillingOsAberta).to(mainExchange).with("os.aberta");
    }

    @Bean
    public Binding bindBillingExecucaoFinalizada(Queue queueBillingExecucaoFinalizada, TopicExchange mainExchange) {
        return BindingBuilder.bind(queueBillingExecucaoFinalizada).to(mainExchange).with("execucao.finalizada");
    }
}
