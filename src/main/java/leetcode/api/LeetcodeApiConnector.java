package leetcode.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import leetcode.LeetcodeConstants;
import leetcode.api.model.LeetcodeResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LeetcodeApiConnector {

    public static LeetcodeResponse getLeetcodeResponse() {
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()) {
            URI uri = new URIBuilder(LeetcodeConstants.LEETCODE_BASE_URL).setPath(LeetcodeConstants.LEETCODE_ALGORITHMS_PATH).build();
            HttpGet request = new HttpGet(uri);

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("Connection error; status code [" + response.getStatusLine().getStatusCode() + "]");
                }
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new RuntimeException("Response entity is null. Response: [" + response.toString() + "].");
                }
                String result = EntityUtils.toString(entity);

                // response entity expected to be JSON formatted to /api objects
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return mapper.readValue(result, LeetcodeResponse.class);
            }
        } catch (IOException | URISyntaxException e) {
           throw new RuntimeException("Exception thrown when obtaining Leetcode information", e);
        }
    }

}
