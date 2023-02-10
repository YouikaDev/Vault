package dev.youika.testvault.commands;

import dev.youika.testvault.economy.EconomyProvider;
import dev.youika.vault.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetBalanceCommand implements CommandExecutor {

    private final Economy provider;

    public SetBalanceCommand(Economy provider) {
        this.provider = provider;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        try {
            Player target = Bukkit.getPlayer(args[0]);
            double sum = Double.parseDouble(args[1]);

            commandSender.sendMessage("Установлен баланс игроку. " + provider.setBalance(target, sum));
        } catch (ArrayIndexOutOfBoundsException exception) {
            commandSender.sendMessage("/setbalance [target] [sum]");
        }

        return false;
    }
}
