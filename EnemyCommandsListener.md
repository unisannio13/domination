# EnemyCommandsListener #

<br>

<h1>Introduzione</h1>

Di seguito viene descritta la procedura per poter usufruire, nella propia AI, delle informazioni sulle mosse dell'avversario.<br>
<br>
<br>
<h1>How To</h1>

Per poter ricevere informazioni riguardo le mosse effettuate dai propri avversari, è sufficiente implementare, nella propria AI, l'interfaccia net.yura.domination.engine.ai.EnemyCommandsListener.<br> A questo punto, è necessario implementare il metodo<br>
<pre><code>void onEnemyCommand(Player enemy, String command);<br>
</code></pre>
che viene chiamato ogni qualvolta un avversario effettua una mossa.<br>
<ul><li>Player enemy, fornisce il riferimento al Player nemico;<br>
</li><li>String command, fornisce la stringa che descrive la mossa effettuata.</li></ul>

<h3>Esempio</h3>

<pre><code>package it.unisannio.perdenti.ai;<br>
<br>
import net.yura.domination.engine.ai.*;<br>
<br>
@Discoverable<br>
public class AISemplice extends AI implements EnemyCommandsListener{<br>
   ...<br>
  <br>
   public void onEnemyCommand(Player enemy, String command) {<br>
     //Farne quel che si vuole<br>
   }<br>
  <br>
   ...<br>
}<br>
<br>
</code></pre>