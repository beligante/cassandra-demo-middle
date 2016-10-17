package almeida.rochapaulo.demo.conf;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import almeida.rochapaulo.demo.service.PhotoService;
import almeida.rochapaulo.demo.service.UserManagement;

public class Bootstrap extends AbstractModule {

	@Override
	protected void configure() {
		bind(UserManagement.class);
		bind(PhotoService.class);
	}
	
	@Provides
	public Session session() {
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withClusterName("Test Cluster").build();
		return cluster.connect("demo");
	}
	
	@Provides
	@Singleton
	public MappingManager mappingManager(Session session) {
		return new MappingManager(session);
	}

}
