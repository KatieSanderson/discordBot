package leetcode;

import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot {

    public static void main(String[] args) throws LoginException {
        new JDABuilder(args[0])
                .addEventListeners(new ReadyListener())
                .build();
    }
}
