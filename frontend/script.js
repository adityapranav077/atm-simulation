const API = "http://localhost:8080/api";
const loginPanel = document.getElementById("login-panel");
const dashboard = document.getElementById("dashboard");
const messageBox = document.getElementById("message");
let activeAccount = null;
let messageTimer;

function showMessage(text, error = false) {
  messageBox.textContent = text;
  messageBox.classList.remove("hidden", "error");
  if (error) messageBox.classList.add("error");
  clearTimeout(messageTimer);
  messageTimer = setTimeout(() => messageBox.classList.add("hidden"), 4500);
}

async function api(path, options = {}) {
  let response;
  try {
    response = await fetch(API + path, {
      ...options,
      headers: { "Content-Type": "application/json", ...(options.headers || {}) }
    });
  } catch {
    throw new Error("Cannot reach the backend. Start Spring Boot and try again.");
  }
  const data = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(data.message || "Request failed.");
  return data;
}

function money(value) {
  return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" }).format(value);
}

async function refreshBalance() {
  const data = await api(`/accounts/${encodeURIComponent(activeAccount)}/balance`);
  document.getElementById("balance").textContent = money(data.balance);
}

async function refreshTransactions() {
  const transactions = await api(`/accounts/${encodeURIComponent(activeAccount)}/transactions`);
  const container = document.getElementById("transactions");
  container.replaceChildren();
  if (!transactions.length) {
    const empty = document.createElement("p");
    empty.className = "muted";
    empty.textContent = "No transactions yet. Make a deposit to get started.";
    container.appendChild(empty);
    return;
  }
  transactions.forEach(item => {
    const row = document.createElement("div");
    row.className = "transaction";
    const symbol = document.createElement("div");
    symbol.className = "symbol";
    symbol.textContent = item.type === "DEPOSIT" ? "↓" : "↑";
    const info = document.createElement("div");
    info.className = "info";
    const name = document.createElement("p");
    name.className = "transaction-name";
    name.textContent = item.type === "DEPOSIT" ? "Cash deposit" : "Cash withdrawal";
    const date = document.createElement("p");
    date.className = "transaction-date";
    date.textContent = new Date(item.timestamp).toLocaleString();
    info.append(name, date);
    const amount = document.createElement("div");
    amount.className = "amount " + (item.type === "DEPOSIT" ? "credit" : "debit");
    amount.textContent = (item.type === "DEPOSIT" ? "+" : "−") + money(item.amount);
    row.append(symbol, info, amount);
    container.appendChild(row);
  });
}

async function refreshAccount() {
  await Promise.all([refreshBalance(), refreshTransactions()]);
}

document.getElementById("login-form").addEventListener("submit", async event => {
  event.preventDefault();
  const accountNumber = document.getElementById("account-number").value.trim();
  const pin = document.getElementById("pin").value.trim();
  try {
    const data = await api("/auth/login", { method: "POST", body: JSON.stringify({ accountNumber, pin }) });
    activeAccount = data.accountNumber;
    document.getElementById("account-ending").textContent = activeAccount.slice(-4);
    loginPanel.classList.add("hidden");
    dashboard.classList.remove("hidden");
    await refreshAccount();
    showMessage("Signed in successfully.");
  } catch (error) {
    showMessage(error.message, true);
  }
});

async function submitMoney(event, action) {
  event.preventDefault();
  if (!activeAccount) return;
  const input = document.getElementById(action + "-amount");
  const amount = Number(input.value);
  try {
    const data = await api(`/accounts/${encodeURIComponent(activeAccount)}/${action}`, {
      method: "POST", body: JSON.stringify({ amount })
    });
    input.value = "";
    await refreshAccount();
    showMessage(data.message);
  } catch (error) {
    showMessage(error.message, true);
  }
}

document.getElementById("deposit-form").addEventListener("submit", event => submitMoney(event, "deposit"));
document.getElementById("withdraw-form").addEventListener("submit", event => submitMoney(event, "withdraw"));
document.getElementById("refresh-button").addEventListener("click", async () => {
  try { await refreshAccount(); showMessage("Account information refreshed."); }
  catch (error) { showMessage(error.message, true); }
});
document.getElementById("logout-button").addEventListener("click", () => {
  activeAccount = null;
  dashboard.classList.add("hidden");
  loginPanel.classList.remove("hidden");
  document.getElementById("pin").value = "";
  showMessage("You have signed out.");
});
