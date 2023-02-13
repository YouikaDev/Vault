package dev.youika.vault;

import java.util.Optional;
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
    Optional<Double> withdraw(double sum);

    /**
     * @param sum сумма которую будем добавляться
     * @return вернёт баланс игрока после проведения операции
     */
    Optional<Double> deposit(double sum);

    /**
     * @param sum сумма которую нужно забрать
     * @return вернёт true если успешно
     */
    CompletableFuture<Optional<Double>> withdrawCompletable(double sum);

    /**
     * @param sum сумма которую будем добавляться
     * @return вернёт true если успешно
     */
    CompletableFuture<Optional<Double>> depositCompletable(double sum);

}
