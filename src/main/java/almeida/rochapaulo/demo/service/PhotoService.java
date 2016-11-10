package almeida.rochapaulo.demo.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.common.util.concurrent.ListenableFuture;

import almeida.rochapaulo.demo.api.requests.CreatePhotoRequest;
import almeida.rochapaulo.demo.api.requests.RatePhotoRequest;
import almeida.rochapaulo.demo.api.responses.CreatePhotoResponse;
import almeida.rochapaulo.demo.api.responses.RatePhotoResponse;
import almeida.rochapaulo.demo.dao.accessor.PhotosAccessor;
import almeida.rochapaulo.demo.entities.LatestPhotos;
import almeida.rochapaulo.demo.entities.Photo;
import almeida.rochapaulo.demo.entities.PhotoLookupByRank;
import almeida.rochapaulo.demo.entities.PhotosLookupByUserID;
import almeida.rochapaulo.demo.entities.Thumbnail;
import almeida.rochapaulo.demo.service.query.PhotoQuery;
import net.coobird.thumbnailator.Thumbnails;

/**
 * 
 * @author rochapaulo
 *
 */
public class PhotoService {

    static final String DATE_PATTERN = "ddMMyyyy";
    
    private final PhotosAccessor accessor;
    private final Mapper<Photo> photoMapper;
    private final Mapper<LatestPhotos> latestPhotoMapper;
    private final Mapper<PhotosLookupByUserID> photoByUserIdMapper;
    private final Mapper<Thumbnail> thumbnailMapper;
    
    @Autowired
    public PhotoService(MappingManager manager) {
        
        accessor = manager.createAccessor(PhotosAccessor.class);
        photoMapper = manager.mapper(Photo.class);
        latestPhotoMapper = manager.mapper(LatestPhotos.class);
        photoByUserIdMapper = manager.mapper(PhotosLookupByUserID.class);
        thumbnailMapper = manager.mapper(Thumbnail.class);
    }

    /**
     * 
     * @param request
     * @return
     */
    public CompletableFuture<CreatePhotoResponse> upload(CreatePhotoRequest request) {
        
        return CompletableFuture.supplyAsync(() -> {
           
            final UUID imageUUID = UUID.randomUUID();
            final UUID userUUID = UUID.fromString(request.getUserId());
            final Date today = new Date();
            final byte[] imageBytes = Base64.getDecoder().decode(request.getBase64Image().getBytes());
            
            Photo photo = new Photo();
            photo.setUserId(userUUID);
            photo.setUuid(imageUUID);
            photo.setName(request.getName());
            photo.setDescription(request.getDescription());
            photo.setTags(request.getTags());
            photo.setAddedDate(today);
            photo.setBytes(ByteBuffer.wrap(imageBytes));

            LatestPhotos latestPhoto = new LatestPhotos();
            latestPhoto.setPhotoId(imageUUID);
            latestPhoto.setPhotoName(request.getName());
            latestPhoto.setAddedDate(today);
            
            final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            latestPhoto.setDdMMyyyy(dateFormat.format(today));

            PhotosLookupByUserID byUserId = new PhotosLookupByUserID();
            byUserId.setUserId(userUUID);
            byUserId.setPhotoId(imageUUID);
            byUserId.setPhotoName(request.getName());
            byUserId.setAddedDate(today);

            Thumbnail thumbnail = new Thumbnail();
            thumbnail.setBytes(ByteBuffer.wrap(getThumbnail(imageBytes)));
            thumbnail.setUuid(imageUUID);
            
            photoMapper.save(photo);
            latestPhotoMapper.save(latestPhoto);
            photoByUserIdMapper.save(byUserId);
            thumbnailMapper.save(thumbnail);

            accessor.insertUnrated(imageUUID);
            
            return new CreatePhotoResponse(imageUUID);
        });
        
    }
    
    private static byte[] getThumbnail(byte[] imageBytes) {
        
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            
            Thumbnails
                .of(new ByteArrayInputStream(imageBytes))
                .outputQuality(0.85)
                .size(400, 400)
                .toOutputStream(baos);
            
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * @param request
     * @return
     */
    public CompletableFuture<RatePhotoResponse> rate(RatePhotoRequest request) {
        
        return CompletableFuture.supplyAsync(() -> {
          
            UUID uuid = UUID.fromString(request.getPhotoUUID());
            PhotoLookupByRank rated = accessor.ratePhoto(uuid, request.getRating()).one();
            
            return new RatePhotoResponse(rated.getStars(), rated.getVotes());
        });
    }
    
    /**
     * 
     * @param photoID
     * @return
     */
    public CompletableFuture<Thumbnail> findThumbnailByPhotoID(UUID photoID) {
        
        return CompletableFuture.supplyAsync(() -> thumbnailMapper.get(photoID));
    }
    
    /**
     * 
     * @param query
     * @return
     */
    public ListenableFuture<List<Photo>> findPhotosBy(PhotoQuery query) {
        
        return query.execute();
    }
    
}
