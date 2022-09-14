package musicbot.Commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import musicbot.Commands.CommandContext;
import musicbot.Commands.ICommand;
import musicbot.LavaPlayer.GuildMusicManager;
import musicbot.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class NowPlayingCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {

        final TextChannel channel = ctx.getEvent().getChannel().asTextChannel();
        final Member self = ctx.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            channel.sendMessage("Musisz być w kanale").queue();
            return;
        }

        final Member member = ctx.getEvent().getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            channel.sendMessage("Musisz być w kanale").queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            channel.sendMessage("Musisz być w kanale gdzie ja").queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        final AudioTrack track = audioPlayer.getPlayingTrack();

        if (track == null) {
            channel.sendMessage("Nic przecież nie leci").queue();
            return;
        }

        ctx.getEvent().getMessage().delete().queue();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(0xC2DDC0)
                .setAuthor(" Teraz leci", member.getAvatarUrl(), member.getEffectiveAvatarUrl())
                .appendDescription("`" + track.getInfo().title)
                .appendDescription("` od `")
                .appendDescription(track.getInfo().author + "`");

        channel.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "now";
    }
}
