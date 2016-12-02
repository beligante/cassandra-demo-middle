![Alt text](/src/main/resources/cassandra/diagram.png?raw=true "Modeling Diagram")

### Libraries
* [cassandra maven plugin] 3.5
* [cassandra driver core] 3.1.0
* [cassandra driver mapping core]: 3.1.0


[cassandra maven plugin]: <https://mvnrepository.com/artifact/org.codehaus.mojo/cassandra-maven-plugin/3.5>
[cassandra driver core]: <https://mvnrepository.com/artifact/com.datastax.cassandra/cassandra-driver-core/3.1.0>
[cassandra driver mapping core]: <https://mvnrepository.com/artifact/com.datastax.cassandra/cassandra-driver-mapping/3.1.0>


### Building
1) Build cassandra-demo-middle docker image
```sh
$ mvn clean install
$ docker build -t cassandra-demo-middle .
```

#### Running
```sh
$ docker-compose up -d
```

#### Scaling Up
```sh
$ docker-compose scale middle={nContainers} cassandra={nContainers}
```

### API (REST: Postman Collection)
* /postman/CassandraDemo_v2.postman_collection
