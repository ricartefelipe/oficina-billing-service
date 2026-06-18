# language: pt
Funcionalidade: Geração e aprovação de Orçamento
  Como o sistema de billing
  Quero gerar um orçamento ao receber uma OS aberta
  Para iniciar o processo de pagamento

  Cenário: Gerar orçamento ao receber OS
    Dado que o evento "os.aberta" foi recebido para a OS "os-001" com valor 500.00
    Quando o Billing Service processa o evento
    Então um orçamento é criado com status "AGUARDANDO_APROVACAO"
    E o evento "orcamento.gerado" é publicado

  Cenário: Recusar orçamento cancela a OS
    Dado que existe um orçamento aguardando aprovação para a OS "os-002"
    Quando o cliente recusa o orçamento
    Então o orçamento fica com status "RECUSADO"
    E o evento "orcamento.recusado" é publicado
