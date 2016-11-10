package almeida.rochapaulo.demo.service.query;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.MappingManager;

/**
 * 
 * @author rochapaulo
 *
 */
public class QueryFactory {

    private final MappingManager manager;

    @Autowired
    public QueryFactory(MappingManager manager) {
        
        this.manager = manager;
    }

    public ProfileQuery profileByUUID(UUID userUUID) {
        return new ProfileByUserID(manager, userUUID);
    }

    public ProfileQuery allProfiles() {
        return new AllProfiles(manager);
    }
    
    public PhotoQuery photoByUUID(UUID photoUUID) {
        return new PhotoByPhotoID(manager, photoUUID);
    }
    
    public PhotoQuery photosByUserUUID(UUID userUUID) {
        return new PhotosByUserID(manager, userUUID);
    }
    
    public PhotoQuery allPhotos() {
        return new AllPhotos(manager);
    }
    
}
