package musicbot.Commands.other;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HelpCommand extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {

        String messageSent = event.getMessage().getContentRaw();

        if (messageSent.equalsIgnoreCase("dj!help")) {
            event.getChannel().sendMessage(
                    "__**Lista wszystkich moich komend:**__\n" +
                            "`!play` - Odpalam muze ***[!play*** <***link*** lub ***wyszukiwana fraza***>***]***\n" +
                            "`!stop` - stopuje utwór i zeruje kolejke\n" +
                            "`!skip` - skipuje nute\n" +
                            "`!loop` - puszczam nute w kółko/raz\n" +
                            "`!now` - nazwa bieżącej nuty\n" +
                            "`!queue` - pokazuje utwory, które są w kolejce\n" +
                            "`!dc` - rozłączam się z głosówki\n" +
                            "`!ping` - pokazuje swój ping"
            ).queue();
        }

    }
}
