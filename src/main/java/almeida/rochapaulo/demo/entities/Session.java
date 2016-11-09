package almeida.rochapaulo.demo.entities;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
@AllArgsConstructor
@Table(name = "session")
public class Session {

    @PartitionKey
    @Column(name = "session_id")
    private UUID sessionId;
    
    @Column(name = "user_id")
    private UUID userId;
    
}
