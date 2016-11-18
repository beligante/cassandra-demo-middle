package almeida.rochalabs.demo.data.entities;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
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
@Table(name = "photos_by_user_id")
public class PhotosLookupByUserID {

    @PartitionKey
    @Column(name = "user_id")
    private UUID userId;

    @ClusteringColumn(value = 0)
    @Column(name = "added_date")
    private Date addedDate;

    @ClusteringColumn(value = 1)
    @Column(name = "photo_id")
    private UUID photoId;

    @Column(name = "photo_name")
    private String photoName;

}