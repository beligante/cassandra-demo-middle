package almeida.rochalabs.demo.api.responses;

import java.util.Set;
import java.util.UUID;

import lombok.Data;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
public class PhotoMetadata {

    private UUID user;
    private String name;
    private String description;
    private Set<String> tags;
    private String location;

}
