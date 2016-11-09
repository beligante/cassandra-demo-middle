package almeida.rochapaulo.demo.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import almeida.rochapaulo.demo.bucket.BucketRepository;

/**
 * 
 * @author rochapaulo
 *
 */
@RestController(value = "/secure/bucket")
public class BucketRS {

    private final BucketRepository bucket;

    @Autowired
    public BucketRS(BucketRepository bucket) {
        this.bucket = bucket;
    }

    @RequestMapping(path = "/images/{uuid}", method = RequestMethod.GET, produces = "image/jpg")
    public ResponseEntity<?> getThumbnail(
            @PathVariable String uuid,
            @RequestParam(name = "thumbnail", required = false) boolean thumbnail
    ) {

        final byte[] image;
        if (thumbnail) {
            image = bucket.getThumbnail(uuid);
        } else {
            image = bucket.getImage(uuid);
        }

        return ResponseEntity.ok().body(image);
    }

}
