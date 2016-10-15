package almeida.rochapaulo.cassandra.demo.store;

import java.util.UUID;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import almeida.rochapaulo.cassandra.demo.conf.DemoModule;
import almeida.rochapaulo.cassandra.demo.entity.User;
import almeida.rochapaulo.cassandra.demo.repository.UserRepository;

/**
 * 
 * @author rochapaulo
 *
 */
public class UserStoreIT {

	@Test
	public void save() {

		Injector injector = Guice.createInjector(new DemoModule());
		UserRepository repository = injector.getInstance(UserRepository.class);
		
		User user = new User();
		user.setUuid(UUID.randomUUID());
		user.setFirstName("Paulo Ricardo");
		user.setLastName("Rocha de Almeida");
		user.setUsername("rochapaulo");
		
		repository.save(user);
	}
	
}
