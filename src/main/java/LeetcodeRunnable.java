import leetcodeAPI.data.DifficultyLevel;
import leetcodeAPI.data.LeetcodeQuestion;
import net.dv8tion.jda.api.entities.TextChannel;

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
        random = new Random();
    }

    @Override
    public void run() {
        textChannel.sendMessage("Leetcode Questions for today!").queue();
        sendNextQuestion(DifficultyLevel.EASY);
        sendNextQuestion(DifficultyLevel.MEDIUM);
        sendNextQuestion(DifficultyLevel.HARD);
    }

    private void sendNextQuestion(DifficultyLevel level) {
        LeetcodeQuestion nextQuestion = getNextQuestion(level);
        String url = "https://leetcode.com/problems/" + nextQuestion.getStat().getUrl();
        textChannel.sendMessage(level.getLevelString() + " Question: " + nextQuestion.getStat().getTitle() + " (" + url + ")").queue();
    }

    private LeetcodeQuestion getNextQuestion(DifficultyLevel level) {
        List<LeetcodeQuestion> questions = map.get(level);
        return questions.get(random.nextInt(questions.size()));
    }
}
