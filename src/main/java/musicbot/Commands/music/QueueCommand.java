package musicbot.Commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import musicbot.Commands.CommandContext;
import musicbot.Commands.ICommand;
import musicbot.LavaPlayer.GuildMusicManager;
import musicbot.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {

        final TextChannel channel = ctx.getEvent().getChannel().asTextChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        final Member member = ctx.getEvent().getMember();

        if (queue.isEmpty()) {
            channel.sendMessage("Kolejka jest pusta").queue();
            channel.sendMessage("Jak twój łeb xdd").queue();
            return;
        }

        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);

        ctx.getEvent().getMessage().delete().queue();

        final MessageCreateAction messageAction = channel.sendMessage("**Bieżąca kolejka**\n");
        messageAction.addContent("\n");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(0xC2DDC0)
                .setAuthor(" Bieżąca kolejka", member.getAvatarUrl(), member.getEffectiveAvatarUrl());

        for (int i = 0; i < trackCount; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();

            embed
                    .appendDescription("`#")
                    .appendDescription(String.valueOf(i + 1))
                    .appendDescription("` `")
                    .appendDescription(info.title)
                    .appendDescription(" od ")
                    .appendDescription(info.author)
                    .appendDescription("` - `")
                    .appendDescription(formatTime(track.getDuration()))
                    .appendDescription("`\n");
        }

        if (trackList.size() > trackCount) {
            embed
                    .appendDescription("i jeszcze `")
                    .appendDescription(String.valueOf(trackList.size() - trackCount))
                    .appendDescription("` innych...");
        }

        channel.sendMessageEmbeds(embed.build()).queue();

    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        if (hours != 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    @Override
    public String getName() {
        return "queue";
    }
}
