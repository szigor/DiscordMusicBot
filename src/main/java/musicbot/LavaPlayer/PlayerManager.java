package musicbot.LavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);

                final String joinTrack = "https://www.youtube.com/watch?v=-53WqO6bUyY";
                final String dcTrack = "https://www.youtube.com/watch?v=mnCUqMB88Ww";

                if (!trackUrl.equals(joinTrack) && !trackUrl.equals(dcTrack)) {
                    channel.sendMessage("Dodaje do kolejki: `")
                            .addContent(track.getInfo().title)
                            .addContent("` od `")
                            .addContent(track.getInfo().author)
                            .addContent("`")
                            .queue();
                }

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();

                if (trackUrl.contains("ytsearch:")) {
                    AudioTrack track = tracks.get(0);
                    musicManager.scheduler.queue(track);
                    channel.sendMessage("Dodaje do kolejki: `")
                            .addContent(track.getInfo().title)
                            .addContent("` od `")
                            .addContent(track.getInfo().author)
                            .addContent("`")
                            .queue();
                } else {
                    channel.sendMessage("Dodaje do kolejki `")
                            .addContent(String.valueOf(tracks.size()))
                            .addContent("` tracków z playlisty: `")
                            .addContent(playlist.getName())
                            .addContent("`")
                            .queue();

                    for (final AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                    }

                }

            }

            @Override
            public void noMatches() {
                System.out.println("No matches ~PlayerManager.java");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                System.out.println("Load failed ~PlayerManager.java");
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}
