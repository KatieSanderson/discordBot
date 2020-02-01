package leetcodeAPI.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionStats {

    @JsonProperty("question_id")
    private int id;
    @JsonProperty("question__title")
    private String title;
    @JsonProperty("question__title_slug")
    private String url;
}
