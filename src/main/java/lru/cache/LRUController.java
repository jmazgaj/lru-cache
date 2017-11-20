package lru.cache;

import lru.cache.services.LRUService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@SuppressWarnings("unchecked")
public class LRUController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LRUService lruService;

    @Autowired
    public LRUController(LRUService lruService) {
        this.lruService = lruService;
    }

    @GetMapping("/get/{key}")
    final public ResponseEntity get(@PathVariable String key) {
        return new ResponseEntity(lruService.get(key), lruService.getHttpHeaders(key), HttpStatus.OK);
    }

    @RequestMapping(value = "/put/{key}", method = RequestMethod.POST, produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
    final public ResponseEntity put(@PathVariable(name="key") String key,
                                    @RequestParam(value = "file", required = false) MultipartFile file,
                                    HttpServletRequest request) throws MultipartException {
        HttpHeaders headers = lruService.getHttpHeaders(key);
        Object value;
        try {
            if (file == null) {
                value = lruService.put(key, IOUtils.toString(request.getReader()));
            } else {
                value = lruService.put(key, IOUtils.toByteArray(file.getInputStream()), file.getOriginalFilename());
            }
            return new ResponseEntity(value, headers, HttpStatus.OK);
        } catch (IOException | OutOfMemoryError e) {
            logger.error("Couldn't put value for key: " + key, e);
            return new ResponseEntity("Couldn't put value for key: " + key, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/changeCapacity")
    final public ResponseEntity changeCapacity(@RequestBody String capacityString) {
        try {
            int capacity = Integer.parseInt(capacityString);
            if (capacity < 0) {
                return new ResponseEntity("Capacity cannot be smaller than 0.", HttpStatus.BAD_REQUEST);
            }
            lruService.setCapacity(capacity);
            return new ResponseEntity("Capacity changed.", HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity("Couldn't parse capacity size.", HttpStatus.BAD_REQUEST);
        }
    }
}

