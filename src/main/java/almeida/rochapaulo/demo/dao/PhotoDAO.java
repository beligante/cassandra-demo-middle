package almeida.rochapaulo.demo.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

import almeida.rochapaulo.demo.api.requests.CreatePhoto;
import almeida.rochapaulo.demo.dao.accessor.PhotosAccessor;
import almeida.rochapaulo.demo.entities.LatestPhotos;
import almeida.rochapaulo.demo.entities.Photo;
import almeida.rochapaulo.demo.entities.PhotosByUserID;
import almeida.rochapaulo.demo.entities.Thumbnail;
import net.coobird.thumbnailator.Thumbnails;

public class PhotoDAO {

	private final Mapper<Photo> photoMapper;
	private final Mapper<LatestPhotos> latestPhotoMapper;
	private final Mapper<PhotosByUserID> photoByUserIdMapper;
	private final Mapper<Thumbnail> thumbnailMapper;
	private final PhotosAccessor photosAccessor;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
	
	@Autowired
	public PhotoDAO(MappingManager manager) {
		photoMapper = manager.mapper(Photo.class);
		latestPhotoMapper = manager.mapper(LatestPhotos.class);
		photoByUserIdMapper = manager.mapper(PhotosByUserID.class);
		thumbnailMapper = manager.mapper(Thumbnail.class);
		photosAccessor = manager.createAccessor(PhotosAccessor.class);
	}
	
	public Photo save(CreatePhoto request) {

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
		latestPhoto.setDdMMyyyy(dateFormat.format(today));
		
		PhotosByUserID byUserId = new PhotosByUserID();
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
		
		photosAccessor.insertUnrated(imageUUID);
		
		return photo;
	}
	
	private static byte[] getThumbnail(byte[] imageBytes) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Thumbnails.of(new ByteArrayInputStream(imageBytes)).outputQuality(0.85).size(400, 400).toOutputStream(baos);
			return baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
