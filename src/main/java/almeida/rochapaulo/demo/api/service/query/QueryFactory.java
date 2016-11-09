package almeida.rochapaulo.demo.api.service.query;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.MappingManager;

/**
 * 
 * @author rochapaulo
 *
 */
public class QueryFactory {

	private MappingManager manager;
	
	@Autowired
	public QueryFactory(MappingManager manager) {
		this.manager = manager;
	}
	
	public ProfileQuery profileByUUID(UUID userUUID) {
		return new ProfileByUserID(manager, userUUID);
	}
	
	public ProfileQuery allProfiles() {
		return new AllProfiles(manager);
	}
}
