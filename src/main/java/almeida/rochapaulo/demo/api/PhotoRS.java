package almeida.rochapaulo.demo.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import almeida.rochapaulo.demo.api.requests.CreatePhoto;
import almeida.rochapaulo.demo.entities.Photo;
import almeida.rochapaulo.demo.service.PhotoService;

@RestController
public class PhotoRS {

	private final PhotoService photoService;

	@Autowired
	public PhotoRS(PhotoService photoService) {
		this.photoService = photoService;
	}
	
	@RequestMapping(path = "/photos/{userId}", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?> upload(@PathVariable String userId, @RequestBody CreatePhoto request) throws Exception {
		photoService.save(UUID.fromString(userId), request);
		return ResponseEntity.created(new URI("/photos/" + userId)).build();
	}
	
	@RequestMapping(path = "/photos", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getPhotos() {
		final List<Photo> photos = photoService.getAllPhotos();
		if (photos.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(photos);
	}
	
	@RequestMapping(path = "/photos/{photoId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getPhoto(String photoId) {
		final Photo photo = photoService.findPhotoById(UUID.fromString(photoId));
		if (photo == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(photo);
	}

}
