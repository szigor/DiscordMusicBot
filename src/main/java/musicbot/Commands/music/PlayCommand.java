package musicbot.Commands.music;

import musicbot.Commands.CommandContext;
import musicbot.Commands.ICommand;
import musicbot.LavaPlayer.GuildMusicManager;
import musicbot.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URL;

public class PlayCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {

        final Member member = ctx.getEvent().getMember(); //w ktorym kanale jest osoba ktora pisze
        final GuildVoiceState memberVoiceState = member.getVoiceState(); //w ktorym kanale jest osoba ktora pisze

        final TextChannel channel = ctx.getEvent().getChannel().asTextChannel(); //pobieranie kanalu gdzie sie pisze

        final Member self = ctx.getGuild().getSelfMember(); //w ktorym kanale jest bot
        final GuildVoiceState selfVoiceState = self.getVoiceState(); //w ktorym kanale jest bot

        final AudioManager audioManager = ctx.getGuild().getAudioManager(); //join
        final AudioChannel memberChannel = memberVoiceState.getChannel(); //join - kanal osoby ktora pisze

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

        if (memberVoiceState.inAudioChannel()) {
            if (ctx.getArgs().isEmpty()) {
                channel.sendMessage("Podaj frazę lub link do utworu po `!play`").queue();
                return;
            }
            if (selfVoiceState.inAudioChannel()) {
                //gra
                ctx.getEvent().getMessage().delete().queue();
                play(channel, ctx, member);
            } else {
                //dolacza i gra
                ctx.getEvent().getMessage().delete().queue();
                join(channel, audioManager, memberChannel, member, musicManager);
                play(channel, ctx, member);
            }

        } else {
            channel.sendMessage("Musisz być w kanale żebym coś puścił").queue();
        }
    }

    private void join(TextChannel channel, AudioManager audioManager, AudioChannel memberChannel, Member member, GuildMusicManager musicManager) {
        String link = "https://www.youtube.com/watch?v=-53WqO6bUyY";

        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        audioManager.openAudioConnection(memberChannel);
        PlayerManager.getInstance().loadAndPlay(channel, link, member);
    }

    private void play(TextChannel channel, CommandContext ctx, Member member) {
        String link = String.join(" ", ctx.getArgs());

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance().loadAndPlay(channel, link, member);
    }

    private boolean isUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "play";
    }
}
