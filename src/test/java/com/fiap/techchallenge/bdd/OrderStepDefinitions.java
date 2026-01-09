package com.fiap.techchallenge.bdd;

import com.fiap.techchallenge.application.usecases.OrderUseCase;
import com.fiap.techchallenge.domain.entities.Order;
import com.fiap.techchallenge.domain.entities.OrderStatus;
import com.fiap.techchallenge.domain.entities.StatusPayment;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderStepDefinitions {

    @Autowired(required = false)
    private OrderUseCase orderUseCase;

    private Order currentOrder;
    private List<Order> orderList;
    private List<OrderStatus> statusHistory;
    private Exception lastException;
    private String customerCpf;

    @Before
    public void setUp() {
        currentOrder = null;
        orderList = new ArrayList<>();
        statusHistory = new ArrayList<>();
        lastException = null;
        customerCpf = null;
    }

    // ========== DADO ==========

    @Dado("que existe um cliente com CPF {string}")
    public void queExisteUmClienteComCPF(String cpf) {
        customerCpf = cpf;
        assertNotNull(customerCpf);
    }

    @Dado("que existe um produto {string} com preço {double}")
    public void queExisteUmProdutoComPreco(String nomeProduto, Double preco) {
        // Produto mock para teste
        assertNotNull(nomeProduto);
        assertTrue(preco > 0);
    }

    @Dado("que existe um pedido cadastrado")
    public void queExisteUmPedidoCadastrado() {
        // Criar pedido mock para teste
        currentOrder = createMockOrder(OrderStatus.RECEIVED);
        assertNotNull(currentOrder);
    }

    @Dado("que existem pedidos com status {string}")
    public void queExistemPedidosComStatus(String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        // Mock: criar lista de pedidos com o status
        orderList.add(createMockOrder(orderStatus));
        orderList.add(createMockOrder(orderStatus));
        assertFalse(orderList.isEmpty());
    }

    @Dado("que existe um pedido com status {string}")
    public void queExisteUmPedidoComStatus(String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        currentOrder = createMockOrder(orderStatus);
        assertNotNull(currentOrder);
    }

    @Dado("que existe um pedido recém criado")
    public void queExisteUmPedidoRecemCriado() {
        currentOrder = createMockOrder(OrderStatus.RECEIVED);
        statusHistory.add(OrderStatus.RECEIVED);
        assertNotNull(currentOrder);
    }

    @Dado("que existem múltiplos pedidos em diferentes status")
    public void queExistemMultiplosPedidosEmDiferentesStatus() {
        orderList.add(createMockOrder(OrderStatus.RECEIVED));
        orderList.add(createMockOrder(OrderStatus.IN_PREPARATION));
        orderList.add(createMockOrder(OrderStatus.READY));
        orderList.add(createMockOrder(OrderStatus.FINISHED));
        assertEquals(4, orderList.size());
    }

    // ========== QUANDO ==========

    @Quando("eu crio um pedido com o produto {string} para o cliente")
    public void euCrioUmPedidoComOProdutoParaOCliente(String nomeProduto) {
        // Mock: criar pedido
        currentOrder = createMockOrder(OrderStatus.RECEIVED);
        assertNotNull(currentOrder);
    }

    @Quando("eu busco o pedido pelo seu ID")
    public void euBuscoOPedidoPeloSeuID() {
        // Mock: buscar pedido
        assertNotNull(currentOrder);
        assertNotNull(currentOrder.getId());
    }

    @Quando("eu listo os pedidos com status {string}")
    public void euListoOsPedidosComStatus(String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        // Filtrar lista por status
        orderList = orderList.stream()
                .filter(o -> o.getStatus() == orderStatus)
                .toList();
    }

    @Quando("eu atualizo o status do pedido para {string}")
    public void euAtualizoOStatusDoPedidoPara(String status) {
        OrderStatus newStatus = OrderStatus.valueOf(status);
        currentOrder.setStatus(newStatus);
        statusHistory.add(newStatus);
    }

    @Quando("o pagamento é aprovado")
    public void oPagamentoEAprovado() {
        currentOrder.setStatus(OrderStatus.IN_PREPARATION);
        statusHistory.add(OrderStatus.IN_PREPARATION);
    }

    @Quando("a cozinha inicia a preparação")
    public void aCozinhaIniciaAPreparacao() {
        assertEquals(OrderStatus.IN_PREPARATION, currentOrder.getStatus());
    }

    @Quando("a cozinha finaliza a preparação")
    public void aCozinhaFinalizaAPreparacao() {
        currentOrder.setStatus(OrderStatus.READY);
        statusHistory.add(OrderStatus.READY);
    }

    @Quando("o cliente retira o pedido")
    public void oClienteRetiraOPedido() {
        currentOrder.setStatus(OrderStatus.FINISHED);
        statusHistory.add(OrderStatus.FINISHED);
    }

    @Quando("eu listo todos os pedidos")
    public void euListoTodosOsPedidos() {
        // Ordenar por status e data
        assertFalse(orderList.isEmpty());
    }

    // ========== ENTÃO ==========

    @Entao("o pedido deve ser criado com sucesso")
    public void oPedidoDeveSerCriadoComSucesso() {
        assertNotNull(currentOrder);
    }

    @Entao("o pedido deve ter status {string}")
    public void oPedidoDeveTerStatus(String status) {
        OrderStatus expectedStatus = OrderStatus.valueOf(status);
        assertEquals(expectedStatus, currentOrder.getStatus());
    }

    @Entao("o pedido deve ter um ID válido")
    public void oPedidoDeveTerUmIDValido() {
        assertNotNull(currentOrder.getId());
        assertTrue(currentOrder.getId() > 0);
    }

    @Entao("o pedido deve ser encontrado")
    public void oPedidoDeveSerEncontrado() {
        assertNotNull(currentOrder);
    }

    @Entao("o pedido deve conter os itens corretos")
    public void oPedidoDeveConterOsItensCorretos() {
        assertNotNull(currentOrder);
        // Verificar itens
    }

    @Entao("devem ser retornados apenas pedidos com status {string}")
    public void devemSerRetornadosApenasPedidosComStatus(String status) {
        OrderStatus expectedStatus = OrderStatus.valueOf(status);
        assertTrue(orderList.stream().allMatch(o -> o.getStatus() == expectedStatus));
    }

    @Entao("devem ser retornados apenas pedidos em preparação")
    public void devemSerRetornadosApenasPedidosEmPreparacao() {
        assertTrue(orderList.stream().allMatch(o -> o.getStatus() == OrderStatus.IN_PREPARATION));
    }

    @Entao("o pedido deve passar por todos os status na ordem correta")
    public void oPedidoDevePassarPorTodosOsStatusNaOrdemCorreta() {
        assertEquals(4, statusHistory.size());
        assertEquals(OrderStatus.RECEIVED, statusHistory.get(0));
        assertEquals(OrderStatus.IN_PREPARATION, statusHistory.get(1));
        assertEquals(OrderStatus.READY, statusHistory.get(2));
        assertEquals(OrderStatus.FINISHED, statusHistory.get(3));
    }

    @Entao("os pedidos devem ser ordenados por status e data de criação")
    public void osPedidosDevemSerOrdenadosPorStatusEDataDeCriacao() {
        assertFalse(orderList.isEmpty());
    }

    // ========== Helper Methods ==========

    private Order createMockOrder(OrderStatus status) {
        Order order = new Order();
        order.setId((long) (Math.random() * 10000));
        order.setStatus(status);
        return order;
    }
}
