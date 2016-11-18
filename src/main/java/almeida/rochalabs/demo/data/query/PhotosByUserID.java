package almeida.rochalabs.demo.data.query;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.Futures.transform;
import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochalabs.demo.data.accessor.PhotosAccessor;
import almeida.rochalabs.demo.data.entities.Photo;
import almeida.rochalabs.demo.data.entities.PhotosLookupByUserID;

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
    public ListenableFuture<List<Photo>> execute() {
        
        return transform(accessor.getByUserIdAsync(uuid), map2Photos());
    }

    private Function<Result<PhotosLookupByUserID>, List<Photo>> map2Photos() {
        
        return (Function<Result<PhotosLookupByUserID>, List<Photo>>) lookupKeys -> {
            
            final List<Photo> photos = synchronizedList(new ArrayList<>());
            for (PhotosLookupByUserID lookupKey : lookupKeys) {
                addCallback(photoMapper.getAsync(lookupKey.getPhotoId()), aggregate(photos));
            }
            
            return photos;
        };
    }

    private FutureCallback<Photo> aggregate(final List<Photo> photos) {
        
        return new FutureCallback<Photo>() {

            @Override
            public void onSuccess(Photo photo) {
                photos.add(photo);
            }

            @Override
            public void onFailure(Throwable throwable) { }
            
        };
    }

}
