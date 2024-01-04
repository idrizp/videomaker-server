package dev.idriz.videomaker.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Video {

    @Id
    private UUID id;

    @Column
    private String title;

    @Column
    private String pathToVideo;

    @Column
    private String captions;

    @Column
    private String pathToThumbnail;

    @Column
    private String generationPrompt;

    @ManyToOne
    private AppUser user;

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

    public String getPathToVideo() {
        return pathToVideo;
    }

    public void setPathToVideo(String pathToVideo) {
        this.pathToVideo = pathToVideo;
    }

    public String getCaptions() {
        return captions;
    }

    public void setCaptions(String captions) {
        this.captions = captions;
    }

    public String getPathToThumbnail() {
        return pathToThumbnail;
    }

    public void setPathToThumbnail(String pathToThumbnail) {
        this.pathToThumbnail = pathToThumbnail;
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
}
