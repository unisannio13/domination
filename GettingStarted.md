# Getting Started #



<br>
<h1>Introduzione</h1>

Di seguito vengono descritte le procedure per importare il progetto e per poter iniziare a lavorare..<br>
<br>
<br>
<br>
<h1>Importare il progetto in Eclipse</h1>

Per Netbeans la procedura è analoga<br>
<br>
<h2>Importare il progetto tramite .zip</h2>
È il metodo più veloce e più facile per importare il progetto.<br>
Basta semplicemente scaricare il <a href='https://unisannio-domination.googlecode.com/files/unisannio-domination-rev2.zip'>file zip contenente il progetto</a> e dunque importarlo nel seguente modo:<br>
<ul><li>Nel menu “File” scegliere la voce “Import.”;<br>
</li><li>Nella finestra di dialogo “Import”:<br>
<ol><li>Scegliere la voce “Existing projects into Workspace” e quindi cliccare su “Next”;<br>
</li><li>Selezionare “Select archive file” e scegliere il file di archivio contenente il progetto da importare;<br>
</li><li>Cliccare su “Finish”.</li></ol></li></ul>

<h2>Clonare il progetto tramite <a href='http://git-scm.com/'>git</a></h2>
Procedura più lunga ma che permette di poter aggiornare più facilmente il progetto in seguito ad eventuali modifiche.<br>
<h3>Tramite comando da shell</h3>
Dopo aver <a href='http://git-scm.com/downloads'>scaricato</a> e installato git, eseguire da shell il seguente comando<br>
<pre><code>git clone https://code.google.com/p/unisannio-domination/<br>
</code></pre>
Una volta completata l'operazione, è possibile importare il progetto in Eclipse in modo analogo a quello descritto precedentemente.<br>
<br>
<h3>Tramite plugin</h3>
È possibile utilizzare un plugin per il proprio IDE per importare il codice.  Per Eclipse è disponibile il plugin <a href='http://www.eclipse.org/egit/'>Egit</a>.<br>
Una guida per installare Egit è presente seguendo <a href='http://www.vogella.com/articles/EGit/article.html#eclipseinstallation'>questo link</a>.<br>
Una guida per importare un progetto tramite Egit è invece presente <a href='http://wiki.eclipse.org/EGit/User_Guide#Cloning_Remote_Repositories'>qui</a>. Ovviamente, bisogna utilizzare come URI il seguente indirizzo:<br>
<pre><code>https://code.google.com/p/unisannio-domination/<br>
</code></pre>

<br>
<h1>Eseguire il progetto</h1>
Il main del progetto è definito nella classe<br>
<pre><code>net.yura.domination.ui.flashgui.MainMenu<br>
</code></pre>
presente nella source folder "src_swing".<br>
<br>
<br>
<h1>Aggiungere una nuova AI</h1>
Vai <a href='https://code.google.com/p/unisannio-domination/wiki/AggiungereAI'>qui</a>

<br>
<h1>Ottenere informazioni sulle mosse avversarie</h1>
EnemyCommandsListener