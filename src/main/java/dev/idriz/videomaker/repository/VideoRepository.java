package dev.idriz.videomaker.repository;

import dev.idriz.videomaker.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * The repository for the {@link Video} entity
 */
public interface VideoRepository extends JpaRepository<Video, UUID> {

    /**
     * Find a video by the user id
     * @param userId The user id
     * @return The video
     */
    Video findVideoByUserId(UUID userId);

}
