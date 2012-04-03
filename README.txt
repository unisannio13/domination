Il tutto si avvia dalla classe net.yura.domination.ui.flashgui.MainMenu presente nella source folder src_swing.

E' stata creata la classe astratta net.yura.domination.engine.ai.AI. Ora e' la superclasse di AICrap, AIEasy, AIHard ecc...
Ai metodi di AI.java sono stati aggiunti i commenti javadoc cosi' da permettere di visualizzare piu' facilmente quali sono le stringhe che i metodi dell'AI devono contenere.

Per permettere di integrare facilmente le AI, sono state fatte diverse modifiche all'engine del gioco. Prima ad ogni Player era assegnato un numero intero che indicava la sua AI (crap, easy, hard o human).
Di solito veniva usato uno swith o degli if annidati per determinare l'IA da utilizzare. Inoltre per accedere al ResourceBundle per le traduzioni e per permettere al parser di capire l'azione da compiere veniva usato una stringa univoca che veniva risolta in base all'intero dell'ai. 
  
Ora invece, ad ogni Player e' associato un oggetto AI. Oltre agli attributi game e player, e' dotato di una stringa id e una stringa name:
La stringa id e' una stringa univoca che serve per far identificare l'AI dal parser. Per essere processata dal parser DEVE iniziare con "ai " e non deve contenere ulteriori spazi (es. "ai crap" e' un id valido).
Viene attribito tramite il metodo setId(String id);

Il nome invece gli viene attribuito per fare in modo che non venga cercato nel ResourceBoundle. Quando serve al gioco per essere mostrato a video, viene recuperato con un getName().
Viene attribito tramite il metodo setName(String name);

Per supportare la possibilita' di usare diverse AI a seconda della modalita' di gioco (come fa AIHard), sono stati aggiunti ad AI.java i metodi setCapitalAI(AI ai) e setMissionAI(AI ai).

I metodi setName(), setID(), setCapitalAI() e setMissionAI() restituiscono this per far utilizzare i metodi a cascata

E' stata aggiunta la classe net.yura.domination.engine.ai.AIManager. Questa serve per integrare le AI nel gioco.
Nel metodo .setup() vanno instanziate le AI; queste vengono aggiunte ad una HashMap usando come chiave l'id.
E' preferibile aggiungere le AI tramite i metodi addAI(AI ai) o addAIs(AI... ais) dato che effettuano alcuni controlli sulla validita' delle AI.

Il metodo getAI(String id) viene chiamato dal parser quando ha bisogno di risolvere l'ai
Il metodo gerAIs() viene utilizzato dall'interfaccia per ottenere la lista di tutte le AI al fine di visualizzarle in una ComboBox (eliminati i RadioButton).

- Danilo Iannelli


