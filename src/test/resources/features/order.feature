# language: pt
Funcionalidade: Gerenciamento de Pedidos
  Como um usuário do sistema de lanchonete
  Eu quero gerenciar pedidos
  Para que eu possa criar, consultar e atualizar o status dos pedidos

  Cenário: Criar um novo pedido com sucesso
    Dado que existe um cliente com CPF "12345678909"
    E que existe um produto "Hambúrguer" com preço 25.00
    Quando eu crio um pedido com o produto "Hambúrguer" para o cliente
    Então o pedido deve ser criado com sucesso
    E o pedido deve ter status "RECEIVED"
    E o pedido deve ter um ID válido

  Cenário: Buscar pedido por ID
    Dado que existe um pedido cadastrado
    Quando eu busco o pedido pelo seu ID
    Então o pedido deve ser encontrado
    E o pedido deve conter os itens corretos

  Cenário: Listar pedidos por status RECEIVED
    Dado que existem pedidos com status "RECEIVED"
    Quando eu listo os pedidos com status "RECEIVED"
    Então devem ser retornados apenas pedidos com status "RECEIVED"

  Cenário: Listar pedidos por status IN_PREPARATION (visão cozinha)
    Dado que existem pedidos com status "IN_PREPARATION"
    Quando eu listo os pedidos com status "IN_PREPARATION"
    Então devem ser retornados apenas pedidos em preparação

  Cenário: Atualizar status do pedido para IN_PREPARATION
    Dado que existe um pedido com status "RECEIVED"
    Quando eu atualizo o status do pedido para "IN_PREPARATION"
    Então o pedido deve ter status "IN_PREPARATION"

  Cenário: Atualizar status do pedido para READY
    Dado que existe um pedido com status "IN_PREPARATION"
    Quando eu atualizo o status do pedido para "READY"
    Então o pedido deve ter status "READY"

  Cenário: Atualizar status do pedido para FINISHED
    Dado que existe um pedido com status "READY"
    Quando eu atualizo o status do pedido para "FINISHED"
    Então o pedido deve ter status "FINISHED"

  Cenário: Fluxo completo de produção de pedido
    Dado que existe um pedido recém criado
    Quando o pagamento é aprovado
    E a cozinha inicia a preparação
    E a cozinha finaliza a preparação
    E o cliente retira o pedido
    Então o pedido deve passar por todos os status na ordem correta

  Cenário: Listar fila de pedidos para cozinha
    Dado que existem múltiplos pedidos em diferentes status
    Quando eu listo todos os pedidos
    Então os pedidos devem ser ordenados por status e data de criação
