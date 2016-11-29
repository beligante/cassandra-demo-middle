package almeida.rochalabs.demo.api;

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import almeida.rochalabs.demo.api.requests.CreatePhotoRequest;
import almeida.rochalabs.demo.api.responses.CreatePhotoResponse;
import almeida.rochalabs.demo.api.responses.PhotoMetadata;
import almeida.rochalabs.demo.data.entities.Photo;
import almeida.rochalabs.demo.data.query.QueryFactory;
import almeida.rochalabs.demo.data.service.PhotoService;

/**
 * 
 * @author rochapaulo
 *
 */
@RestController
public class PhotoRS {

    private final QueryFactory queryFactory;
    private final PhotoService photoService;

    @Autowired
    public PhotoRS(PhotoService photoService, QueryFactory queryFactory) {
        this.photoService = photoService;
        this.queryFactory = queryFactory;
    }

    @RequestMapping(
            path = "/api/secure/photos", 
            method = RequestMethod.POST, 
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> upload(@RequestBody CreatePhotoRequest request) throws Exception {
        
        CreatePhotoResponse created = photoService.upload(request).get();
        return ResponseEntity.created(new URI("/photos/" + created.getPhotoUUID())).build();
    }

    
    @RequestMapping(
            path = "/api/secure/photos", 
            method = RequestMethod.GET, 
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getPhotos() throws Exception {
        
        final List<PhotoMetadata> photos = 
            photoService.findPhotosBy(queryFactory.allPhotos())
                .get()
                .parallelStream()
                .map(p -> {
                    PhotoMetadata meta = new PhotoMetadata();
                    meta.setDescription(p.getDescription());
                    meta.setName(p.getName());
                    meta.setUser(p.getUserId());
                    meta.setTags(p.getTags());
                    meta.setLocation("/api/secure/bucket/images/" + p.getUuid());
                    return meta;
                }).collect(toList());

        if (photos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(photos);
    }

    
    @RequestMapping(
            path = "/api/secure/photos/{photoId}", 
            method = RequestMethod.GET, 
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getPhoto(@PathVariable String photoId) throws Exception {
        
        final List<Photo> photos = photoService.findPhotosBy(queryFactory.photoByUUID(UUID.fromString(photoId))).get();
        if (photos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Photo photo = photos.get(0);
        PhotoMetadata meta = new PhotoMetadata();
        meta.setDescription(photo.getDescription());
        meta.setName(photo.getName());
        meta.setUser(photo.getUserId());
        meta.setTags(photo.getTags());
        meta.setLocation("/api/secure/bucket/images/" + photo.getUuid());

        return ResponseEntity.ok(meta);
    }
    

    @RequestMapping(
            path = "/api/secure/photos/user/{userId}", 
            method = RequestMethod.GET, 
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getPhotos(@PathVariable String userId) throws Exception {
        
        final List<Photo> photos = photoService.findPhotosBy(queryFactory.photosByUserUUID(UUID.fromString(userId))).get();
        if (photos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<PhotoMetadata> response = 
            photos.parallelStream()
                .map(p -> {
                    PhotoMetadata meta = new PhotoMetadata();
                    meta.setDescription(p.getDescription());
                    meta.setName(p.getName());
                    meta.setUser(p.getUserId());
                    meta.setTags(p.getTags());
                    meta.setLocation("/api/secure/bucket/images/" + p.getUuid());
                    return meta;
                }).collect(toList());
        
        return ResponseEntity.ok(response);
    }

}
