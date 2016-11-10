package almeida.rochapaulo.demo.service.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

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
    public ListenableFuture<List<Photo>> execute() {

        return Futures.transform(accessor.getAllAsync(), wrap2List());
    }

    private Function<Result<Photo>, List<Photo>> wrap2List() {

        final List<Photo> result = Collections.synchronizedList(new ArrayList<>());
        return (Function<Result<Photo>, List<Photo>>) photos -> {
            
            photos.forEach(result::add);
            return result;
        };
    }

}
