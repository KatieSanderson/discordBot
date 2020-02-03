package leetcode;

import leetcode.api.model.DifficultyLevel;
import leetcode.api.model.LeetcodeQuestion;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

public class ReadyListener implements EventListener {

    private static final int SECONDS_IN_A_DAY = 24 * 60 * 60;
    private static final int START_TIME_HOUR_DEFAULT = 20;
    private static final String CHANNEL_NAME = System.getenv("CHANNEL_NAME");
    private static final String GENERAL_CHANNEL = "general";

    private final Map<Guild, ScheduledExecutorService> guilds = new HashMap<>();
    private final Map<DifficultyLevel, List<LeetcodeQuestion>> leetcodeQuestions;
    private final int startTimeHour;

    ReadyListener(Map<DifficultyLevel, List<LeetcodeQuestion>> leetcodeQuestions) {
        this.leetcodeQuestions = leetcodeQuestions;
        startTimeHour = getStartHour();
    }

    private int getStartHour() {
        try {
            return Integer.parseInt(System.getenv("START_TIME_GMT"));
        } catch (NumberFormatException e) {
            return START_TIME_HOUR_DEFAULT;
        }
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof GuildJoinEvent) {
            GuildJoinEvent joinEvent = (GuildJoinEvent) event;
            Guild guild = joinEvent.getGuild();
            System.out.println("Joined " + guild.getName());
            sendWelcomeMessage(guild);
            runForGuild(guild);
        }
        if (event instanceof GuildLeaveEvent) {
            GuildLeaveEvent leaveEvent = (GuildLeaveEvent) event;
            Guild guild = leaveEvent.getGuild();
            System.out.println("Left " + guild.getName());
            guilds.remove(guild).shutdown();
        }
        if (event instanceof ReadyEvent) {
            for (Guild guild : event.getJDA().getGuilds()) {
                System.out.println("Starting up on " + guild.getName());
                runForGuild(guild);
            }
        }
    }

    private void sendWelcomeMessage(Guild guild) {
        TextChannel generalTextChannel = guild.getTextChannelsByName(GENERAL_CHANNEL, true).get(0);
        generalTextChannel.sendMessage("Hi! I'm a bot. I was created by Katie Sanderson to post daily Leetcode questions!").queue();
        generalTextChannel.sendMessage("Source code: https://github.com/KatieSanderson/discordBot").queue();
    }

    private void runForGuild(Guild guild) {
        try {
            if (guild.getTextChannelsByName(CHANNEL_NAME, true).isEmpty()) {
                TextChannel textChannel = guild.getTextChannelsByName(GENERAL_CHANNEL, true).get(0);
                textChannel.sendMessage("Server does not have a \"" + CHANNEL_NAME + "\" channel. Please add so I can post my questions!").queue();
            } else {
                TextChannel textChannel = guild.getTextChannelsByName(CHANNEL_NAME, true).get(0);
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                if (System.getenv("RUN_SCHEDULED").toLowerCase().equals("true")) {
                    scheduler.scheduleAtFixedRate(new LeetcodeRunnable(textChannel, leetcodeQuestions), getSecondsToStart(), SECONDS_IN_A_DAY, TimeUnit.SECONDS);
                } else {
                    scheduler.schedule(new LeetcodeRunnable(textChannel, leetcodeQuestions), 0, TimeUnit.SECONDS);
                }
                guilds.put(guild, scheduler);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println("Error during application start-up for guild [" + guild.getName() + "]. The development team has been notified.");
            // TODO: actually send notification
        }
    }

    private long getSecondsToStart() {
        LocalTime now = LocalTime.now(ZoneOffset.UTC);
        LocalTime startTime = LocalTime.of(startTimeHour, 0);
        long secondsToStart = SECONDS.between(now, startTime);
        if (now.isAfter(startTime)) {
            secondsToStart += SECONDS_IN_A_DAY;
        }
        return secondsToStart;
    }
}
