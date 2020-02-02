package leetcode;

import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot {

    public static void main(String[] args) throws LoginException {
        String token = System.getenv("TOKEN");
        if (token == null) {
            throw new RuntimeException("Environment variable \"TOKEN\" must be provided");
        }
        new JDABuilder(token)
                .addEventListeners(new ReadyListener())
                .build();
    }
}
