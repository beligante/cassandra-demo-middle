package almeida.rochapaulo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.dao.PhotoDAO;
import almeida.rochapaulo.demo.dao.UserDAO;

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
	public UserDAO userManagement(MappingManager manager) {
		return new UserDAO(manager);
	}
	
	@Bean
	public PhotoDAO photoService(MappingManager manager) {
		return new PhotoDAO(manager);
	}
	
}
