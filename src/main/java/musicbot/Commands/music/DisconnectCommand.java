package musicbot.Commands.music;

import musicbot.Commands.CommandContext;
import musicbot.Commands.ICommand;
import musicbot.LavaPlayer.GuildMusicManager;
import musicbot.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class DisconnectCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {

        final TextChannel channel = ctx.getEvent().getChannel().asTextChannel();
        final Member self = ctx.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        final Member member = ctx.getEvent().getMember();

        final Guild guild = ctx.getGuild();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);

        final AudioManager audioManager = guild.getAudioManager();

        if (selfVoiceState.inAudioChannel()) {

            ctx.getEvent().getMessage().delete().queue();

            musicManager.scheduler.looping = false;
            musicManager.scheduler.queue.clear();
            musicManager.audioPlayer.stopTrack();

            PlayerManager.getInstance().loadAndPlay(channel, "https://www.youtube.com/watch?v=mnCUqMB88Ww", member);

            try {
                Thread.sleep(4500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            audioManager.closeAudioConnection(); //dc

        } else {
            ctx.getEvent().getMessage().delete().queue();
        }

    }

    @Override
    public String getName() {
        return "dc";
    }
}
