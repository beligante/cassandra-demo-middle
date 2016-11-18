package almeida.rochalabs.demo.data.query;

import java.util.List;

import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochalabs.demo.data.entities.UserProfile;

/**
 * 
 * @author rochapaulo
 *
 */
public interface ProfileQuery {

    ListenableFuture<List<UserProfile>> execute();

}
