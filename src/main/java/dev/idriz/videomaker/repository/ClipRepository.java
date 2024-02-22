package dev.idriz.videomaker.repository;

import dev.idriz.videomaker.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClipRepository extends JpaRepository<Video.Clip, UUID> {
}
