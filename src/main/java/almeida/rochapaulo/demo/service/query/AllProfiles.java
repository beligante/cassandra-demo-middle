package almeida.rochapaulo.demo.service.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochapaulo.demo.data.accessor.UserProfileAccessor;
import almeida.rochapaulo.demo.data.entities.UserProfile;

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
    public ListenableFuture<List<UserProfile>> execute() {

        return Futures.transform(accessor.getAllAsync(), wrap2List());
    }

    private Function<Result<UserProfile>, List<UserProfile>> wrap2List() {

        final List<UserProfile> result = Collections.synchronizedList(new ArrayList<>());
        return (Function<Result<UserProfile>, List<UserProfile>>) profile -> {
            
            profile.forEach(result::add);
            return result;
        };
    }
}
