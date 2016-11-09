package almeida.rochapaulo.demo.api.service.query;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.entities.Photo;

/**
 * 
 * @author rochapaulo
 *
 */
public class PhotoByPhotoID implements PhotoQuery {

    private final Mapper<Photo> photoMapper;
    private final UUID uuid;

    public PhotoByPhotoID(MappingManager manager, UUID uuid) {
        
        photoMapper = manager.mapper(Photo.class);
        this.uuid = uuid;
    }
    
    @Override
    public CompletableFuture<List<Photo>> execute() {
        
        return CompletableFuture.supplyAsync(() -> Arrays.asList(photoMapper.get(uuid)));
    }

}
