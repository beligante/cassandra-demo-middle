![Alt text](/src/main/resources/cassandra/diagram.png?raw=true "Modeling Diagram")

### Libraries
* [cassandra maven plugin] 3.5
* [cassandra driver core] 3.1.0
* [cassandra driver mapping core]: 3.1.0


[cassandra maven plugin]: <https://mvnrepository.com/artifact/org.codehaus.mojo/cassandra-maven-plugin/3.5>
[cassandra driver core]: <https://mvnrepository.com/artifact/com.datastax.cassandra/cassandra-driver-core/3.1.0>
[cassandra driver mapping core]: <https://mvnrepository.com/artifact/com.datastax.cassandra/cassandra-driver-mapping/3.1.0>

### Running
1) Build cassandra-demo-middle docker image
```sh
$ mvn clean install
$ docker build -t cassandrademomiddle .
```

2) Start cassandra container
```sh
$ docker run --name demo-cassandra -d cassandra:3.9
```

3) Start cassandra-demo-middle container
```sh
$ docker run --link demo-cassandra:cassandra -p 8080:8080 cassandrademomiddle $(docker inspect --format='{{ .NetworkSettings.IPAddress }}' demo-cassandra)
```

### API (REST: Postman Collection)
* /postman/CassandraDemo_v2.postman_collection
