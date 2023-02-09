package dev.youika.testvault.commands;

import dev.youika.testvault.economy.EconomyProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final EconomyProvider provider;

    public PayCommand(EconomyProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;

        try {
            Player target = Bukkit.getPlayer(args[0]);
            double sum = Double.parseDouble(args[1]);

            player.sendMessage("Вы успешно перевели игроку деньги. Ваш баланс: " + provider.withdraw(player, sum));
            target.sendMessage("Вам поступил перевод. Ваш баланс: " + provider.deposit(target, sum));
        } catch (ArrayIndexOutOfBoundsException exception) {
            player.sendMessage("/pay [target] [sum]");
        }

        return false;
    }
}
