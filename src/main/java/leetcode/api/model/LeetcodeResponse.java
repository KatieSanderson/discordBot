package leetcode.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LeetcodeResponse {

    @JsonProperty("num_total")
    private int numQuestions;
    @JsonProperty("stat_status_pairs")
    private List<LeetcodeQuestion> questions;
}
