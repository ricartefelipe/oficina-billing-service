package br.com.oficina.billing.bdd;

import br.com.oficina.billing.application.port.out.BillingEventPublisherPort;
import br.com.oficina.billing.application.port.out.MercadoPagoPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.UUID;

@TestConfiguration
public class TestConfig {

    @Bean
    public BillingEventPublisherPort billingEventPublisherPort() {
        return (routingKey, event) -> {};
    }

    @Bean
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
