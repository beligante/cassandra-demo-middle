package almeida.rochalabs.demo.data.entities;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
@Table(name = "user_credentials")
@NoArgsConstructor
@AllArgsConstructor
public class UserCredential {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "password")
    private String password;

    @PartitionKey
    @Column(name = "email")
    private String email;

}
