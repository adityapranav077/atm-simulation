package com.atm.controller;
import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.service.AtmService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AtmController {
  private final AtmService service;
  public AtmController(AtmService service) { this.service = service;}
   @PostMapping("/reset")
  public Map<String, String> resetDemoAccount(
        @RequestParam(defaultValue = "1001") String accountNumber) {
    service.resetDemoAccount(accountNumber);
    return Map.of(
        "message", "Demo account reset successfully"
    );
  }
  @PostMapping("/auth/login")
  public Map<String,String> login(@Valid @RequestBody LoginRequest request) {
    Account account = service.login(request.accountNumber(), request.pin());
    return Map.of("message", "Login successful", "accountNumber", account.getAccountNumber());
  }
  @GetMapping("/accounts/{accountNumber}/balance")
  public Map<String,Object> balance(@PathVariable String accountNumber) {
    return Map.of("accountNumber", accountNumber, "balance", service.getBalance(accountNumber));
  }
  @PostMapping("/accounts/{accountNumber}/deposit")
  public Map<String,Object> deposit(@PathVariable String accountNumber, @Valid @RequestBody AmountRequest request) {
    return Map.of("message", "Deposit completed successfully.", "balance", service.deposit(accountNumber, request.amount()));
  }
  @PostMapping("/accounts/{accountNumber}/withdraw")
  public Map<String,Object> withdraw(@PathVariable String accountNumber, @Valid @RequestBody AmountRequest request) {
    return Map.of("message", "Withdrawal completed successfully.", "balance", service.withdraw(accountNumber, request.amount()));
  }
  @GetMapping("/accounts/{accountNumber}/transactions")
  public List<Transaction> transactions(@PathVariable String accountNumber) { return service.getTransactions(accountNumber); }

  public record LoginRequest(@NotBlank(message="Account number is required.") String accountNumber,
                             @NotBlank(message="PIN is required.") String pin) {}
  public record AmountRequest(@NotNull(message="Amount is required.") @Positive(message="Amount must be greater than zero.") BigDecimal amount) {}
}

@RestControllerAdvice
class ApiExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,String>> badRequest(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,String>> validation(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream().findFirst()
      .map(error -> error.getDefaultMessage()).orElse("Invalid request.");
    return ResponseEntity.badRequest().body(Map.of("message", message));
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String,String>> unreadable() {
    return ResponseEntity.badRequest().body(Map.of("message", "Invalid request body."));
  }
}
