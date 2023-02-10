package dev.youika.testvault.user;

import com.google.common.util.concurrent.AtomicDouble;
import dev.youika.vault.User;

import java.util.concurrent.CompletableFuture;

public class UserImpl implements User {

    private final AtomicDouble atomicBalance;

    public UserImpl(double balance) {
        atomicBalance = new AtomicDouble(balance);
    }

    @Override
    public double getBalance() {
        return atomicBalance.get();
    }

    @Override
    public double setBalance(double sum) {
        atomicBalance.set(sum);
        return getBalance();
    }

    @Override
    public boolean withdraw(double sum) {
        double prev = getBalance();
        double update = prev - sum;

        return atomicBalance.compareAndSet(prev, update);
    }

    @Override
    public boolean deposit(double sum) {

        double prev = getBalance();
        double update = prev + sum;

        return atomicBalance.compareAndSet(prev, update);
    }

    @Override
    public CompletableFuture<Boolean> withdrawCompletable(double sum) {
        return CompletableFuture.completedFuture(withdraw(sum));
    }

    @Override
    public CompletableFuture<Boolean> depositCompletable(double sum) {
        return CompletableFuture.completedFuture(deposit(sum));
    }
}
