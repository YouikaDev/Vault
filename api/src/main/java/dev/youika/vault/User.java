package dev.youika.vault;

import java.util.UUID;

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

}
