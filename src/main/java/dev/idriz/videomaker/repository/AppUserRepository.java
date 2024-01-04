package dev.idriz.videomaker.repository;

import dev.idriz.videomaker.entity.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

/**
 * The repository for the {@link AppUser} entity
 */
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    /**
     * Find a user by the username
     *
     * @param username The username
     * @return The user
     */
    Optional<AppUser> findByUsernameIgnoreCase(String username);

    /**
     * Find a user by the email
     *
     * @param email The email
     * @return The user
     */
    Optional<AppUser> findByEmailIgnoreCase(String email);

    /**
     * Find a user by the username or email
     *
     * @param username The username
     * @param email    The email
     * @return The user
     */
    Optional<AppUser> findByUsernameEqualsOrEmailEqualsIgnoreCase(String username, String email);

    /**
     * Updates the balance of a user
     * @param userId The id of the user
     * @param balance The new balance
     * @return The number of rows updated
     */
    @Transactional
    @Modifying
    @Query("UPDATE AppUser u SET u.balance = u.balance + :balance WHERE u.id = :userId")
    int addToBalance(UUID userId, BigInteger balance);

    /**
     * Updates the balance of a user
     * @param username The username of the user
     * @param balance The new balance
     * @return The number of rows updated
     */
    @Transactional
    @Modifying
    @Query("UPDATE AppUser u SET u.balance = u.balance + :balance WHERE u.username = :username")
    int addToBalance(String username, BigInteger balance);

}
