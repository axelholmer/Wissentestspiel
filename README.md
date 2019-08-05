## Projektidee

Ziel des Projekts ist die Entwicklung des Spiels „Wissenstest“, mit dem man sein Wissen in verschiedenen Wissensgebieten (Kategorien) testen kann.



## Datenbasis

Auf dem Datenbankserver werden zu unterschiedlichen Kategorien Fragen gespeichert.
Zu jeder Frage gibt es genau vier Antworten, von denen genau eine richtig ist und folglich drei falsch sind.

Jede Frage gehört zu genau einer Kategorie.

Eine Basis von Kategorien, Fragen und Antworten als Stammdaten wird in einer entsprechenden `csv`-Datei zur Verfügung gestellt. Die `csv`-Datei finden Sie im Unterordner `/src/main/resources/`. Sie trägt den Namen: `Wissenstest_sample200.csv`.



## Spielbeschreibung

Wenn ein Spieler ein Spiel spielt, wählt er zunächst eine beliebige Anzahl von Kategorien aus (mindestens zwei) und lässt sich hierzu zufällig eine maximale Anzahl von Fragen generieren. Diese maximale Anzahl der Fragen pro Kategorie wird pro Spiel zu Beginn ebenfalls festgelegt und ist dann für alle Kategorien innerhalb eines Spiels gleich.
Aus den angebotenen vier Antworten kann der Spieler genau eine auswählen – er bekommt sofort eine Auskunft darüber, ob die ausgewählte Antwort richtig oder falsch war.
Jedes Spiel wird in dem Datenbankserver mit den folgenden Informationen inserted:
- Spieler
- Timestamp Start, Timestamp Ende
- Liste der Kategorien, Liste der Fragen und ausgewählte Antwort

Von jedem Spieler wird der eindeutige Spielername gespeichert sein.



## Massdata und Analyse

Durch die Menu kann man auswählen, ob massdata generiert werden soll. Es wird 10.000 Spieler, die jeweils 100 Spiele gespielt haben, generiert.
Man kann auch alle vorhandene Spiele in dem DB analysieren. Beispielweise könnte Spiele innerhalb einer bestimmten Zeitraum oder Spiele eines bestimmten Spieler ausgegeben werden. 

