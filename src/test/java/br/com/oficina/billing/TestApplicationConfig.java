package br.com.oficina.billing;

import br.com.oficina.billing.application.port.out.BillingEventPublisherPort;
import br.com.oficina.billing.application.port.out.MercadoPagoPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.util.UUID;

@TestConfiguration
public class TestApplicationConfig {

    @Bean
    @Primary
    public BillingEventPublisherPort billingEventPublisherPort() {
        return (routingKey, event) -> {};
    }

    @Bean
    @Primary
    public MercadoPagoPort mercadoPagoPort() {
        return new MercadoPagoPort() {
            @Override
            public String criarPreferencia(UUID osId, BigDecimal valor, String descricao) {
                return "TEST-PREF-" + osId;
            }

            @Override
            public String consultarStatusPagamento(String paymentId) {
                return "approved";
            }
        };
    }
}
