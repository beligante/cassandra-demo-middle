package almeida.rochapaulo.cassandra.demo.conf;

import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.AtomicMonotonicTimestampGenerator;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TimestampGenerator;
import com.datastax.driver.mapping.MappingManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import almeida.rochapaulo.cassandra.demo.entity.User;
import almeida.rochapaulo.cassandra.demo.lookuptables.LookupTable;
import almeida.rochapaulo.cassandra.demo.lookuptables.UserByUserID;
import almeida.rochapaulo.cassandra.demo.lookuptables.UserByUsername;
import almeida.rochapaulo.cassandra.demo.repository.UserRepository;

/**
 * 
 * @author rochapaulo
 *
 */
public class DemoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UserRepository.class);
	}
	
	@Provides
	public Session session() {
		Cluster cluster = 
				Cluster.builder()
					.addContactPoint("127.0.0.1")
					.withClusterName("Test Cluster")
					.build();
		return cluster.connect("demo");
	}
	
	@Provides
	@Singleton
	public TimestampGenerator timestampGenerator() {
		return new AtomicMonotonicTimestampGenerator();
	}
	
	@Provides
	@Singleton
	public MappingManager mappingManager(Session session) {
		return new MappingManager(session);
	}
	
	@Provides
	@Singleton
	@Named("UserTables")
	public List<LookupTable<User>> userTables(MappingManager mappingManager) {
		List<LookupTable<User>> tables = new ArrayList<LookupTable<User>>();
		tables.add(new UserByUserID(mappingManager));
		tables.add(new UserByUsername(mappingManager));
		return tables;
	}

}
