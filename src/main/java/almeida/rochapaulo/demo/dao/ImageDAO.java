package almeida.rochapaulo.demo.dao;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochapaulo.demo.entities.Photo;
import almeida.rochapaulo.demo.entities.Thumbnail;

public class ImageDAO {

	private final Mapper<Photo> photoMapper;
	private final Mapper<Thumbnail> thumbnailMapper;
	
	@Autowired
	public ImageDAO(MappingManager manager) {
		photoMapper = manager.mapper(Photo.class);
		thumbnailMapper = manager.mapper(Thumbnail.class);
	}

	public byte[] getThumbnail(String uuid) {
		
		final Thumbnail photo = thumbnailMapper.get(UUID.fromString(uuid));
		final ByteBuffer buff = photo.getBytes();
		return buff.array();
	}
	
	public byte[] getImage(String uuid) {
		
		final Photo photo = photoMapper.get(UUID.fromString(uuid));
		final ByteBuffer buff = photo.getBytes();
		return buff.array();
	}
	
}
