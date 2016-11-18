package almeida.rochalabs.demo.data.query;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochalabs.demo.data.entities.Photo;

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
    public ListenableFuture<List<Photo>> execute() {
        
        return Futures.transform(photoMapper.getAsync(uuid), wrap2List());
    }

    private Function<Photo, List<Photo>> wrap2List() {
        
        return (Function<Photo, List<Photo>>) photo -> Arrays.asList(photo);
    }

}
