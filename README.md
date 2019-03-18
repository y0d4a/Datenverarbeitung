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


## Wichtig

Um den Server zu beenden `Strg`+`d` 
