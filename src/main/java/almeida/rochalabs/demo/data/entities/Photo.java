package almeida.rochalabs.demo.data.entities;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Set;
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
@Table(name = "photos")
public class Photo {

    @PartitionKey
    @Column(name = "photo_id")
    private UUID uuid;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "bytes")
    private ByteBuffer bytes;

    @Column(name = "tags")
    private Set<String> tags;

    @Column(name = "added_date")
    private Date addedDate;

}
