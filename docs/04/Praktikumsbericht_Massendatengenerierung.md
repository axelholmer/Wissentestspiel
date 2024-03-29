# Praktikumsbericht

- Genutzte Hardware:
    - Zur Zeiterfassung der Datengenerierung wurde das Programm auf einer t2.micro Instanz (1 vCPU) auf AWS EC2 laufen gelassen
- Allgemeine Performanceverbeserung
    - Spiele und Spieler werden im regulären Programmverlauf persistiert und nicht in einem gesonderten Loop
    - In der Category Klasse werden die zur Kategorie gehörenden Fragen über eager loading geladen, das gleiche gilt für die zu einer Frage gehörenden Antworten -> nur ein Datenbankzugriff, um alle notwendigen Daten zu holen 
- Einsatz von "Flush" und "clear"
    - es wird kein flush genutzt, da nach jeweils 10'000 erzeugten und persistierten Spielen ein commit ausgeführt mit
    - nach jedem commit wird ein clear aufgerufen, um den Persistenzkontext zu leeren 
    - alternativ zum commit könnten auch ein flush eingesetzt werden, was jedoch nach unseren Messungen länger dauert
    - clear ist in jedem Fall wichtig, da sonst der Programmspeicher überläuft
- Öffnen/ schließen von Transaktionen:
    - Es wird jeweils für 100 Spieler und für ihre jeweiligen Spiele eine Transaktion geöffnet und geschlossen, d.h. es gibt insgesamt 100 Transaktionen
- Batch writing:
    - Der Einsatz von Batch writing führt zu einer erheblichen Verbesserung der Speicherzeit von Faktor 3 
      - Mit Batch-Writing: 
        - Erstellung und Speicherung von 10'000 Spielen ca. 6 Sekunden
        - Komplette Dauer der Erstellung und Speicherung ca. 10 Minuten
      - Ohne Batch-Writing: 
        - Erstellung und Speicherung von 10'000 Spielen ca. 19 Sekunden
        - Komplette Dauer der Erstellung und Speicherung ca. 32 Minuten
- Überprüfung der Ergegnisse:
  - Datenbankabfragen über psql cli
    - Select count (id) from game; --> Ergebnis 1'000'000
    - Select count (id) from player; --> Ergebnis 10'000