package musicbot.Commands.music;

import musicbot.Commands.CommandContext;
import musicbot.Commands.ICommand;
import musicbot.LavaPlayer.GuildMusicManager;
import musicbot.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class LoopCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {

        final TextChannel channel = ctx.getEvent().getChannel().asTextChannel();
        final Member self = ctx.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        final Member member = ctx.getEvent().getMember();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final boolean newLooping = !musicManager.scheduler.looping;

        EmbedBuilder looped = new EmbedBuilder()
                .setColor(0xC2DDC0)
                .setAuthor(" Włącza pętle", member.getAvatarUrl(), member.getEffectiveAvatarUrl());

        EmbedBuilder unlooped = new EmbedBuilder()
                .setColor(0xC2DDC0)
                .setAuthor(" Wyłącza pętle", member.getAvatarUrl(), member.getEffectiveAvatarUrl());

        if (selfVoiceState.inAudioChannel()) {

            ctx.getEvent().getMessage().delete().queue();

            musicManager.scheduler.looping = newLooping;

            channel.sendMessageEmbeds(newLooping ? looped.build() : unlooped.build()).queue();

        } else {
            ctx.getEvent().getMessage().delete().queue();
        }

    }

    @Override
    public String getName() {
        return "loop";
    }
}
