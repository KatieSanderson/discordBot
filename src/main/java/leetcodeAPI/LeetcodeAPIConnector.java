package leetcodeAPI;


import com.fasterxml.jackson.databind.ObjectMapper;
import leetcodeAPI.data.LeetcodeAPI;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
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

public class LeetcodeAPIConnector {

    private static String LEETCODE_DOMAIN = "https://leetcode.com";
    private static String LEETCODE_ALGORITHMS_PATH = "/api/problems/algorithms/";

    public static LeetcodeAPI getLeetcodeAPI() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()) {
            URI uri = new URIBuilder(LEETCODE_DOMAIN + LEETCODE_ALGORITHMS_PATH).build();
            HttpGet request = new HttpGet(uri);

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Connection error; status code [" + response.getStatusLine().getStatusCode() + "]");
                }
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new RuntimeException("Response entity is null. Response: [" + response.toString() + "].");
                }
                String result = EntityUtils.toString(entity);

                // response entity expected to be JSON formatted to /leetcodeAPI objects
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(result, LeetcodeAPI.class);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
