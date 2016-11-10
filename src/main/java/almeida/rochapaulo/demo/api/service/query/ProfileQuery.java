package almeida.rochapaulo.demo.api.service.query;

import java.util.List;

import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochapaulo.demo.entities.UserProfile;

/**
 * 
 * @author rochapaulo
 *
 */
public interface ProfileQuery {

    ListenableFuture<List<UserProfile>> execute();

}
