package leetcode;

import leetcode.api.LeetcodeApiConnector;
import leetcode.api.model.DifficultyLevel;
import leetcode.api.model.LeetcodeQuestion;
import leetcode.api.model.LeetcodeResponse;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot {

    private final String token;

    public Bot(String token) {
        this.token = token;
    }

    private void run() throws LoginException {
        Map<DifficultyLevel, List<LeetcodeQuestion>> leetcodeQuestions = getLeetcodeQuestions();
        new JDABuilder(token)
                .addEventListeners(new ReadyListener(leetcodeQuestions))
                .build();
    }

    private Map<DifficultyLevel, List<LeetcodeQuestion>> getLeetcodeQuestions() {
        LeetcodeResponse leetcodeResponse = LeetcodeApiConnector.getLeetcodeResponse();
        return sortQuestionsByDifficulty(leetcodeResponse);
    }

    private Map<DifficultyLevel, List<LeetcodeQuestion>> sortQuestionsByDifficulty(LeetcodeResponse leetcodeResponse) {
        Map<DifficultyLevel, List<LeetcodeQuestion>> map = new HashMap<>();
        map.put(DifficultyLevel.EASY, new ArrayList<>());
        map.put(DifficultyLevel.MEDIUM, new ArrayList<>());
        map.put(DifficultyLevel.HARD, new ArrayList<>());

        List<LeetcodeQuestion> questions = leetcodeResponse.getQuestions();
        for (LeetcodeQuestion question : questions) {
            // skip premium questions, ain't nobody got money for that
            if (question.isPremium()) {
                continue;
            }
            DifficultyLevel level;
            switch (question.getDifficulty().getLevel()) {
                case 1 :
                    level = DifficultyLevel.EASY;
                    break;
                case 2 :
                    level = DifficultyLevel.MEDIUM;
                    break;
                case 3 :
                    level = DifficultyLevel.HARD;
                    break;
                default:
                    throw new RuntimeException("Unknown difficulty level [" + question.getDifficulty().getLevel() + "[ in question [" + question.getStat().getTitle() + "]");

            }
            map.get(level).add(question);
        }
        return map;
    }

    public static void main(String[] args) throws LoginException {
        String token = System.getenv("TOKEN");
        if (token == null) {
            throw new RuntimeException("Environment variable \"TOKEN\" must be provided");
        }
        Bot bot = new Bot(token);
        bot.run();
    }
}
