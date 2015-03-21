# Aggiungere una nuova AI #




# Introduzione #

Di seguito sono descritte le procedure per poter integrare una nuova AI all'interno di Domination


# How To #

Per per integrare una nuova AI all'interno di Domination, è necessario includere le proprie classi all'interno del Classpath facendo in modo che esse rispettino le regole descritte di seguito. È inoltre possibile includere le proprie classi tramite pacchetto jar.<br>
Per fare in modo che le proprie classi AI siano rilevate automaticamete, è necessario che rispettino le seguenti regole:<br>
<ul><li>Le classi devono essere presenti nel Classpath (sia contenute all'interno di un jar, sia come file .class "sfusi");<br>
</li><li>Il nome della classe deve iniziare con "AI";<br>
</li><li>La classe deve estendere <a href='https://code.google.com/p/unisannio-domination/wiki/AggiungereAI#Estendere_AI'>AI</a> oppure <a href='https://code.google.com/p/unisannio-domination/wiki/AggiungereAI#Estendere_BaseAI'>BaseAI</a> presenti nel package net.yura.domination.engine.ai;<br>
</li><li>La classe deve essere annotata con l'annotazione @Discoverable definita nel package net.yura.domination.engine.ai.<br>
Le classi rilevate, diventano automaticamente selezionabili nel gioco tramite un menu a tendina. Il nome visualizzato sarà uguale al nome della classe senza "AI".<br>
<br>
<h3>Esempio:</h3>
È possibile creare all'interno del progetto una nuova source folder (es. "src_ai") all'interno della quale definire la seguente classe:<br>
<pre><code>package it.unisannio.perdenti.ai;<br>
<br>
import net.yura.domination.engine.ai.*;<br>
<br>
@Discoverable<br>
public class AISemplice extends AI{<br>
...<br>
}<br>
<br>
</code></pre>
Una classe così definita, sarà indicata, all'interno del gioco, con il nome "Semplice".</li></ul>

<h2>Estendere AI</h2>
Il progetto Domination originale è stato modificato affinché tutte le AI abbiano, come superclasse, la classe astratta net.yura.domination.engine.ai.AI.<br>
Una volta estesa la classe AI, è necessario implementare i seguenti metodi:<br>
<pre><code>String getTrade();<br>
String getPlaceArmies();<br>
String getAttack();<br>
String getRoll();<br>
String getBattleWon();<br>
String getTacMove();<br>
String getAutoDefendString();<br>
</code></pre>
Ogni metodo viene chiamato a seconda della fase di gioco corrente. La stringa da restituire descrive la mossa da eseguire.<br>
Per informazioni riguardo al significato dei suddetti metodi e alla sintassi delle stringhe da restituire, fare riferimento alla documentazione Javadoc della classe AI.<br>
<br>
<h2>Estendere BaseAI</h2>
È possibile creare una AI estendendo la classe astratta net.yura.domination.engine.ai.BaseAI.<br>
BaseAI, che estede AI, fornisce una interfaccia ad oggetti per l'implementazione della propria AI.<br>
Una volta estesa la classe BaseAI, è necessario implementare i seguenti metodi:<br>
<pre><code>Country onCountryFortification();<br>
Attack onAttack();<br>
</code></pre>
e, eventualmente, fare l'Override dei seguenti metodi:<br>
<pre><code>Country onCountrySelection();<br>
Trade onTrade();<br>
Fortification onFortification();<br>
int onAttackRoll();<br>
int onOccupation()<br>
Move onArmyMove();<br>
int onDefenseRoll();<br>
</code></pre>
I suddetti metodi restituiscono degli oggetti rappresentativi dell'azione da svolgere. In questo modo, il programmatore è sollevato dal problema di dover costruire "a mano" la stringa che indica la mossa da eseguire.<br>
Inoltre vengono effettuati dei sanity check che controllano che, l'azione indicata, sia valida lanciando delle IllegalArgoumentException. Ciò evita che eventuali errori siano gestiti dal gioco stesso che reagisce semplicemente bloccandosi..<br>
Per maggiori informazioni sui metodi, sugli oggetti da restituire e sulle azioni di default intraprese se i metodi non vengono "Overrided", fare riferimento alla documentazione Javadoc della classe BaseAI.