package almeida.rochapaulo.demo.data.entities;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Data;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
@Table(name = "thumbnail")
public class Thumbnail {

    @PartitionKey
    @Column(name = "photo_id")
    private UUID uuid;

    @Column(name = "bytes")
    private ByteBuffer bytes;

}
