package almeida.rochapaulo.demo.api.service.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.dao.accessor.UserProfileAccessor;
import almeida.rochapaulo.demo.entities.UserProfile;

/**
 * 
 * @author rochapaulo
 *
 */
public class AllProfiles implements ProfileQuery {

    private final UserProfileAccessor accessor;

    public AllProfiles(MappingManager manager) {
        
        accessor = manager.createAccessor(UserProfileAccessor.class);
    }

    @Override
    public CompletableFuture<List<UserProfile>> execute() {

        return CompletableFuture.supplyAsync(() -> accessor.getAll().all());
    }

}
