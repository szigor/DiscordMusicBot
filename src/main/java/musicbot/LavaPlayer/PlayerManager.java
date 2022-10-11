package musicbot.LavaPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    public void loadAndPlay(TextChannel channel, String trackUrl, Member member) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);

                final String joinTrack = "https://www.youtube.com/watch?v=-53WqO6bUyY";
                final String dcTrack = "https://www.youtube.com/watch?v=mnCUqMB88Ww";

                if (!trackUrl.equals(joinTrack) && !trackUrl.equals(dcTrack)) {

                    EmbedBuilder embed = new EmbedBuilder()
                            .setColor(0xC2DDC0)
                            .setAuthor(" Dodaje do kolejki", member.getAvatarUrl(), member.getEffectiveAvatarUrl())
                            .appendDescription("`" + track.getInfo().title)
                            .appendDescription("` od `")
                            .appendDescription(track.getInfo().author + "` - `")
                            .appendDescription(formatTime(track.getDuration()) + "`");

                    channel.sendMessageEmbeds(embed.build()).queue();
                }

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();

                if (trackUrl.contains("ytsearch:")) {
                    AudioTrack track = tracks.get(0);
                    musicManager.scheduler.queue(track);

                    EmbedBuilder embed = new EmbedBuilder()
                            .setColor(0xC2DDC0)
                            .setAuthor(" Dodaje do kolejki", member.getAvatarUrl(), member.getEffectiveAvatarUrl())
                            .appendDescription("`" + track.getInfo().title)
                            .appendDescription("` od `")
                            .appendDescription(track.getInfo().author + "` - `")
                            .appendDescription(formatTime(track.getDuration()) + "`");

                    channel.sendMessageEmbeds(embed.build()).queue();

                } else {

                    EmbedBuilder embed = new EmbedBuilder()
                            .setColor(0xC2DDC0)
                            .setAuthor(" Dodaje do kolejki", member.getAvatarUrl(), member.getEffectiveAvatarUrl())
                            .appendDescription("`" + tracks.size())
                            .appendDescription("` tracków z playlisty `")
                            .appendDescription(playlist.getName() +  "`");

                    channel.sendMessageEmbeds(embed.build()).queue();
                    
                    Collections.shuffle(tracks);

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
}
