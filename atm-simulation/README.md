# ATM Simulation

An educational ATM application with an HTML/CSS/JavaScript frontend and a Java Spring Boot REST API.

## Demo account
- Account number: `1001`
- PIN: `1234`
- Starting balance: ₹5,000.00

## Requirements
- JDK 17+
- Maven
- A modern browser

## Start the backend
Open a terminal in `backend/` and run:
```bash
mvn spring-boot:run
```
The API runs at `http://localhost:8080`.

## Start the frontend
From the project root, run:
```bash
python -m http.server 5500
```
Open `http://localhost:5500/frontend/`.

## Features
- Demo login
- Balance inquiry
- Deposit and withdrawal
- Transaction history
- Responsive interface

## API endpoints
- `POST /api/auth/login`
- `GET /api/accounts/{accountNumber}/balance`
- `POST /api/accounts/{accountNumber}/deposit`
- `POST /api/accounts/{accountNumber}/withdraw`
- `GET /api/accounts/{accountNumber}/transactions`

## Important
This is a learning prototype, not real banking software. It uses demo credentials and in-memory storage; all balances and transactions reset when the backend restarts. Do not use real account information or PINs.
