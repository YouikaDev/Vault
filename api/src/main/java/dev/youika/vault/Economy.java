package dev.youika.vault;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Economy {

    /**
     * @param sum сумма которую нужно забрать
     * @return вернёт баланс игрока после проведения операции
     */
    double withdraw(Player player, double sum);

    /**
     * @param sum сумма которую будем добавляться
     * @return вернёт баланс игрока после проведения операции
     */
    double deposit(Player player, double sum);

    /**
     * @param sum сумма которую нужно забрать
     * @return true - операция успешная, false - произошла ошибка
     */
    CompletableFuture<Boolean> withdrawAsync(Player player, double sum);

    /**
     * @param sum сумма которую будем добавляться
     * @return true - операция успешная, false - произошла ошибка
     */
    CompletableFuture<Boolean> depositAsync(Player player, double sum);

    /**
     * @return вернёт баланс игрока после проведения set операции
     */
    double setBalance(Player player, double sum);

    /**
     * @return вернёт баланс игрока
     */
    double getBalance(Player player);

    User getUser(Player player);

}
