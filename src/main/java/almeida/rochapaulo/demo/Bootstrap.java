package almeida.rochapaulo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.api.service.UserManagement;
import almeida.rochapaulo.demo.dao.ImageDAO;
import almeida.rochapaulo.demo.dao.PhotoDAO;

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
	public PhotoDAO photoDAO(MappingManager manager) {
		return new PhotoDAO(manager);
	}
	
	@Bean
	public ImageDAO imageDAO(MappingManager manager) {
		return new ImageDAO(manager);
	}
	
}
