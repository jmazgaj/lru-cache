package lru.cache;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LRUControllerTest {

    @LocalServerPort
    private int serverPort;

    @Test
    public void putGetTest() throws Exception {
        String key = "key";
        File file = new File(this.getClass().getClassLoader().getResource("uploadTestFile.jpg").getFile());
        FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("file", fileBody);
        HttpEntity entity = builder.build();
        HttpPost putRequest = new HttpPost("http://localhost:" + serverPort + "/put/" + key);
        putRequest.setEntity(entity);
        HttpClientBuilder.create().build().execute(putRequest);

        HttpGet getRequest = new HttpGet("http://localhost:" + serverPort + "/get/" + key);
        HttpResponse getResponse = HttpClientBuilder.create().build().execute(getRequest);

        Assertions.assertThat(IOUtils.toByteArray(getResponse.getEntity().getContent()))
                  .isEqualTo(IOUtils.toByteArray(new FileInputStream(file)));
    }

    @Test
    public void changeCapacityTest() throws Exception {
        HttpPut changeCapRequest = new HttpPut("http://localhost:" + serverPort + "/changeCapacity");
        changeCapRequest.setEntity(new StringEntity("10"));
        HttpResponse putResponse = HttpClientBuilder.create().build().execute(changeCapRequest);

        Assertions.assertThat(putResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void changeCapacityNFETest() throws Exception {
        HttpPut changeCapRequest = new HttpPut("http://localhost:" + serverPort + "/changeCapacity");
        changeCapRequest.setEntity(new StringEntity("xdxd"));
        HttpResponse putResponse = HttpClientBuilder.create().build().execute(changeCapRequest);

        Assertions.assertThat(putResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
    @Test
    public void changeCapacitySmallerThanZeroTest() throws Exception {
        HttpPut changeCapRequest = new HttpPut("http://localhost:" + serverPort + "/changeCapacity");
        changeCapRequest.setEntity(new StringEntity("-1"));
        HttpResponse putResponse = HttpClientBuilder.create().build().execute(changeCapRequest);

        Assertions.assertThat(putResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}