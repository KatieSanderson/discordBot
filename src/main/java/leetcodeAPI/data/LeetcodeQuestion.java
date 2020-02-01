package leetcodeAPI.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public
class LeetcodeQuestion {

    private QuestionStats stat;
    private Difficulty difficulty;
}
