package com.atm.model;
import java.math.BigDecimal;
import java.time.Instant;
public class Transaction {
  private final String type;
  private final BigDecimal amount;
  private final Instant timestamp;
  public Transaction(String type, BigDecimal amount) { this.type = type; this.amount = amount; this.timestamp = Instant.now(); }
  public String getType() { return type; }
  public BigDecimal getAmount() { return amount; }
  public Instant getTimestamp() { return timestamp; }
}
