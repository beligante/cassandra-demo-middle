package almeida.rochapaulo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.api.service.query.QueryFactory;
import almeida.rochapaulo.demo.dao.BucketRepository;
import almeida.rochapaulo.demo.service.PhotoService;
import almeida.rochapaulo.demo.service.UserManagement;

/**
 * 
 * @author rochapaulo
 *
 */
@SpringBootApplication
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

    @Bean
    public Session session() {
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withClusterName("Test Cluster").build();
        return cluster.connect("demo");
    }

    @Bean
    public MappingManager mappingManager(Session session) {
        return new MappingManager(session);
    }

    @Bean
    public UserManagement userManagement(MappingManager manager) {
        return new UserManagement(manager);
    }

    @Bean
    public PhotoService photoService(MappingManager manager) {
        return new PhotoService(manager);
    }

    @Bean
    public BucketRepository imageDAO(MappingManager manager) {
        return new BucketRepository(manager);
    }

    @Bean
    public QueryFactory queryFactory(MappingManager manager) {
        return new QueryFactory(manager);
    }

}
