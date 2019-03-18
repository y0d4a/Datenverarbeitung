# ECCO-Project: Datenverarbeitung

## Verwendete Frameworks

- Scalatra
- Apache Spark
- MongoDB
  - MongoSpark Connector


## Deployment
```
$ git clone https://github.com/htw-wise-2018/Datenverarbeitung.git
$ cd Datenverarbeitung
$ screen -S ecco_process
$ sbt clean
$ sbt package
$ sbt
$ jetty:start
```

Nach dem Screen Befehl kann die Console geschlossen werden und der Prozess läuft weiter.
Zur Auflisten der Hintergrundprozesse:
```
$ screen -ls
```

Um alle Screen Prozesse zu schließen:
```
$ pkill screen
```

## Wichtig

Um den Server zu beenden `Strg`+`d` 
