package dev.youika.testvault.economy;

import dev.youika.mysql.MySQL;
import dev.youika.mysql.factory.AsyncQueryFactory;
import dev.youika.mysql.factory.SyncQueryFactory;
import dev.youika.testvault.user.UserRepository;
import dev.youika.vault.Economy;
import dev.youika.vault.User;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class EconomyProvider implements Economy {

    private final AsyncQueryFactory factory;
    private final SyncQueryFactory syncFactory;
    private final UserRepository repository;

    public EconomyProvider(MySQL mySQL, UserRepository repository) {
        this.factory = mySQL.async();
        this.syncFactory = mySQL.sync();
        this.repository = repository;
    }

    @Override
    public User getUser(Player player) {
        return repository.unsafeUser(player);
    }

    @Override
    public CompletableFuture<Boolean> withdrawAsync(Player player, double sum) {
        User user = getUser(player);

        return user.withdrawCompletable(sum).thenApplyAsync(hasComplete -> {
            if (hasComplete) {

                int result = syncFactory.prepareUpdate("update `Players` set `balance` = ? where `uuid` = ?", ps -> {
                    try {
                        ps.setDouble(1, user.getBalance());
                        ps.setString(2, player.getUniqueId().toString());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                return result != 0;
            } else {
                player.sendMessage("Произошла неизвестная ошибка");
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> depositAsync(Player player, double sum) {
        User user = getUser(player);

        return user.depositCompletable(sum).thenApplyAsync(hasComplete -> {
            if (hasComplete) {

                int result = syncFactory.prepareUpdate("update `Players` set `balance` = ? where `uuid` = ?", ps -> {
                    try {
                        ps.setDouble(1, user.getBalance());
                        ps.setString(2, player.getUniqueId().toString());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                return result != 0;
            } else {
                player.sendMessage("Произошла неизвестная ошибка");
                return false;
            }
        });
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
            }).thenAcceptAsync(result -> {
                if (result == 0) {
                    user.deposit(sum);
                    player.sendMessage("Произошла неизвестная ошибка. Деньги вернулись");
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
            factory.prepareUpdate("update `Players` set `balance` = ? where `uuid` = ?", ps -> {
                try {
                    ps.setDouble(1, user.getBalance());
                    ps.setString(2, player.getUniqueId().toString());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).thenAcceptAsync(result -> {
                if (result == 0) {
                    user.withdraw(sum);
                    player.sendMessage("Произошла неизвестная ошибка. Последний депозит вычтен из баланса");
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

        double before = user.getBalance();
        double balance = user.setBalance(sum);

        factory.prepareUpdate("update `Players` set `balance` = ? where `uuid` = ?", ps -> {
            try {
                ps.setDouble(1, balance);
                ps.setString(2, player.getUniqueId().toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenAcceptAsync(result -> {
            if (result == 0) {
                user.setBalance(before);
                player.sendMessage("Произошла неизвестная ошибка. Откат последней установки баланса");
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
