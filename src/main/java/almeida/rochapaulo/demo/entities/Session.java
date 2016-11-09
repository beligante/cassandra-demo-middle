package almeida.rochapaulo.demo.entities;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
@AllArgsConstructor
public class Session {

    @PartitionKey
    @Column(name = "session_id")
    private UUID sessionId;
    
    @Column(name = "user_id")
    private UUID userId;
    
}
