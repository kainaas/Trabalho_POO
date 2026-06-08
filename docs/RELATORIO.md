# Java Event Planner — Relatório do Projeto

**Disciplina:** SCC0204 — Programação Orientada a Objetos
**Projeto 7 — Java Event Planner (versão completa)**

Integrantes:
- Fabio Kauê Araujo da Silva — 16311045
- Kainã Alves Tureso — 15466391
- Luís Henrique de Queiroz Veras — 14592414

## 1. Descrição da aplicação

O Java Event Planner é um aplicativo de desktop feito em Java com Swing para
organizar compromissos num calendário. A tela principal mostra a grade do mês de um
lado e a lista de eventos do dia selecionado do outro. Os dias que têm eventos ficam
destacados, e o dia de hoje também.

Dá pra criar, editar e excluir eventos. Cada evento tem título, data, hora, duração,
local, descrição e uma categoria (reunião, aniversário, compromisso ou outro). Os
eventos podem se repetir (diário, semanal ou mensal) e podem ter participantes (nome
e e-mail). Também tem busca por palavra-chave em todas as datas e um botão pra trocar
entre tema claro e escuro.

Cada evento pode ter um lembrete (10 minutos, 1 hora ou 1 dia antes). Com o programa
aberto, uma thread separada fica conferindo os horários e mostra um aviso na tela
quando chega a hora. Os eventos são salvos num arquivo de texto e recarregados quando
o programa abre de novo.

## 2. Como compilar e executar

O projeto não usa nenhuma biblioteca externa, só o JDK (testamos no Java 25). A partir
da pasta `src`:

```
cd src
compile.cmd      (ou: javac -d bin ./Controller/*.java ./Model/*.java ./View/*.java ./Main/*.java)
Run.cmd          (ou: cd bin && java Main.Main)
```

O arquivo `events.txt` é criado dentro de `bin` na primeira vez que um evento é salvo.
Os ícones ficam em `bin/iconImages`.

## 3. Conceitos de POO aplicados

O projeto é dividido no esquema MVC: o pacote `Model` guarda os dados e as regras, o
`View` tem as telas em Swing e o `Controller` faz a ponte entre os dois. Além disso
usamos o padrão Observer: o `CalendarModel` herda de `AbstractModel`, que tem um
`PropertyChangeSupport`. Quando algo muda no modelo (um evento novo, troca de mês,
troca de tema), ele dispara um aviso e a `CalendarView` redesenha as telas sozinha,
sem o modelo precisar conhecer a interface.

A herança aparece em alguns pontos. O `CalendarModel` herda de `AbstractModel` pra
reaproveitar toda a parte de notificação. As nossas exceções (`EventValidationException`
e `StorageException`) herdam de `Exception`. E na interface os painéis herdam de
`JPanel`, a janela de `JFrame` e o formulário de `JDialog`.

O encapsulamento é a base do modelo: os campos são todos `private` e só dá pra mexer
neles pelos getters e setters. Quem monta e valida um `Event` é sempre o `Controller`,
então a tela nunca cria um evento "na mão" — ela passa os dados e recebe o evento
pronto (ou uma exceção com a mensagem de erro).

O polimorfismo aparece nos vários `toString()` que sobrescrevemos (em `Recurrence`,
`Attendee` e `ViewMode`), na `CalendarView` que implementa `PropertyChangeListener`,
no `ReminderService` que implementa `Runnable` e no renderizador próprio que a lista de
eventos usa pra mostrar cada evento com a hora na frente do título. A composição também
está presente: um `CalendarModel` é dono da lista de `Event`, e cada `Event` é dono dos
seus `Attendee` e do seu `Recurrence`.

## 4. Principais desafios

O maior foi a parte de eventos que se repetem. No começo a gente ia criar uma cópia do
evento pra cada dia, mas isso enche a memória e complica salvar. Trocamos por guardar só
o evento "mestre" com o tipo de repetição, e o método `occursOn(dia)` calcula na hora se
aquele evento cai num dia específico. Pra apagar uma única ocorrência sem mexer no resto,
guardamos as datas removidas num conjunto de exceções.

Editar "somente esta" ocorrência deu trabalho por causa de uma regra que já existia no
modelo: não pode ter dois eventos com o mesmo título. Quando o usuário edita só uma
ocorrência, ela vira um evento avulso com o mesmo título do mestre, e isso batia na
regra. Resolvemos com um método `addEventForced`, usado só nesse caso, que pula a
checagem de título repetido.

Salvar tudo em arquivo também exigiu pensar um pouco, porque participantes e datas de
exceção são listas. Acabamos usando uma linha por evento, com os campos separados por
TAB e as sublistas separadas por vírgula. Na leitura, se uma linha estiver fora do
formato ela é ignorada, então um arquivo corrompido não derruba o programa (ele só
carrega os eventos que deram certo).

Os lembretes foram a primeira vez que mexemos com threads. A `ReminderService` roda numa
thread daemon que confere os horários de tempos em tempos. Como não dá pra mexer no Swing
de fora da thread da interface, o aviso é mostrado com `SwingUtilities.invokeLater`.

## 5. Observação sobre o escopo

O enunciado pede pra perguntar, ao editar ou excluir um evento recorrente, se a ação vale
"somente esta ocorrência ou todas as futuras". Pra não complicar demais a conta de datas,
a gente tratou a segunda opção como "a série inteira", que na prática é o caso mais comum.
As duas opções aparecem numa caixa de diálogo toda vez que o evento se repete.

## 6. Diagrama de classes

O diagrama está em `docs/uml.puml`, em PlantUML. Pra gerar a imagem é só abrir o arquivo
no site plantuml.com ou na extensão de PlantUML do VS Code.
