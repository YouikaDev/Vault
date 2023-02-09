package dev.youika.testvault.economy;

import dev.youika.testvault.user.UserRepository;
import dev.youika.mysql.factory.AsyncQueryFactory;
import dev.youika.vault.Economy;
import dev.youika.vault.User;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class EconomyProvider implements Economy {

    private final AsyncQueryFactory factory;
    private final UserRepository repository;

    public EconomyProvider(AsyncQueryFactory factory, UserRepository repository) {
        this.factory = factory;
        this.repository = repository;
    }

    private User getUser(Player player) {
        return repository.unsafeUser(player);
    }

    @Override
    public double withdraw(Player player, double sum) {
        User user = getUser(player);

        if (user.withdraw(sum)) {
            factory.prepareUpdate("update `Players` set `balance` = ? where `uuid` = ?", ps -> {
                try {
                    ps.setDouble(1, user.getBalance());
                    ps.setString(2, player.getUniqueId().toString());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            player.sendMessage("Произошла неизвестная ошибка");
        }

        return user.getBalance();
    }

    @Override
    public double deposit(Player player, double sum) {
        User user = getUser(player);

        if (user.deposit(sum)) {
            System.out.println(user.getBalance());
            factory.prepareUpdate("update `Players` set `balance` = ? where `uuid` = ?", ps -> {
                try {
                    ps.setDouble(1, user.getBalance());
                    ps.setString(2, player.getUniqueId().toString());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            player.sendMessage("Произошла неизвестная ошибка");
        }

        return user.getBalance();
    }

    @Override
    public double setBalance(Player player, double sum) {
        User user = getUser(player);

        double balance = user.setBalance(sum);

        factory.prepareUpdate("update `Players` set `balance` = ? where `uuid` = ?", ps -> {
            try {
                ps.setDouble(1, balance);
                ps.setString(2, player.getUniqueId().toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return balance;
    }

    @Override
    public double getBalance(Player player) {
        User user = getUser(player);
        return user.getBalance();
    }
}
