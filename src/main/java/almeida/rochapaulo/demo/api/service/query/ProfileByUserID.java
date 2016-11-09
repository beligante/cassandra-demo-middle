package almeida.rochapaulo.demo.api.service.query;

import java.util.Arrays;
import java.util.List;
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
    public CompletableFuture<List<UserProfile>> execute() {

        return CompletableFuture.supplyAsync(() -> Arrays.asList(profileMapper.get(uuid)));
    }

}
