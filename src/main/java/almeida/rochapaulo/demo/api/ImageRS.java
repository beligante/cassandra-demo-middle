package almeida.rochapaulo.demo.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import almeida.rochapaulo.demo.dao.ImageDAO;

/**
 * 
 * @author rochapaulo
 *
 */
@RestController
public class ImageRS {

    private final ImageDAO dao;

    @Autowired
    public ImageRS(ImageDAO dao) {
        this.dao = dao;
    }

    @RequestMapping(path = "/image/{uuid}", method = RequestMethod.GET, produces = "image/*")
    public ResponseEntity<?> getThumbnail(
            @PathVariable String uuid,
            @RequestParam(name = "thumbnail", required = false) boolean thumbnail
    ) {

        final byte[] image;
        if (thumbnail) {
            image = dao.getThumbnail(uuid);
        } else {
            image = dao.getImage(uuid);
        }

        return ResponseEntity.ok().body(image);
    }

}
