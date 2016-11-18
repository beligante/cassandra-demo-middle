package almeida.rochalabs.demo.data.accessor;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.driver.mapping.annotations.QueryParameters;
import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochalabs.demo.data.entities.UserProfile;

/**
 * 
 * @author rochapaulo
 *
 */
@Accessor
public interface UserProfileAccessor {

    @Query("SELECT * FROM users")
    Result<UserProfile> getAll();

    @Query("SELECT * FROM users")
    @QueryParameters(consistency = "QUORUM")
    ListenableFuture<Result<UserProfile>> getAllAsync();

}
