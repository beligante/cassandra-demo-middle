package almeida.rochapaulo.demo.api.service.query;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

import almeida.rochapaulo.demo.dao.accessor.PhotosAccessor;
import almeida.rochapaulo.demo.entities.Photo;
import almeida.rochapaulo.demo.entities.PhotosLookupByUserID;

/**
 * 
 * @author rochapaulo
 *
 */
public class PhotosByUserID implements PhotoQuery {

    private final PhotosAccessor accessor;
    private final Mapper<Photo> photoMapper;
    private final UUID uuid;
    
    public PhotosByUserID(MappingManager manager, UUID userUUID) {
        
        accessor = manager.createAccessor(PhotosAccessor.class);
        photoMapper = manager.mapper(Photo.class);
        uuid = userUUID;
    }

    @Override
    public CompletableFuture<List<Photo>> execute() {
        
        return CompletableFuture.supplyAsync(() -> {
          
            final Result<PhotosLookupByUserID> resultset = accessor.getByUserId(uuid);
            final List<PhotosLookupByUserID> lookupKeys = resultset.all();
            
            final List<Photo> photos = Collections.synchronizedList(new ArrayList<>(lookupKeys.size()));
            for (PhotosLookupByUserID lookupKey : lookupKeys) {
                
                supplyAsync(() -> photoMapper.get(lookupKey.getPhotoId()))
                    .thenAccept(photo -> {
                        photos.add(photo);
                    });
                
                photos.add(photoMapper.get(lookupKey.getPhotoId()));
            }
            
            return photos;
        });
    }

}
