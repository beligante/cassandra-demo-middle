package almeida.rochapaulo.demo.dao.accessor;

import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochapaulo.demo.entities.Photo;
import almeida.rochapaulo.demo.entities.PhotoLookupByRank;
import almeida.rochapaulo.demo.entities.PhotosLookupByUserID;

/**
 * 
 * @author rochapaulo
 *
 */
@Accessor
public interface PhotosAccessor {

    @Query("SELECT * FROM photos")
    Result<Photo> getAll();

    @Query("SELECT * FROM photos")
    ListenableFuture<Result<Photo>> getAllAsync();

    @Query("SELECT * FROM photos_by_user_id WHERE user_id = ?")
    Result<PhotosLookupByUserID> getByUserId(UUID userId);

    @Query("SELECT * FROM photos_by_user_id WHERE user_id = ?")
    ListenableFuture<Result<PhotosLookupByUserID>> getByUserIdAsync(UUID userId);
    
    @Query("UPDATE photo_rank SET votes = votes + 1 WHERE photo_id = ? AND stars = ?")
    Result<PhotoLookupByRank> ratePhoto(UUID photoId, String stars);

    @Query("UPDATE photo_rank SET votes = votes + 0 WHERE photo_id = ? AND stars = 'UNRATED'")
    Result<PhotoLookupByRank> insertUnrated(UUID photoId);

}