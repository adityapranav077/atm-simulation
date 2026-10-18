package com.atm.model;
import java.math.BigDecimal;
public class Account {
  private final String accountNumber;
  private final String pin;
  private BigDecimal balance;
  public Account(String accountNumber, String pin, BigDecimal balance) {
    this.accountNumber = accountNumber; this.pin = pin; this.balance = balance;
  }
  public String getAccountNumber() { return accountNumber; }
  public boolean matchesPin(String suppliedPin) { return pin.equals(suppliedPin); }
  public BigDecimal getBalance() { return balance; }
  public void setBalance(BigDecimal balance) { this.balance = balance; }
}
