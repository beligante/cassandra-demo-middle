![Alt text](/src/cassandra/diagram.png?raw=true "Modeling Diagram")

### Libraries
* [cassandra maven plugin] 3.5
* [cassandra driver core] 3.1.0
* [cassandra driver mapping core]: 3.1.0


[cassandra maven plugin]: <https://mvnrepository.com/artifact/org.codehaus.mojo/cassandra-maven-plugin/3.5>
[cassandra driver core]: <https://mvnrepository.com/artifact/com.datastax.cassandra/cassandra-driver-core/3.1.0>
[cassandra driver mapping core]: <https://mvnrepository.com/artifact/com.datastax.cassandra/cassandra-driver-mapping/3.1.0>

### Installation
First:
```sh
$ mvn clean install
```
Second:
```sh
$ mvn cassandra:run
$ mvn cassandra:cql-exec
$ mvn spring-boot:run
```

### API (REST: Postman Collection)
* /postman/CassandraDemo_v2.postman_collection