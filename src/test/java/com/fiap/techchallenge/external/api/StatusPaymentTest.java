package com.fiap.techchallenge.external.api;

import com.fiap.techchallenge.domain.entities.StatusPayment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatusPaymentTest {

    @Test
    @DisplayName("Deve mapear status do Mercado Pago para AGUARDANDO_PAGAMENTO")
    public void shouldMapPendingStatusesToAguardandoPagamento() {
        assertEquals(StatusPayment.AGUARDANDO_PAGAMENTO, StatusPayment.fromMercadoPagoStatus("pending"));
        assertEquals(StatusPayment.AGUARDANDO_PAGAMENTO, StatusPayment.fromMercadoPagoStatus("in_process"));
        assertEquals(StatusPayment.AGUARDANDO_PAGAMENTO, StatusPayment.fromMercadoPagoStatus("In_Process")); // teste case-insensitive
    }

    @Test
    @DisplayName("Deve mapear status do Mercado Pago para APROVADO")
    public void shouldMapApprovedStatusToAprovado() {
        assertEquals(StatusPayment.APROVADO, StatusPayment.fromMercadoPagoStatus("approved"));
        assertEquals(StatusPayment.APROVADO, StatusPayment.fromMercadoPagoStatus("APPROVED")); // teste case-insensitive
    }

    @Test
    @DisplayName("Deve mapear status do Mercado Pago para REJEITADO")
    public void shouldMapRejectedStatusesToRejeitado() {
        assertEquals(StatusPayment.REJEITADO, StatusPayment.fromMercadoPagoStatus("rejected"));
        assertEquals(StatusPayment.REJEITADO, StatusPayment.fromMercadoPagoStatus("cancelled"));
        assertEquals(StatusPayment.REJEITADO, StatusPayment.fromMercadoPagoStatus("CANCELLED")); // teste case-insensitive
    }

    @Test
    @DisplayName("Deve lançar exceção para status desconhecido")
    public void shouldThrowExceptionForUnknownStatus() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> StatusPayment.fromMercadoPagoStatus("unknown_status")
        );

        assertTrue(exception.getMessage().contains("Status desconhecido do Mercado Pago"));
    }
}
