package com.atm.service;
import com.atm.model.Account;
import com.atm.model.Transaction;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AtmService {
  private final Map<String, Account> accounts = new ConcurrentHashMap<>();
  private final Map<String, List<Transaction>> transactions = new ConcurrentHashMap<>();
  public AtmService() {
    accounts.put("1001", new Account("1001", "1234", new BigDecimal("5000.00")));
    transactions.put("1001", new CopyOnWriteArrayList<>());
  }
  public synchronized Account login(String accountNumber, String pin) {
    Account account = accounts.get(accountNumber);
    if (account == null || !account.matchesPin(pin)) throw new IllegalArgumentException("Invalid account number or PIN.");
    return account;
  }
  public synchronized BigDecimal getBalance(String accountNumber) { return getAccount(accountNumber).getBalance(); }
  public synchronized BigDecimal deposit(String accountNumber, BigDecimal amount) {
    Account account = getAccount(accountNumber);
    BigDecimal value = validateAmount(amount);
    account.setBalance(account.getBalance().add(value));
    addTransaction(accountNumber, new Transaction("DEPOSIT", value));
    return account.getBalance();
  }
  public synchronized BigDecimal withdraw(String accountNumber, BigDecimal amount) {
    Account account = getAccount(accountNumber);
    BigDecimal value = validateAmount(amount);
    if (account.getBalance().compareTo(value) < 0) throw new IllegalArgumentException("Insufficient balance for this withdrawal.");
    account.setBalance(account.getBalance().subtract(value));
    addTransaction(accountNumber, new Transaction("WITHDRAWAL", value));
    return account.getBalance();
  }
  public synchronized List<Transaction> getTransactions(String accountNumber) {
    getAccount(accountNumber);
    List<Transaction> copy = new ArrayList<>(transactions.getOrDefault(accountNumber, List.of()));
    copy.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
    return copy;
  }
  private Account getAccount(String accountNumber) {
    Account account = accounts.get(accountNumber);
    if (account == null) throw new IllegalArgumentException("Account not found.");
    return account;
  }
  private BigDecimal validateAmount(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Enter an amount greater than zero.");
    try { return amount.setScale(2, RoundingMode.UNNECESSARY); }
    catch (ArithmeticException e) { throw new IllegalArgumentException("Amount can have at most two decimal places."); }
  }
  private void addTransaction(String accountNumber, Transaction transaction) {
    transactions.computeIfAbsent(accountNumber, key -> new CopyOnWriteArrayList<>()).add(transaction);
  }
}
