package dev.youika.testvault.user;

import dev.youika.mysql.factory.AsyncQueryFactory;
import dev.youika.vault.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private final AsyncQueryFactory factory;

    private final Map<Player, User> users = new ConcurrentHashMap<>();

    public UserRepository(AsyncQueryFactory factory) {
        this.factory = factory;

        Bukkit.getOnlinePlayers().forEach(this::asyncLoadUserOrNew);
    }

    public User unsafeUser(Player player) {
        return users.get(player);
    }

    public void removeUserInMap(Player player) {
        users.remove(player);
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
                factory.prepareUpdate("replace into `Players` values (?,?)", ps -> {
                    try {
                        ps.setString(1, uuid.toString());
                        ps.setDouble(2, 0);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                // Убил 2 часа чтоб выглядело красиво, а в итоге это оказался рабочий вариант... *тильт*
                user = new UserImpl(result.all().stream().map(section -> Double.parseDouble(section.getValue("balance"))).findFirst().orElse(0D));
            }
            users.put(player, user);
        });
    }
}
