package almeida.rochalabs.demo.data.query;

import static java.util.concurrent.Executors.newCachedThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

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
    private final ListeningExecutorService service = MoreExecutors.listeningDecorator(newCachedThreadPool());
    
    public PhotosByUserID(MappingManager manager, UUID userUUID) {
        
        accessor = manager.createAccessor(PhotosAccessor.class);
        photoMapper = manager.mapper(Photo.class);
        uuid = userUUID;
    }

    @Override
    public ListenableFuture<List<Photo>> execute() {
        
    	return service.submit(() -> {
    		
    		final Result<PhotosLookupByUserID> result = accessor.getByUserId(uuid);
    		
    		final List<Photo> photos = new ArrayList<>();
    		result.forEach(each -> {
    			photos.add(photoMapper.get(each.getPhotoId()));
    		});
    		
    		return photos;
    	});
    			
    }

}
