package leetcodeAPI.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeetcodeAPI {

    @JsonProperty("num_total")
    private int numQuestions;
    @JsonProperty("stat_status_pairs")
    private List<LeetcodeQuestion> questions;
}
