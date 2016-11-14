package almeida.rochapaulo.demo.data.query;

import java.util.List;

import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochapaulo.demo.data.entities.Photo;

/**
 * 
 * @author rochapaulo
 *
 */
public interface PhotoQuery {

    ListenableFuture<List<Photo>> execute();
    
}
