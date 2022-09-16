package musicbot.Commands.music;

import musicbot.Commands.CommandContext;
import musicbot.Commands.ICommand;
import musicbot.LavaPlayer.GuildMusicManager;
import musicbot.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class StopCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {

        final TextChannel channel = ctx.getEvent().getChannel().asTextChannel();
        final Member self = ctx.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        final Member member = ctx.getEvent().getMember();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

        EmbedBuilder stopEmbed = new EmbedBuilder()
                .setColor(0xC2DDC0)
                .setAuthor(" Wyczyścił liste odtwarzania", member.getAvatarUrl(), member.getEffectiveAvatarUrl());

        if (selfVoiceState.inAudioChannel()) {
            ctx.getEvent().getMessage().delete().queue();
            musicManager.scheduler.player.stopTrack();
            musicManager.scheduler.queue.clear();
            channel.sendMessageEmbeds(stopEmbed.build()).queue();
        } else {
            ctx.getEvent().getMessage().delete().queue();
        }

    }

    @Override
    public String getName() {
        return "stop";
    }
}
