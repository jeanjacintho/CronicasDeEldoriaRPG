# Guia de Contribuição - Cronicas de Eldoria 🎮

Este documento contém todas as diretrizes e regras para contribuir com o projeto de forma organizada e eficiente.

## 📋 Índice

- [Configuração do Ambiente](#-configuração-do-ambiente)
- [Estrutura de Branches](#-estrutura-de-branches)
- [Convenções de Commit](#-convenções-de-commit)
- [Padrões de Código](#-padrões-de-código)
- [Processo de Development](#-processo-de-development)
- [Code Review](#-code-review)
- [Testes](#-testes)
- [Documentação](#-documentação)
- [Definição de Pronto](#-definição-de-pronto)
- [Estrutura de Pastas](#-estrutura-de-pastas)

## 🛠️ Configuração do Ambiente

### Pré-requisitos
- Java 21+
- Maven 3.8+
- IDE de sua preferência (IntelliJ IDEA recomendado)
- Git

### Setup Inicial
```bash
# Clone o repositório
git clone https://github.com/jeanjacintho/CronicasDeEldoriaRPG
cd CronicasDeEldoriaRPG

# Configure sua identidade Git
git config user.name "Seu Nome"
git config user.email "seu.email@exemplo.com"

# Instale as dependências
mvn clean install

# Execute os testes
mvn test
```

## 🌿 Estrutura de Branches (Estratégia Simplificada para 4 Devs Júnior)

### Estratégia Recomendada: GitHub Flow Adaptado

#### Branch Principal
- **`main`** - Código principal (sempre funcional e testado)

#### Branches de Trabalho Individual
- **`feature/sprint-X-nome-da-funcionalidade-dev-nome`**
- **`bugfix/descricao-do-bug-dev-nome`**

### Por Que Essa Estratégia?
✅ **Simples de entender** - Apenas uma branch principal  
✅ **Menos conflitos** - Cada dev tem suas branches nomeadas  
✅ **Fácil rastreamento** - Identifica quem está trabalhando no quê  
✅ **Segurança** - Main sempre estável com code review obrigatório  

### Nomenclatura de Branches
```bash
# ✅ Formato Correto
feature/sprint-1-classe-personagem-dev-joao
feature/sprint-5-sistema-batalha-dev-maria
feature/sprint-9-habilidades-dev-carlos
bugfix/corrigir-calculo-dano-dev-ana

# ✅ Alternativa Mais Curta (usando iniciais)
feature/sprint-1-personagem-jm  # João Mendes
feature/sprint-5-batalha-ms     # Maria Silva
feature/sprint-9-habilidades-cr # Carlos Rodrigues
bugfix/calculo-dano-al          # Ana Lima

# ❌ Incorreto
nova-funcionalidade
bug-fix
minha-branch
sprint-1
```

### Organização por Desenvolvedor
```bash
# Desenvolvedor João (JM)
feature/sprint-1-personagem-jm
feature/sprint-4-combate-jm
bugfix/corrigir-hp-jm

# Desenvolvedora Maria (MS)
feature/sprint-2-guerreiro-ms
feature/sprint-6-turnos-ms
feature/sprint-10-elementos-ms

# Desenvolvedor Carlos (CR)
feature/sprint-3-inimigos-cr
feature/sprint-7-interface-cr
feature/sprint-11-defesa-cr

# Desenvolvedora Ana (AL)
feature/sprint-5-batalha-al
feature/sprint-8-integracao-al
feature/sprint-12-critico-al
```

## 📝 Convenções de Commit

### Formato Padrão
```
tipo(escopo): descrição curta

Descrição mais detalhada (opcional)

Closes #123
```

### Tipos de Commit
- **`feat`** - Nova funcionalidade
- **`fix`** - Correção de bug
- **`docs`** - Documentação
- **`style`** - Formatação, ponto e vírgula, etc
- **`refactor`** - Refatoração de código
- **`test`** - Adição ou correção de testes
- **`chore`** - Tarefas de build, configuração, etc

### Exemplos de Commits
```bash
# ✅ Bons exemplos
feat(personagem): adicionar classe base Personagem
fix(batalha): corrigir cálculo de dano crítico
docs(readme): atualizar instruções de instalação
test(guerreiro): adicionar testes unitários para classe Guerreiro
refactor(ia): extrair lógica de decisão para classe separada

# ❌ Exemplos ruins
"mudanças"
"fix bug"
"atualização"
"código novo"
```

## 💻 Padrões de Código

### Convenções Java
```java
// ✅ Nomes de classes - PascalCase
public class PersonagemGuerreiro extends Personagem {
    
    // ✅ Constantes - SNAKE_CASE maiúsculo
    private static final int PONTOS_VIDA_INICIAL = 100;
    
    // ✅ Variáveis e métodos - camelCase
    private int pontosVidaAtual;
    private List<Habilidade> habilidadesDisponiveis;
    
    // ✅ Métodos com verbos descritivos
    public void atacarInimigo(Inimigo alvo) {
        // implementação
    }
    
    // ✅ Getters/Setters padrão
    public int getPontosVidaAtual() {
        return pontosVidaAtual;
    }
}
```

### Estrutura de Pacotes
```
com.rpg
├── model/           # Entidades do domínio
├── service/         # Lógica de negócio
├── network/         # WebSocket handlers e comunicação
├── ia/              # Integração com JLama
├── util/            # Utilitários
├── exception/       # Exceções customizadas
├── controller/      # Controllers do jogo
└── config           # Arquivos de config
```

### Regras de Código
1. **Máximo 80 caracteres por linha**
2. **Sempre usar `Optional` para valores que podem ser null**
3. **Javadoc obrigatório para métodos públicos**
4. **Nunca deixar `System.out.println()` no código final**

### 1. Iniciar Desenvolvimento
```bash
# Sempre partir da main atualizada
git checkout main
git pull origin main

# Criar branch pessoal
git checkout -b feature/sprint-X-nome-da-funcionalidade-dev-seu-nome
```

### 2. Durante o Desenvolvimento
```bash
# Commits frequentes (pelo menos diário)
git add .
git commit -m "feat(personagem): implementar classe base"

# Sincronizar com main regularmente (recomendado 2x por semana)
git checkout main
git pull origin main
git checkout feature/sprint-X-nome-da-funcionalidade-dev-seu-nome
git rebase main  # ou merge main se rebase for complexo
```

### 4. Finalizar Sprint
```bash
# Push da branch
git push origin feature/sprint-X-nome-da-funcionalidade-dev-seu-nome

# Criar Pull Request no GitHub/GitLab
# - Título: "Sprint X: Nome da Funcionalidade"
# - Descrição: O que foi implementado
# - Reviewer: Escolher 1 colega da equipe + Scrum Master
```

### 5. Code Review e Merge
- **Review obrigatório** de pelo menos 1 pessoa
- **Aprovação do Scrum Master** para sprints críticas
- **Merge na main** após aprovação
- **Deletar branch** após merge

## 👀 Code Review

### Checklist do Revisor
- [ ] Código segue padrões estabelecidos
- [ ] Funcionalidade atende aos requisitos da sprint
- [ ] Testes estão presentes e passando
- [ ] Não há código duplicado
- [ ] Nomes de variáveis e métodos são descritivos
- [ ] Não há hardcoding de valores
- [ ] Tratamento de exceções está adequado
- [ ] WebSocket connections são gerenciadas corretamente
- [ ] Recursos são liberados adequadamente (try-with-resources)

### Checklist do Desenvolvedor
- [ ] Código compila sem warnings
- [ ] Todos os testes passam
- [ ] Funcionalidade foi testada manualmente
- [ ] Documentação foi atualizada
- [ ] Não há `TODO` ou `FIXME` no código
- [ ] Branch está atualizada com develop
- [ ] Connections WebSocket são fechadas adequadamente
- [ ] Threads são gerenciadas corretamente

## 🧪 Testes

### Estrutura de Testes
```java
// ✅ Padrão de nomeação
class PersonagemTest {
    
    @Test
    @DisplayName("Deve criar personagem com atributos corretos")
    void deveCriarPersonagemComAtributosCorretos() {
        // Given - Arrange
        String nome = "Aragorn";
        int pontosVida = 100;
        
        // When - Act
        Guerreiro guerreiro = new Guerreiro(nome, pontosVida);
        
        // Then - Assert
        assertThat(guerreiro.getNome()).isEqualTo(nome);
        assertThat(guerreiro.getPontosVida()).isEqualTo(pontosVida);
    }
}
```

### Cobertura de Testes
- **Mínimo 80% de cobertura** para classes de service
- **100% de cobertura** para classes de model
- Usar `@ParameterizedTest` para múltiplos cenários
- Testar casos de erro e exceções

### Executar Testes
```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=PersonagemTest

# Com relatório de cobertura
mvn test jacoco:report
```

## 📖 Documentação

### Javadoc Obrigatório
```java
/**
 * Calcula o dano causado por um ataque baseado na força do atacante
 * e na defesa do alvo.
 *
 * @param atacante O personagem que está atacando
 * @param alvo O personagem que receberá o dano
 * @return O valor do dano calculado (mínimo 1)
 * @throws IllegalArgumentException se atacante ou alvo forem null
 * @since 1.0
 */
public int calcularDano(Personagem atacante, Personagem alvo) {
    // implementação
}
```

### README.md por Sprint
Cada sprint deve ter documentação atualizada explicando:
- O que foi implementado
- Como usar as novas funcionalidades
- Exemplos de código

## ✅ Definição de Pronto

Uma sprint está pronta quando:

### Desenvolvimento
- [ ] Código implementado e funcionando
- [ ] Segue todos os padrões estabelecidos
- [ ] Não quebra funcionalidades existentes
- [ ] Testes unitários implementados (cobertura > 80%)

### Qualidade
- [ ] Code review aprovado por pelo menos 1 pessoa
- [ ] Todos os testes passam no CI/CD
- [ ] Não há warnings de compilação
- [ ] Análise de código estática passou

### Documentação
- [ ] Javadoc completo para métodos públicos
- [ ] README atualizado se necessário
- [ ] Changelog atualizado

### Integração
- [ ] Branch merged em `main`
- [ ] Card movido para "Concluído" no Trello
- [ ] Demo funcional preparada para retrospectiva
- [ ] Branch de trabalho deletada após merge

## 📁 Estrutura de Pastas

```
cronicasdeeldoriarpg/
├── docs/                  # Documentação do projeto
│   ├── sprints/           # Documentação por sprint
│   └── arquitetura/       # Diagramas e documentos técnicos
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/cronicasdeeldoria/
│   │   │       ├── model/        # Entidades (Personagem, Habilidade, etc)
│   │   │       ├── service/      # Lógica de negócio
│   │   │       ├── network/      # WebSocket handlers
│   │   │       ├── ia/           # Integração JLama
│   │   │       ├── game/         # Controllers do jogo
│   │   │       ├── util/         # Classes utilitárias
│   │   │       ├── exception/    # Exceções customizadas
│   │   │       └── config/       # Config de arquivos
│   │   └── resources/
│   │       ├── web/              # Arquivos web (HTML, CSS, JS)
│   │       └── config/           # Arquivos de configuração
│   └── test/
│       └── java/
│           └── br/com/cronicasdeeldoria/         # Testes seguindo mesma estrutura
├── .gitignore
├── pom.xml
├── README.md
├── CONTRIBUTING.md
└── CHANGELOG.md
```

## 🚨 Regras Importantes

### ❌ Não Permitido
- Commits diretamente em `main` (NUNCA!)
- Push de código que não compila
- Merge sem code review
- Trabalhar na branch de outro desenvolvedor sem avisar
- Deixar `System.out.println()` no código
- Hardcoding de valores de configuração
- Código comentado (usar Git para histórico)

### ⚠️ Atenção Especial
- **Performance:** Sempre considere o impacto de suas mudanças
- **Segurança:** Nunca commite senhas ou chaves de API
- **Compatibilidade:** Mudanças em APIs devem ser backward-compatible
- **Logs:** Use `java.util.logging` ou SLF4J, não `System.out`
- **WebSocket:** Sempre feche connections adequadamente
- **Threads:** Use ExecutorService para gerenciar threads
- **Memória:** Libere recursos da JLama após uso
- **Comunicação:** Avise a equipe sobre mudanças que afetam outros devs