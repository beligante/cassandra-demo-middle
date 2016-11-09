package almeida.rochapaulo.demo.api.service.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import almeida.rochapaulo.demo.entities.Photo;

/**
 * 
 * @author rochapaulo
 *
 */
public interface PhotoQuery {

    CompletableFuture<List<Photo>> execute();
    
}
