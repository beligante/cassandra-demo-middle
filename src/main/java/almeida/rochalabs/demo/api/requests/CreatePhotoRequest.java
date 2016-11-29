package almeida.rochalabs.demo.api.requests;

import java.util.Set;
import java.util.UUID;

import lombok.Data;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
public class CreatePhotoRequest {

    private UUID userId;
    private String name;
    private String description;
    private Set<String> tags;
    private String base64Image;

}
