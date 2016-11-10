package almeida.rochapaulo.demo.api.service.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.dao.accessor.PhotosAccessor;
import almeida.rochapaulo.demo.entities.Photo;

/**
 * 
 * @author rochapaulo
 *
 */
public class AllPhotos implements PhotoQuery {

    private final PhotosAccessor accessor;

    public AllPhotos(MappingManager manager) {
        
        accessor = manager.createAccessor(PhotosAccessor.class);
    }
    
    @Override
    public CompletableFuture<List<Photo>> execute() {
        
        return CompletableFuture.supplyAsync(() -> accessor.getAll().all());
    }

}
