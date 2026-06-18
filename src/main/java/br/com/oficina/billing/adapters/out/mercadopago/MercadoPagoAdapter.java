package br.com.oficina.billing.adapters.out.mercadopago;

import br.com.oficina.billing.application.port.out.MercadoPagoPort;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class MercadoPagoAdapter implements MercadoPagoPort {

    @Value("${app.mercadopago.access-token:TEST_TOKEN_PLACEHOLDER}")
    private String accessToken;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    @Override
    public String criarPreferencia(UUID osId, BigDecimal valor, String descricao) {
        try {
            var item = PreferenceItemRequest.builder()
                .title(descricao)
                .quantity(1)
                .unitPrice(valor)
                .build();

            var preference = PreferenceRequest.builder()
                .items(List.of(item))
                .externalReference(osId.toString())
                .build();

            var client = new PreferenceClient();
            var response = client.create(preference);
            return response.getId();
        } catch (MPException | MPApiException e) {
            throw new RuntimeException("Erro ao criar preferencia no Mercado Pago: " + e.getMessage(), e);
        }
    }

    @Override
    public String consultarStatusPagamento(String paymentId) {
        try {
            var client = new PaymentClient();
            var payment = client.get(Long.parseLong(paymentId));
            return payment.getStatus();
        } catch (MPException | MPApiException e) {
            throw new RuntimeException("Erro ao consultar pagamento: " + paymentId, e);
        }
    }
}
