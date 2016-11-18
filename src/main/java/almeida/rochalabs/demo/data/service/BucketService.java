package almeida.rochalabs.demo.data.service;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import almeida.rochalabs.demo.data.entities.Photo;
import almeida.rochalabs.demo.data.entities.Thumbnail;

/**
 * 
 * @author rochapaulo
 *
 */
public class BucketService {

    private final Mapper<Photo> photoMapper;
    private final Mapper<Thumbnail> thumbnailMapper;

    @Autowired
    public BucketService(MappingManager manager) {
        
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
