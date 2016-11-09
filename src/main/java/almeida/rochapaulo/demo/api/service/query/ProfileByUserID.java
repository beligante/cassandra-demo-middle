package almeida.rochapaulo.demo.api.service.query;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.entities.UserProfile;

/**
 * 
 * @author rochapaulo
 *
 */
public class ProfileByUserID implements ProfileQuery {

	private final Mapper<UserProfile> profileMapper;
	private final UUID uuid;
	
	public ProfileByUserID(MappingManager manager, UUID uuid) {
		profileMapper = manager.mapper(UserProfile.class);
		this.uuid = uuid;
	}
	
	@Override
	public CompletableFuture<UserProfile> execute() {
		
		return CompletableFuture.supplyAsync(() -> profileMapper.get(uuid));
	}

}
