package leetcode.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LeetcodeQuestion {

    private QuestionStats stat;
    private Difficulty difficulty;
    @JsonProperty("paid_only")
    private boolean isPremium;
}
