package almeida.rochapaulo.demo.api.requests;

import lombok.Data;

@Data
public class RatePhotoRequest {

    private String photoUUID;
    private String rating;
    
}
