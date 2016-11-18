package almeida.rochalabs.demo.data.entities;

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
@Table(name = "photo_rank")
public class PhotoLookupByRank {

    @ClusteringColumn
    @Column(name = "photo_id")
    private UUID photoId;

    @PartitionKey
    @Column(name = "stars")
    private int stars = 0;

    @Column(name = "votes")
    private long votes = 0;

}
