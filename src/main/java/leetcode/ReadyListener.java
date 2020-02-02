package leetcode;

import leetcode.api.LeetcodeApiConnector;
import leetcode.api.model.DifficultyLevel;
import leetcode.api.model.LeetcodeQuestion;
import leetcode.api.model.LeetcodeResponse;
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

    private static final int SECONDS_IN_A_DAY = 24 * 60 * 60;
    private static final int START_TIME_HOUR = 20;
    private static final String CHANNEL_NAME = System.getenv("CHANNEL_NAME");
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            TextChannel textChannel = event.getJDA().getTextChannelsByName(CHANNEL_NAME, true).get(0);
            try {
                LeetcodeResponse leetcodeResponse = LeetcodeApiConnector.getLeetcodeResponse();
                Map<DifficultyLevel, List<LeetcodeQuestion>> map = sortQuestionsByDifficulty(leetcodeResponse);
                if (System.getenv("RUN_SCHEDULED").equals("TRUE")) {
                    scheduler.scheduleAtFixedRate(new LeetcodeRunnable(textChannel, map), getSecondsToStart(), SECONDS_IN_A_DAY, TimeUnit.SECONDS);
                } else {
                    new LeetcodeRunnable(textChannel, map).run();
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                textChannel.sendMessage("Error during application start-up. The development team has been notified.").queue();
                // TODO: actually send notification
            }
        }
    }

    private long getSecondsToStart() {
        LocalTime now = LocalTime.now(ZoneOffset.UTC);
        LocalTime startTime = LocalTime.of(START_TIME_HOUR, 0);
        long secondsToStart = SECONDS.between(now, startTime);
        if (now.isAfter(startTime)) {
            secondsToStart += SECONDS_IN_A_DAY;
        }
        return secondsToStart;
    }

    private Map<DifficultyLevel, List<LeetcodeQuestion>> sortQuestionsByDifficulty(LeetcodeResponse leetcodeResponse) {
        Map<DifficultyLevel, List<LeetcodeQuestion>> map = new HashMap<>();
        map.put(DifficultyLevel.EASY, new ArrayList<>());
        map.put(DifficultyLevel.MEDIUM, new ArrayList<>());
        map.put(DifficultyLevel.HARD, new ArrayList<>());

        List<LeetcodeQuestion> questions = leetcodeResponse.getQuestions();
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
