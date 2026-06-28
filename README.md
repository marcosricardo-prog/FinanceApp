# FinanceApp

Aplicativo Android de controle financeiro pessoal desenvolvido como projeto final da disciplina **Desenvolvimento para Dispositivos Móveis II**.

---

## Tecnologias

- Kotlin + Jetpack Compose + Material 3
- MVVM + Clean Architecture
- Firebase Authentication
- Firestore + Room (SQLite local) + Retrofit (REST API)
- Navigation Compose + Koin + DataStore

---

## Funcionalidades

- Login, cadastro e recuperação de senha via Firebase Authentication
- Controle de sessão automático
- CRUD completo de transações financeiras
- Resumo de entradas, saídas e saldo
- Três fontes de persistência intercambiáveis em tempo real (Firebase / Room / API REST)
- Tela de configurações com troca de fonte e logout

---

## Segurança

- Autenticação obrigatória para acesso aos dados
- Regras de segurança no Firestore (apenas usuários autenticados)
- Sessão gerenciada pelo Firebase SDK

---

## Como executar

1. Clone o repositório

2. Acesse console.firebase.google.com com sua conta Google

3. Clique em "Criar um projeto"
    - Nome: FinanceApp (ou qualquer nome)
    - Google Analytics: pode desativar, não precisa para o projeto

4. Adicionar app Android

    - No painel do projeto, clique no ícone Android </>
    - Package name: com.example.financeapp ← exatamente esse
    - Apelido: FinanceApp (opcional)
    - Clique em Registrar app

5. Baixar o google-services.json

    - Clique em Baixar google-services.json
    - Adicione o arquivo `google-services.json` em `app/`

6. Ativar Authentication

    - Menu lateral → Authentication → Primeiros passos
    - Aba Sign-in method → E-mail/senha → ativar → Salvar

7. Criar o Firestore

    - Menu lateral → Firestore Database → Criar banco de dados
    - Modo de produção (as regras do firestore.rules que geramos já cuidam da segurança)
    - Região: southamerica-east1 (São Paulo)

8. Publicar as regras de segurança

 - Firestore → aba Regras → cole o conteúdo do firestore.rules gerado → Publicar


---
