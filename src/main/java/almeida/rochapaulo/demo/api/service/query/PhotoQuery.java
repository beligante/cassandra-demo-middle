package almeida.rochapaulo.demo.api.service.query;

import java.util.List;

import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochapaulo.demo.entities.Photo;

/**
 * 
 * @author rochapaulo
 *
 */
public interface PhotoQuery {

    ListenableFuture<List<Photo>> execute();
    
}
