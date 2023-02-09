package dev.youika.testvault;

import dev.youika.testvault.commands.BalanceCommand;
import dev.youika.testvault.commands.PayCommand;
import dev.youika.testvault.commands.SetBalanceCommand;
import dev.youika.testvault.economy.EconomyProvider;
import dev.youika.testvault.listener.ConnectionListener;
import dev.youika.testvault.user.UserRepository;
import dev.youika.mysql.MySQL;
import dev.youika.mysql.factory.AsyncQueryFactory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class Vault extends JavaPlugin {

    private MySQL mySQL;
    private UserRepository repository;
    private EconomyProvider provider;

    @Override
    public void onDisable() {
        mySQL.shutdown();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ConfigurationSection section = getConfig().getConfigurationSection("sql");
        mySQL = new MySQL(this,
                section.getString("user"),
                section.getString("password"),
                section.getString("database"),
                section.getString("host"),
                section.getInt("port"));

        AsyncQueryFactory factory = mySQL.async();

        factory.unsafeUpdate("create table if not exists `Players` (`uuid` varchar(36) primary key, `balance` long);");

        repository = new UserRepository(factory);
        provider = new EconomyProvider(factory, repository);

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(repository), this);

        getCommand("balance").setExecutor(new BalanceCommand(provider));
        getCommand("pay").setExecutor(new PayCommand(provider));
        getCommand("setbalance").setExecutor(new SetBalanceCommand(provider));
        super.onEnable();
    }
}
