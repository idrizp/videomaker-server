package dev.idriz.videomaker.service;

import dev.idriz.videomaker.entity.AppUser;
import dev.idriz.videomaker.repository.AppUserRepository;
import dev.idriz.videomaker.token.Tokenizer;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.UUID;

@Service
public class BalanceService {

    private static final BigInteger COST_PER_HUNDRED_TOKENS = BigInteger.valueOf(5); // in cents
    private final AppUserRepository userRepository;

    public BalanceService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean addToBalance(UUID userId, BigInteger amount) {
        return userRepository.addToBalance(userId, amount) > 0;
    }

    public boolean withdrawFromBalance(UUID userId, BigInteger amount) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        if (user.getBalance().compareTo(amount) < 0) {
            return false;
        }
        return userRepository.addToBalance(userId, amount.negate()) > 0;
    }


    public boolean canAfford(AppUser user, String... promptLines) {
        var cost = getCost(promptLines);
        return user.getBalance().compareTo(cost) >= 0;

    }

    public BigInteger getCost(String[] promptLines) {
        var cost = BigInteger.ZERO;
        for (var line : promptLines) {
            var lines = Tokenizer.encode(line);
            cost = cost.add(COST_PER_HUNDRED_TOKENS.multiply(BigInteger.valueOf(lines.size())).divide(BigInteger.valueOf(100)));
        }
        return cost;
    }
}
