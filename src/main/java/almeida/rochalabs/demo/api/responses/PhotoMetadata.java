package almeida.rochalabs.demo.api.responses;

import java.util.Set;

import lombok.Data;

/**
 * 
 * @author rochapaulo
 *
 */
@Data
public class PhotoMetadata {

    private String user;
    private String name;
    private String description;
    private Set<String> tags;
    private String location;

}
