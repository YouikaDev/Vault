package dev.youika.testvault.user;

import dev.youika.mysql.factory.AsyncQueryFactory;
import dev.youika.vault.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private final AsyncQueryFactory factory;

    private final Map<Player, User> users = new ConcurrentHashMap<>();

    public UserRepository(AsyncQueryFactory factory) {
        this.factory = factory;

        Bukkit.getOnlinePlayers().forEach(this::asyncLoadUserOrNew);
    }

    public void shutdown() {
        CompletableFuture.runAsync(() ->
                users.keySet().forEach(player -> savePlayerInDatabaseFromRemoveInMap(player, true)));
    }

    public User unsafeUser(Player player) {
        return users.get(player);
    }

    public void savePlayerInDatabaseFromRemoveInMap(Player player, boolean remove) {
        factory.prepareUpdate("replace into `Players` values (?,?)", ps -> {
            try {
                ps.setString(1, player.getUniqueId().toString());
                ps.setDouble(2, unsafeUser(player).getBalance());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenAcceptAsync(result -> {
           if (remove && result > 0) users.remove(player);
        });
    }

    public void asyncLoadUserOrNew(Player player) {
        UUID uuid = player.getUniqueId();
        factory.prepareGet("select `balance` from `Players` where `uuid` = ?", ps -> {
            try {
                ps.setString(1, uuid.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenAcceptAsync(result -> {
            User user = new UserImpl(0D);
            if (result.isEmpty()) {
                savePlayerInDatabaseFromRemoveInMap(player, false);
            } else {
                user = new UserImpl(Double.parseDouble(result.all().get(0).getValue("balance")));
            }
            users.put(player, user);
        });
    }
}
