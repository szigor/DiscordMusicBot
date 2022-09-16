package musicbot.Commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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

        final Member member = ctx.getEvent().getMember();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        final AudioTrack track = audioPlayer.getPlayingTrack();

        EmbedBuilder embed = new EmbedBuilder();

        embed
                .setColor(0xC2DDC0)
                .setAuthor(" Teraz leci", member.getAvatarUrl(), member.getEffectiveAvatarUrl());
//                .appendDescription("`" + track.getInfo().title)
//                .appendDescription("` od `")
//                .appendDescription(track.getInfo().author + "`")
//                .addField("Link", track.getInfo().uri, true);

        if (selfVoiceState.inAudioChannel()) {
            if (track != null) {
                ctx.getEvent().getMessage().delete().queue();
//                channel.sendMessageEmbeds(embed.build()).queue();
                channel.sendMessage(track.getInfo().uri).queue();
            } else {
                ctx.getEvent().getMessage().reply("Nic nie leci").queue();
            }
        } else {
            ctx.getEvent().getMessage().delete().queue();
        }
    }

    @Override
    public String getName() {
        return "now";
    }
}
