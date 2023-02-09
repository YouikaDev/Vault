package dev.youika.testvault.commands;

import dev.youika.testvault.economy.EconomyProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    private final EconomyProvider provider;

    public BalanceCommand(EconomyProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        commandSender.sendMessage("Ваш баланс: " + provider.getBalance((Player) commandSender));
        return false;
    }
}
