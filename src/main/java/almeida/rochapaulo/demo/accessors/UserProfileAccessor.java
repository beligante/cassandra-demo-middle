package almeida.rochapaulo.demo.accessors;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.driver.mapping.annotations.QueryParameters;
import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochapaulo.demo.entities.UserProfile;

@Accessor
public interface UserProfileAccessor {

	@Query("SELECT * FROM user_profile")
	Result<UserProfile> getAll();
	
	@Query("SELECT * FROM user_profile")
	@QueryParameters(consistency="QUORUM")
	ListenableFuture<Result<UserProfile>> getAllAsync();
	
}
