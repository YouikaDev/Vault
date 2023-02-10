package dev.youika.vault;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface User {

    /**
     * @return вернёт баланс игрока
     */
    double getBalance();

    /**
     * @return вернёт баланс игрока после проведения set операции
     */
    double setBalance(double sum);

    /**
     * @param sum сумма которую нужно забрать
     * @return вернёт баланс игрока после проведения операции
     */
    boolean withdraw(double sum);

    /**
     * @param sum сумма которую будем добавляться
     * @return вернёт баланс игрока после проведения операции
     */
    boolean deposit(double sum);

    /**
     * @param sum сумма которую нужно забрать
     * @return вернёт true если успешно
     */
    CompletableFuture<Boolean> withdrawCompletable(double sum);

    /**
     * @param sum сумма которую будем добавляться
     * @return вернёт true если успешно
     */
    CompletableFuture<Boolean> depositCompletable(double sum);

}
