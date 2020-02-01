package leetcode;

import leetcode.api.model.DifficultyLevel;
import leetcode.api.model.LeetcodeQuestion;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LeetcodeRunnable implements Runnable {

    private final TextChannel textChannel;
    private final Map<DifficultyLevel, List<LeetcodeQuestion>> map;
    private final Random random;


    LeetcodeRunnable(TextChannel textChannel, Map<DifficultyLevel, List<LeetcodeQuestion>> map) {
        this.textChannel = textChannel;
        this.map = map;
        this.random = new Random();
    }

    @Override
    public void run() {
        textChannel.sendMessage("Leetcode Questions for today!").queue();
        for (DifficultyLevel level : DifficultyLevel.values()) {
            try {
                sendNextQuestion(level);
            } catch (RuntimeException e) {
                textChannel.sendMessage("Could not load " + level.getLevelString() + " question. The development team has been notified.").queue();
                // TODO: actually send notification
            }
        }
    }

    private void sendNextQuestion(DifficultyLevel level) {
        LeetcodeQuestion nextQuestion = getNextQuestion(level);
        URI uri = getQuestionURI(nextQuestion.getStat().getUrl());
        String questionDifficulty = String.format("`%-16s` ", level.getLevelString() + " Question:");
        textChannel.sendMessage(questionDifficulty + nextQuestion.getStat().getTitle() + " (" + uri + ")").queue();
    }

    private URI getQuestionURI(String questionPath) {
        try {
            return new URIBuilder(LeetcodeConstants.LEETCODE_BASE_URL).setPathSegments(LeetcodeConstants.PROBLEMS_PATH, questionPath).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Exception when constructing question's URL path [" + questionPath + "]", e);
        }
    }

    private LeetcodeQuestion getNextQuestion(DifficultyLevel level) {
        List<LeetcodeQuestion> questions = map.get(level);
        return questions.get(random.nextInt(questions.size()));
    }
}
