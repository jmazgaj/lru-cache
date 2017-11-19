package lru.main;

import lru.main.services.LRUService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class LRUController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LRUService lruService;

    @Autowired
    public LRUController(LRUService lruService) {
        this.lruService = lruService;
    }

    @GetMapping("/get/{key}")
    final public @ResponseBody Object get(@PathVariable String key) {
        return lruService.get(key);
    }

    @RequestMapping(value = "/put/{key}", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    final public @ResponseBody Object put(@PathVariable(name="key") String key,
                                          HttpServletRequest request) {
        try {
            return lruService.put(key, IOUtils.toString(request.getReader()));
        } catch (IOException | OutOfMemoryError e) {
            logger.error("Couldn't put value for key: " + key, e);
            return "OutOfMemoryError|IOException - " + e.getMessage();
        }
    }

    @RequestMapping(value = "/getb/{key}", method = RequestMethod.GET, produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
    final public ResponseEntity<byte[]> getBinary(@PathVariable(name="key") String key) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>((byte[]) lruService.get(key), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/putb/{key}", method = RequestMethod.POST, produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
    final public ResponseEntity<byte[]> putBinary(@PathVariable(name="key") String key,
                                                  HttpServletRequest request) {
        HttpHeaders headers = getHttpHeaders();
        try {
            return new ResponseEntity<>((byte[]) lruService.put(key, IOUtils.toByteArray(request.getInputStream())), headers, HttpStatus.OK);
        } catch (IOException | OutOfMemoryError e) {
            logger.error("Couldn't put binary value for key: " + key, e);
            return new ResponseEntity<>(new byte[0], headers, HttpStatus.BAD_REQUEST);
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
        String filename = "output.jpg";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return  headers;
    }


    @PutMapping("/changeCapacity")
    final public @ResponseBody String changeCapacity(@RequestBody String capacityString) {
        try {
            int capacity = Integer.parseInt(capacityString);
            lruService.setCapacity(capacity);
            return "Capacity changed";
        } catch (NumberFormatException e) {
            return "Couldn't format capacity number";
        }
    }
}
