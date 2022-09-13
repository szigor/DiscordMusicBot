package musicbot.Commands.music;

import musicbot.Commands.CommandContext;
import musicbot.Commands.ICommand;
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

        if (memberVoiceState.inAudioChannel()) {
            if (ctx.getArgs().isEmpty()) {
                channel.sendMessage("Podaj frazę lub link do utworu po `!play`").queue();
                return;
            }
            if (selfVoiceState.inAudioChannel()) {
                //gra
                play(channel, ctx);
            } else {
                //dolacza i gra
                join(channel, audioManager, memberChannel);
                play(channel, ctx);
            }

        } else {
            channel.sendMessage("Musisz być w kanale żebym coś puścił").queue();
        }
    }

    private void join(TextChannel channel, AudioManager audioManager, AudioChannel memberChannel) {
        String link = "https://www.youtube.com/watch?v=-53WqO6bUyY";

        audioManager.openAudioConnection(memberChannel);
        channel.sendMessage("Wbijam szefie").queue();
        PlayerManager.getInstance().loadAndPlay(channel, link);
    }

    private void play(TextChannel channel, CommandContext ctx) {
        String link = String.join(" ", ctx.getArgs());

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance().loadAndPlay(channel, link);
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
