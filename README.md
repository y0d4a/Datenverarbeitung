# Datenverarbeitung

Genutze Frameworks und Bibliotheken:
- Scalatra
- MongoDB
- Spark

In der Datenverarbeitung bestand die Aufgabe darin, die Daten aus der MongoDB zulesen, die benötigen Informationen zusamenzufügen und diese dann an vorher definierten Rest Schnittstellen bereitzustellen.

Dabei haben wir für die REST Schnittstellen das Framework Scalatra genutzt, da dieses als lightweight Framework genau das bietet was wir brauchen. Man versteht sehr schnell wie das Framework arbeitet und kann trotz der hoher Benutzerfreundlichkeit genau definieren wie der Service koniguriert seien soll. Für einen schnellen Einstieg empfielt sich die offiziele Seite: http://scalatra.org/

Die Daten liegen in einer MongoDB vor und können mit dem MongoSpark-connctor direkt aus der Datenbank in ein SparkRDD geladen werden.Von hier an kann man mit den funktionalen Methoden die Spark bietet die Daten so bearbeiten und umformen, wie sie für die Abgesprochenen Schnittstellen zum Frontend gefordert sind. 

Um den Server zu starten muss man:
1. Das repository clonen
2. In den Projektordner wechseln
3. den befehl `sbt run` ausführen
4. In der sbt-Konsole: `jetty:start`

WICHTIG!
Zum beenden `Strg` + `d` 
