package dev.youika.testvault.listener;

import dev.youika.testvault.user.UserRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final UserRepository repository;

    public ConnectionListener(UserRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        repository.asyncLoadUserOrNew(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        repository.savePlayerInDatabaseFromRemoveInMap(event.getPlayer(), true);
    }

}
