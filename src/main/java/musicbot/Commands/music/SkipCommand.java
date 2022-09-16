package musicbot.Commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import musicbot.Commands.CommandContext;
import musicbot.Commands.ICommand;
import musicbot.LavaPlayer.GuildMusicManager;
import musicbot.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class SkipCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {

        final TextChannel channel = ctx.getEvent().getChannel().asTextChannel();
        final Member self = ctx.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        final Member member = ctx.getEvent().getMember();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        EmbedBuilder skipEmbed = new EmbedBuilder()
                .setColor(0xC2DDC0)
                .setAuthor(" Skipuje..", member.getAvatarUrl(), member.getEffectiveAvatarUrl());

        if (selfVoiceState.inAudioChannel()) {

            if (audioPlayer.getPlayingTrack() != null) {
                ctx.getEvent().getMessage().delete().queue();
                musicManager.scheduler.nextTrack();
                channel.sendMessageEmbeds(skipEmbed.build()).queue();
            } else {
                ctx.getEvent().getMessage().reply("Kolejka jest pusta").queue();
            }

        } else {
            ctx.getEvent().getMessage().delete().queue();
        }

    }

    @Override
    public String getName() {
        return "skip";
    }
}
