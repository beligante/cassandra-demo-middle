package almeida.rochapaulo.demo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

import almeida.rochapaulo.demo.accessors.PhotosAccessor;
import almeida.rochapaulo.demo.api.requests.CreatePhoto;
import almeida.rochapaulo.demo.entities.LatestPhotos;
import almeida.rochapaulo.demo.entities.Photo;
import almeida.rochapaulo.demo.entities.PhotoRank;
import almeida.rochapaulo.demo.entities.PhotosByUserID;

public class PhotoService {

	private final MappingManager manager;
	private final Mapper<Photo> photoMapper;
	private final Mapper<LatestPhotos> latestPhotoMapper;
	private final Mapper<PhotosByUserID> photoByUserIdMapper;
	private final Mapper<PhotoRank> photoRankMapper;
	private final PhotosAccessor photosAccessor;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
	
	@Autowired
	public PhotoService(MappingManager manager) {
		this.manager = manager;
		photoMapper = manager.mapper(Photo.class);
		latestPhotoMapper = manager.mapper(LatestPhotos.class);
		photoByUserIdMapper = manager.mapper(PhotosByUserID.class);
		photoRankMapper = manager.mapper(PhotoRank.class);
		photosAccessor = manager.createAccessor(PhotosAccessor.class);
	}
	
	public void save(UUID userId, CreatePhoto request) {

		final UUID imageUUID = UUID.randomUUID();
		final Date today = new Date();
		final String imageLocation = insertToS3Bucket(request.getBase64Image());
		final BatchStatement batch = new BatchStatement();
		
		Photo photo = new Photo();
		photo.setUserId(userId);
		photo.setUuid(imageUUID);
		photo.setName(request.getName());
		photo.setDescription(request.getDescription());
		photo.setTags(request.getTags());
		photo.setAddedDate(today);
		photo.setLocation(imageLocation);
		
		LatestPhotos latestPhoto = new LatestPhotos();
		latestPhoto.setPhotoId(imageUUID);
		latestPhoto.setPhotoName(request.getName());
		latestPhoto.setAddedDate(today);
		latestPhoto.setDdMMyyyy(dateFormat.format(today));
		
		PhotosByUserID byUserId = new PhotosByUserID();
		byUserId.setUserId(userId);
		byUserId.setPhotoId(imageUUID);
		byUserId.setPhotoName(request.getName());
		byUserId.setAddedDate(today);
		byUserId.setLocation(imageLocation);
		
		PhotoRank photoRank = new PhotoRank();
		photoRank.setPhotoId(imageUUID);
		
		batch.add(photoMapper.saveQuery(photo));
		batch.add(latestPhotoMapper.saveQuery(latestPhoto));
		batch.add(photoByUserIdMapper.saveQuery(byUserId));
		batch.add(photoRankMapper.saveQuery(photoRank));
		
		Session session = manager.getSession();
		session.execute(batch);
	}
	
	private String insertToS3Bucket(String base64Image) {
		
		/* TODO
		 * Integrate with AWS S3 bucket
		 */
		
		return "";
	}

	public void ratePhoto(UUID photoId, String stars) {
		photosAccessor.ratePhoto(photoId, stars);
	}
	
	public List<Photo> getAllPhotos() {
		Result<Photo> photos = photosAccessor.getAll();
		return photos.all();
	}

	public List<PhotosByUserID> findPhotosByUserId(UUID userId) {
		Result<PhotosByUserID> userPhotos = photosAccessor.getByUserId(userId);
		return userPhotos.all();
	}

	public Photo findPhotoById(UUID photoId) {
		return photoMapper.get(photoId);
	}
	
}
