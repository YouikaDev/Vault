package dev.youika.testvault.commands;

import dev.youika.testvault.economy.EconomyProvider;
import dev.youika.vault.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class PayCommand implements CommandExecutor {

    private final Economy provider;

    public PayCommand(Economy provider) {
        this.provider = provider;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;

        try {
            Player target = Bukkit.getPlayer(args[0]);
            double sum = Double.parseDouble(args[1]);

            CompletableFuture<Boolean> withdraw = provider.withdrawAsync(player, sum),
                    deposit = provider.depositAsync(target, sum);

            deposit.thenAcceptBoth(withdraw, (b1, b2) -> {
                if (b1 == b2) {
                    player.sendMessage("Вы успешно перевели игроку деньги. Ваш баланс: " + provider.getBalance(player));
                    target.sendMessage("Вам поступил перевод. Ваш баланс: " + provider.getBalance(target));
                } else {
                    player.sendMessage("Во время перевода произошла ошибка. Последний перевод поступил на ваш баланс");
                    provider.getUser(player).deposit(sum);

                    target.sendMessage("Во время перевода произошла ошибка. Последний перевод был вычтен из вашего баланса");
                    provider.getUser(target).withdraw(sum);
                }
            });
        } catch (ArrayIndexOutOfBoundsException exception) {
            player.sendMessage("/pay [target] [sum]");
        }

        return false;
    }
}
