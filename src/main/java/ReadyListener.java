import leetcodeAPI.LeetcodeAPIConnector;
import leetcodeAPI.data.DifficultyLevel;
import leetcodeAPI.data.LeetcodeAPI;
import leetcodeAPI.data.LeetcodeQuestion;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

public class ReadyListener implements EventListener {

    private final int secondsInADay = 24 * 60 * 60;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            TextChannel textChannel = event.getJDA().getTextChannelsByName("general", true).get(0);
            LeetcodeAPI leetcodeAPI = LeetcodeAPIConnector.getLeetcodeAPI();
            Map<DifficultyLevel, List<LeetcodeQuestion>> map = sortQuestionsByDifficulty(leetcodeAPI);

            LocalTime now = LocalTime.now(ZoneOffset.UTC);
            LocalTime eightPM = LocalTime.of(20, 0);
            long secondsTo8PM = (SECONDS.between(now, eightPM) + secondsInADay) % secondsInADay;

            scheduler.scheduleAtFixedRate(new LeetcodeRunnable(textChannel, map), secondsTo8PM, secondsInADay, TimeUnit.SECONDS);
        }
    }

    private Map<DifficultyLevel, List<LeetcodeQuestion>> sortQuestionsByDifficulty(LeetcodeAPI leetcodeAPI) {
        Map<DifficultyLevel, List<LeetcodeQuestion>> map = new HashMap<>();
        map.put(DifficultyLevel.EASY, new ArrayList<>());
        map.put(DifficultyLevel.MEDIUM, new ArrayList<>());
        map.put(DifficultyLevel.HARD, new ArrayList<>());

        List<LeetcodeQuestion> questions = leetcodeAPI.getQuestions();
        for (LeetcodeQuestion question : questions) {
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
}
