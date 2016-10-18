package almeida.rochapaulo.demo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.api.requests.CreatePhoto;
import almeida.rochapaulo.demo.entities.LatestPhotos;
import almeida.rochapaulo.demo.entities.Photo;
import almeida.rochapaulo.demo.entities.PhotosByUserID;

public class PhotoService {

	private Mapper<Photo> photoMapper;
	private Mapper<LatestPhotos> latestPhotoMapper;
	private Mapper<PhotosByUserID> photoByUserIdMapper;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
	
	@Autowired
	public PhotoService(MappingManager manager) {
		photoMapper = manager.mapper(Photo.class);
		latestPhotoMapper = manager.mapper(LatestPhotos.class);
		photoByUserIdMapper = manager.mapper(PhotosByUserID.class);
	}
	
	public void save(UUID userId, CreatePhoto request) {

		UUID imageUUID = UUID.randomUUID();
		Date today = new Date();
		
		Photo photo = new Photo();
		photo.setUserId(userId);
		photo.setUuid(imageUUID);
		photo.setName(request.getName());
		photo.setDescription(request.getDescription());
		photo.setLocation(request.getLocation());
		photo.setTags(request.getTags());
		photo.setAddedDate(today);
		photo.setBase64Image(request.getBase64Image());
		
		photoMapper.save(photo);
		
		
		LatestPhotos latestPhoto = new LatestPhotos();
		latestPhoto.setPhotoId(imageUUID);
		latestPhoto.setPhotoName(request.getName());
		latestPhoto.setAddedDate(today);
		latestPhoto.setDdMMyyyy(dateFormat.format(today));
		
		latestPhotoMapper.save(latestPhoto);
		
		
		PhotosByUserID byUserId = new PhotosByUserID();
		byUserId.setUserId(userId);
		byUserId.setPhotoId(imageUUID);
		byUserId.setPhotoName(request.getName());
		byUserId.setAddedDate(today);
		
		photoByUserIdMapper.save(byUserId);
		
	}

}
