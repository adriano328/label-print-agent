## Label Print Agent 🖨️
É um agente local de impressão desenvolvido para permitir a impressão de etiquetas térmicas em impressoras Brother QL-800 (USB) a partir de aplicações web.

A solução resolve limitações de navegadores modernos (CORS, Private Network Access) e permite que um frontend online (Angular) envie comandos de impressão para um agente local rodando no Windows.

O agente:
- É desenvolvido em Java 17 com Spring Boot
- Expõe uma API HTTP local em http://127.0.0.1:9100
- Utiliza Java Print Service e o Windows Print Spooler
- É empacotado como executável Windows via jpackage
- Não depende de plugins ou extensões no navegador

🛠 TECNOLOGIAS UTILIZADAS
Backend:
- Java 17
- Spring Boot
- Java Print Service
- Maven
- jpackage

Frontend (integração):
- Angular
- HttpClient
- RxJS

🖨 IMPRESSORA SUPORTADA
- Brother QL-800 (USB)
- Trabalha com larguras fixas de rolo (29, 38, 50, 54, 62 mm)
- Jobs inválidos podem ser ignorados silenciosamente pelo driver
- O projeto valida parâmetros de mídia para evitar falhas

🌐 API LOCAL
Endpoints:
- GET /health
- GET /printers
- POST /print

Exemplo de payload:
{
  "printerName": "Brother QL-800",
  "text": "0051",
  "copies": 1
}

🔐 SEGURANÇA E NAVEGADORES
- CORS configurado corretamente
- Suporte a Private Network Access (Chrome)
- Comunicação segura entre site HTTPS e 127.0.0.1

📦 DISTRIBUIÇÃO
- Executável Windows (LabelPrintAgent.exe)
- Gerado via jpackage
- Servidor sobe automaticamente na porta 9100

🚀 EXECUÇÃO
- Clone do repositório
- Build com mvnw clean package
- Execução via LabelPrintAgent.exe
- Health check em /health

🎯 FUNCIONALIDADES
- Impressão local de etiquetas via web
- Integração direta com hardware USB
- Validação de mídia e parâmetros
- Arquitetura extensível
