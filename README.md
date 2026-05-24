Projeto A3 2026 - Sistema de Gerenciamento de Loja de Roupas
📌 Sobre o projeto

O Projeto A3 2026 é um sistema desktop desenvolvido para gerenciamento de uma loja de roupas, permitindo o controle de produtos, estoque, usuários e vendas de forma prática e intuitiva.

O sistema foi desenvolvido como projeto acadêmico, aplicando conceitos de:

Programação Orientada a Objetos (POO)
Interface gráfica com Java Swing
Persistência de dados com JDBC
Banco de dados MySQL
Arquitetura em camadas
Segurança de autenticação com BCrypt
🚀 Funcionalidades
👤 Usuários
Cadastro de usuários
Login com autenticação segura
Criptografia de senha com BCrypt
Controle de acesso
👕 Produtos
Cadastro de produtos
Edição de produtos
Exclusão de produtos
Upload e gerenciamento de imagens
Organização por:
Nome
Categoria
Tamanho
Preço
Quantidade em estoque
📦 Estoque
Controle automático de estoque
Atualização de quantidade por tamanho
Visualização de disponibilidade
🛒 Vendas
Seleção de produtos
Escolha de tamanhos
Controle de carrinho
Registro de vendas
🛠 Tecnologias utilizadas
Java
Java Swing
JDBC
MySQL
BCrypt
IntelliJ IDEA / NetBeans
📂 Estrutura do projeto
ProjetoA3_2026_v1/
│── src/
│   └── org/projeto/
│       ├── DBConnector.java
│       ├── TelaLogin.java
│       ├── TelaCadastro.java
│       ├── TelaMenu.java
│       ├── TelaCadastroProduto.java
│       ├── TelaEditarProduto.java
│       ├── TelaSelecaoTamanho.java
│       └── ...
│
├── imagens/
├── banco.sql
└── README.md
⚙️ Como executar o projeto
1. Clone o repositório
git clone https://github.com/ViniciuPSantos/ProjetoA3_2026_v1.git
2. Configure o banco de dados

Crie um banco MySQL:

CREATE DATABASE projeto_a3;

Execute o script SQL do projeto para criar as tabelas.

3. Configure a conexão

No arquivo:

DBConnector.java

altere:

private static final String URL = "jdbc:mysql://localhost:3306/projeto_a3";
private static final String USER = "root";
private static final String PASSWORD = "sua_senha";
4. Execute

Abra o projeto em sua IDE e rode a classe principal:

Main.java
📸 Telas do sistema
Login
Cadastro de usuário
Menu principal
Cadastro de produto
Edição de produto
Seleção de tamanho
Controle de estoque

(adicione screenshots aqui futuramente)

🎯 Objetivo acadêmico

Este projeto foi desenvolvido para a disciplina A3, com foco em aplicar conceitos de desenvolvimento de software em um sistema real, simulando um ambiente comercial.

👨‍💻 Autor

Vinícius de Paula Santos

GitHub: @ViniciuPSantos

📄 Licença

Projeto acadêmico para fins educacionais.
