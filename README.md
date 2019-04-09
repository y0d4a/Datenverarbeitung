# ECCO-Project: Datenverarbeitung

## Verwendete Frameworks

- Scalatra
- Apache Spark
- MongoDB
  - MongoSpark Connector


## Deployment
```
$ git clone https://github.com/htw-wise-2018/Datenverarbeitung.git
$ cd Datenverarbeitung/floatbackendapp
$ screen -S ecco_datenverarbeitung
$ sbt clean
$ sbt package
$ sbt
$ jetty:start
```

Nach dem Start sollte keine Tastatureingabe erfolgen, sonst schließt sich das Programm.  
Konsole schließen empfohlen.

## Neuladen der Anwendung
```
$ sbt
> ~;jetty:stop;jetty:start
```


Nach dem Screen Befehl kann die Console geschlossen werden und der Prozess läuft weiter.
Zum Auflisten der Hintergrundprozesse:
```
$ screen -ls
```

Um wieder zum Prozess zu gelangen:
```
screen -r XXXX.ecco_datenverarbeitung
```

Um alle Screen Prozesse zu schließen:
```
$ pkill screen
```

## Wichtig

Um den Server zu beenden `Strg`+`d` 
