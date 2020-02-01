package leetcodeAPI.data;

import lombok.Getter;

@Getter
public enum DifficultyLevel {

    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    private final String levelString;

    DifficultyLevel(String levelString) {
        this.levelString = levelString;
    }
}
