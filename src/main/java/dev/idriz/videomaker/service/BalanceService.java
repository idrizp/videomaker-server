package dev.idriz.videomaker.service;

import dev.idriz.videomaker.entity.AppUser;
import dev.idriz.videomaker.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

@Service
public class BalanceService {

    private static final BigInteger COST_PER_LINE = BigInteger.valueOf(5); // in cents
    private final AppUserRepository userRepository;

    public BalanceService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean addToBalance(String username, BigInteger amount) {
        return userRepository.addToBalance(username, amount) > 0;
    }

    public boolean subtractFromBalance(String username, BigInteger amount) {
        return addToBalance(username, amount.negate());
    }

    public BigInteger getBalance(String username) {
        Optional<AppUser> user = userRepository.findByUsernameIgnoreCase(username);
        return user.map(AppUser::getBalance).orElse(BigInteger.ZERO);
    }

    public boolean canAfford(String username, BigInteger amount) {
        return getBalance(username).compareTo(amount) >= 0;
    }

    public boolean canAfford(AppUser user, String... promptLines) {
        BigInteger cost = BigInteger.valueOf(promptLines.length).multiply(COST_PER_LINE);
        return user.getBalance().compareTo(cost) >= 0;
    }
}
