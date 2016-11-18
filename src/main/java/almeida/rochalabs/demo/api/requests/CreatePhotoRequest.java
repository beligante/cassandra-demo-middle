package almeida.rochalabs.demo.api.requests;

import java.util.Set;

import lombok.Data;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
public class CreatePhotoRequest {

    private String userId;
    private String name;
    private String description;
    private Set<String> tags;
    private String base64Image;

}
