# Projeto A3 2026 - Sistema de Gerenciamento de Loja de Roupas

## 📌 Sobre o projeto

O **Projeto A3 2026** é um sistema desktop desenvolvido em Java para gerenciamento de uma loja de roupas, permitindo o controle de produtos, estoque, usuários e vendas de forma prática e intuitiva.

Este projeto foi desenvolvido como atividade acadêmica da disciplina **A3**, aplicando conceitos fundamentais de engenharia de software e desenvolvimento desktop.

---

# 🚀 Funcionalidades

## 👤 Gerenciamento de Usuários
- Cadastro de novos usuários
- Login com autenticação segura
- Criptografia de senhas utilizando BCrypt
- Controle de acesso ao sistema

---

## 👕 Gerenciamento de Produtos
- Cadastro de produtos
- Edição de produtos
- Exclusão de produtos
- Upload e gerenciamento de imagens
- Organização por:
  - Nome
  - Categoria
  - Tamanho
  - Preço
  - Quantidade em estoque

---

## 📦 Controle de Estoque
- Atualização automática de estoque
- Controle por tamanho
- Visualização da disponibilidade dos produtos

---

## 🛒 Processo de Venda
- Seleção de produtos
- Escolha de tamanhos
- Simulação de carrinho
- Registro das vendas

---

# 🛠 Tecnologias Utilizadas

- Java
- Java Swing
- JDBC
- MySQL
- BCrypt
- IntelliJ IDEA

---

# 📂 Estrutura do Projeto

```bash
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
```

---

# ⚙️ Como Executar o Projeto

## 1. Clone o repositório

```bash
git clone https://github.com/ViniciuPSantos/ProjetoA3_2026_v1.git
```

---

## 2. Configure o banco de dados

Crie um banco de dados MySQL:

```sql
CREATE DATABASE projeto_a3;
```

Depois execute o script SQL do projeto para criar as tabelas necessárias.

---

## 3. Configure a conexão com o banco

No arquivo `DBConnector.java`, altere os dados conforme sua configuração local:

```java
private static final String URL = "jdbc:mysql://localhost:3306/projeto_a3";
private static final String USER = "root";
private static final String PASSWORD = "sua_senha";
```

---

## 4. Execute o projeto

Abra em sua IDE (preferencialmente IntelliJ) e execute a classe principal do sistema.

---

# 📸 Telas do Sistema

- Tela de Login
- Tela de Cadastro
- Menu Principal
- Cadastro de Produto
- Edição de Produto
- Seleção de Tamanho
- Controle de Estoque

> *(adicione prints futuramente para melhorar a apresentação do repositório)*

---

# 🎯 Objetivo Acadêmico

O objetivo deste projeto é aplicar conhecimentos de:

- Programação Orientada a Objetos
- Desenvolvimento Desktop
- Integração com Banco de Dados
- Interface Gráfica
- Boas práticas de desenvolvimento

---

# 👨‍💻 Autor

**Vinícius de Paula Santos**

GitHub: :contentReference[oaicite:0]{index=0}

---

# 📄 Licença

Projeto acadêmico desenvolvido para fins educacionais.
