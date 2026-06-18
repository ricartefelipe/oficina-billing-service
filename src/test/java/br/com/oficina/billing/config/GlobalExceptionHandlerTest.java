package br.com.oficina.billing.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_deveRetornar404() {
        var pd = handler.handleNotFound(new NoSuchElementException("não encontrado"));
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(pd.getDetail()).isEqualTo("não encontrado");
    }

    @Test
    void handleConflict_deveRetornar422() {
        var pd = handler.handleConflict(new IllegalStateException("operação inválida"));
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
}
