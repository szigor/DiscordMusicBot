package musicbot;

import musicbot.Commands.other.HelpCommand;
import musicbot.Commands.other.PingCommand;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Bot {

    private Bot() throws LoginException {

        JDABuilder.createDefault(
                        System.getenv("TOKEN"),
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.MESSAGE_CONTENT
//                        GatewayIntent.GUILD_PRESENCES,
//                        GatewayIntent.GUILD_MEMBERS
                )
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(
                        new Listener(),
                        new HelpCommand(),
                        new PingCommand())
                .setActivity(Activity.listening("dj!help - komendy"))
                .build();
    }

    public static void main(String[] args) throws LoginException {
        new Bot();
    }
}
