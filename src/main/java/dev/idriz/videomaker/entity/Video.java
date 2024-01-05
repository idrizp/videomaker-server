package dev.idriz.videomaker.entity;

import dev.idriz.videomaker.video.VideoUtils;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Video {

    @Id
    private UUID id;

    @Column
    private String title;

    @Column
    private String voice;

    @Column
    private String url;

    @Column
    private String generationPrompt;

    @ManyToOne
    private AppUser user;

    @OneToMany(mappedBy = "video", orphanRemoval = true)
    @OrderBy("clip.ordinal")
    private List<Clip> clips = new ArrayList<>();

    public List<Clip> getClips() {
        return clips;
    }

    public void setClips(List<Clip> clips) {
        this.clips = clips;
    }

    public void addClip(VideoUtils.Clip clip, int indexInList) {
        Clip c = new Clip();
        c.setOrdinal(indexInList);
        c.setAudioUrl(clip.getAudioFilePath());
        c.setUrl(clip.getFilePath());
        c.setTextSection(clip.getTextSections().getFirst());
        c.setVideo(this);
        clips.add(c);
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGenerationPrompt() {
        return generationPrompt;
    }

    public void setGenerationPrompt(String generationPrompt) {
        this.generationPrompt = generationPrompt;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    @Entity
    public static class Clip {

        @Id
        @GeneratedValue
        private UUID id;

        private int ordinal;

        private VideoUtils.TextSection textSection;
        private String url;
        private String audioUrl;

        @ManyToOne
        @JoinColumn(name = "video_id")
        private Video video;

        public Video getVideo() {
            return video;
        }

        public void setVideo(Video video) {
            this.video = video;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public int getOrdinal() {
            return ordinal;
        }

        public void setOrdinal(int ordinal) {
            this.ordinal = ordinal;
        }

        public VideoUtils.TextSection getTextSection() {
            return textSection;
        }

        public void setTextSection(VideoUtils.TextSection textSection) {
            this.textSection = textSection;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }
    }
}
