import java.util.*;

class JournalLine {
    String accountTitle;
    double debit;
    double credit;

    public JournalLine(String accountTitle, double debit, double credit) {
        this.accountTitle = accountTitle.trim();
        this.debit = debit;
        this.credit = credit;
    }
}

class JournalEntry {
    private String date;
    private String description;
    ArrayList<JournalLine> lines = new ArrayList<>();

    public JournalEntry(String date, String description) {
        Objects.requireNonNull(date, "date cannot be null");
        Objects.requireNonNull(description, "description cannot be null");
        this.date = date.trim();
        this.description = description.trim();
    }

    public void setDate(String date) {
        Objects.requireNonNull(date, "date cannot be null");
        this.date = date.trim();
    }

    public void setDescription(String description) {
        Objects.requireNonNull(description, "description cannot be null");
        this.description = description.trim();
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public void addLine(String acct, double debit, double credit) {
        lines.add(new JournalLine(acct, debit, credit));
    }
}

class Account {
    String name;
    double balance;
    ArrayList<String> transactions = new ArrayList<>();

    public Account(String name) {
        this.name = name.trim();
        this.balance = 0;
    }

    public void addTransaction(double debit, double credit) {
        balance += debit - credit;
        transactions.add(String.format("Debit: %.2f, Credit: %.2f", debit, credit));
    }

    public boolean isDebit() {
        return balance >= 0;
    }

    public double getAmount() {
        return Math.abs(balance);
    }

    public void printLedger() {
        System.out.println("\nAccount: " + name);
        System.out.printf("+----------------------+------------+%n");
        System.out.printf("| Debit                | Credit     |%n");
        System.out.printf("+----------------------+------------+%n");
        for (String t : transactions) {
            String[] parts = t.split(", ");
            String debit = parts[0].split(": ")[1];
            String credit = parts[1].split(": ")[1];
            System.out.printf("| %-20s | %10s |%n",
                    debit.equals("0.00") ? "" : debit,
                    credit.equals("0.00") ? "" : credit);
        }
        System.out.printf("+----------------------+------------+%n");
        System.out.printf("Balance: %.2f (%s)%n", getAmount(), isDebit() ? "Debit" : "Credit");
    }
}

public class SimpleAccounting {
    static Scanner input = new Scanner(System.in);
    static ArrayList<JournalEntry> journal = new ArrayList<>();
    static ArrayList<Account> accounts = new ArrayList<>();
    static String journalTitle = " - Journal Entries";

    static String companyName = "";
    static String fiscalYear = "";

    public static void main(String[] args) {
        System.out.print("Enter Company Name: ");
        companyName = input.nextLine().trim();
        if (companyName.isEmpty()) companyName = "Unknown Company";

        System.out.print("Enter Fiscal Year: ");
        fiscalYear = input.nextLine().trim();
        if (fiscalYear.isEmpty()) fiscalYear = "Unknown Fiscal Year";

        int choice = -1;
        while (choice != 8) {
            System.out.println("\n====================================");
            System.out.println("     SIMPLE ACCOUNTING SYSTEM");
            System.out.println("====================================");
            System.out.println("Company: " + companyName + " | Fiscal Year: " + fiscalYear);
            System.out.println("\n1. Record Journal Entry");
            System.out.println("2. Show Journal");
            System.out.println("3. Show Ledger");
            System.out.println("4. Show Trial Balance");
            System.out.println("5. Show Balance Sheet");
            System.out.println("6. Edit Journal Entry");
            System.out.println("7. Delete Journal Entry");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 8.");
                continue;
            }

            switch (choice) {
                case 1 -> addJournalEntry();
                case 2 -> showJournal();
                case 3 -> showLedger();
                case 4 -> showTrialBalance();
                case 5 -> showBalanceSheet();
                case 6 -> editJournalEntry();
                case 7 -> deleteJournalEntry();
                case 8 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice. Please enter a number between 1 and 8.");
            }
        }
    }

    public static void addJournalEntry() {
        JournalEntry entry = createJournalEntry();
        journal.add(entry);
        System.out.println("Journal entry added!");
    }

    public static JournalEntry createJournalEntry() {
        System.out.println();
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = input.nextLine().trim();
        if (date.isEmpty()) date = "Unknown Date";

        System.out.print("Enter Description: ");
        String desc = input.nextLine().trim();
        if (desc.isEmpty()) desc = "No Description";

        JournalEntry entry = new JournalEntry(date, desc);

        System.out.println("Enter Debit Accounts (type 'done' to finish):");
        while (true) {
            System.out.print("Account: ");
            String acct = input.nextLine().trim();
            if (acct.equalsIgnoreCase("done")) break;
            double debit = getPositiveDouble("Debit Amount: ");
            entry.addLine(acct, debit, 0);
            updateAccount(acct, debit, 0);
        }

        System.out.println("Enter Credit Accounts (type 'done' to finish):");
        while (true) {
            System.out.print("Account: ");
            String acct = input.nextLine().trim();
            if (acct.equalsIgnoreCase("done")) break;
            double credit = getPositiveDouble("Credit Amount: ");
            entry.addLine(acct, 0, credit);
            updateAccount(acct, 0, credit);
        }

        return entry;
    }

    public static double getPositiveDouble(String prompt) {
        double value = -1;
        while (value < 0) {
            System.out.print(prompt);
            try {
                value = Double.parseDouble(input.nextLine());
                if (value < 0) System.out.println("Amount cannot be negative. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
        return value;
    }

    public static void updateAccount(String name, double debit, double credit) {
        for (Account acct : accounts) {
            if (acct.name.equalsIgnoreCase(name)) {
                acct.addTransaction(debit, credit);
                return;
            }
        }
        Account newAcct = new Account(name);
        newAcct.addTransaction(debit, credit);
        accounts.add(newAcct);
    }

    public static void showJournal() {
        if (journal.isEmpty()) {
            System.out.println("No journal entries recorded yet.");
            return;
        }
        System.out.println("\n" + companyName + " | Fiscal Year: " + fiscalYear);
        System.out.println(journalTitle);
        System.out.printf("+------------+----------------------+------------+------------+%n");
        System.out.printf("| Date       | Account              | Debit      | Credit     |%n");
        System.out.printf("+------------+----------------------+------------+------------+%n");

        for (int idx = 0; idx < journal.size(); idx++) {
            JournalEntry entry = journal.get(idx);
            System.out.printf("| Entry #%d | %-20s | %-10s | %-10s |%n", idx + 1, "", "", "");
            for (int i = 0; i < entry.lines.size(); i++) {
                JournalLine line = entry.lines.get(i);
                String dateStr = (i == 0) ? entry.getDate() : "";
                String accountDisplay = (line.credit > 0 && line.debit == 0) ? "    " + line.accountTitle : line.accountTitle;

                System.out.printf("| %-10s | %-20s | %10.2f | %10.2f |%n",
                        dateStr, accountDisplay, line.debit, line.credit);
            }
            System.out.printf("+------------+----------------------+------------+------------+%n");
        }
    }

    public static void showLedger() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts recorded yet.");
            return;
        }
        System.out.println("\n" + companyName + " | Fiscal Year: " + fiscalYear);
        System.out.println("\n=== LEDGER (T-ACCOUNTS) ===");
        for (Account acct : accounts) acct.printLedger();
    }

    public static void showTrialBalance() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts recorded yet.");
            return;
        }
        System.out.println("\n" + companyName + " | Fiscal Year: " + fiscalYear);
        double totalDebit = 0, totalCredit = 0;
        System.out.println("\n=== TRIAL BALANCE ===");
        System.out.printf("+----------------------+------------+------------+%n");
        System.out.printf("| Account              | Debit      | Credit     |%n");
        System.out.printf("+----------------------+------------+------------+%n");
        for (Account acct : accounts) {
            if (acct.isDebit()) {
                System.out.printf("| %-20s | %10.2f | %10s |%n", acct.name, acct.getAmount(), "");
                totalDebit += acct.getAmount();
            } else {
                System.out.printf("| %-20s | %10s | %10.2f |%n", acct.name, "", acct.getAmount());
                totalCredit += acct.getAmount();
            }
        }
        System.out.printf("+----------------------+------------+------------+%n");
        System.out.printf("| %-20s | %10.2f | %10.2f |%n", "TOTAL", totalDebit, totalCredit);
        System.out.printf("+----------------------+------------+------------+%n");
    }

    public static void showBalanceSheet() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts recorded yet.");
            return;
        }
        System.out.println("\n" + companyName + " | Fiscal Year: " + fiscalYear);

        double totalAssets = 0, totalLiabilities = 0, totalEquity = 0;
        double totalRevenue = 0, totalExpenses = 0, drawing = 0;

        System.out.println("\n=== BALANCE SHEET ===");

        System.out.println("\nASSETS:");
        System.out.printf("+----------------------+------------+%n");
        System.out.printf("| Account              | Amount     |%n");
        System.out.printf("+----------------------+------------+%n");
        for (Account acct : accounts) {
            String name = acct.name.toLowerCase();
            if (name.contains("cash") || name.contains("supplies" )|| name.contains("accounts receivable") ||
                name.contains("equipment") || name.contains("prepaid") || name.contains("furnitures and fixtures")) {
                System.out.printf("| %-20s | %10.2f |%n", acct.name, acct.getAmount());
                totalAssets += acct.getAmount();
            }
        }
        System.out.printf("+----------------------+------------+%n");
        System.out.printf("| %-20s | %10.2f |%n", "Total Assets", totalAssets);
        System.out.printf("+----------------------+------------+%n");

        System.out.println("\nLIABILITIES:");
        System.out.printf("+----------------------+------------+%n");
        System.out.printf("| Account              | Amount     |%n");
        System.out.printf("+----------------------+------------+%n");
        for (Account acct : accounts) {
            String name = acct.name.toLowerCase();
            if (name.contains("payable")) {
                System.out.printf("| %-20s | %10.2f |%n", acct.name, acct.getAmount());
                totalLiabilities += acct.getAmount();
            }
        }
        System.out.printf("+----------------------+------------+%n");
        System.out.printf("| %-20s | %10.2f |%n", "Total Liabilities", totalLiabilities);
        System.out.printf("+----------------------+------------+%n");

        System.out.println("\nOWNER'S EQUITY:");
        System.out.printf("+----------------------+------------+%n");
        System.out.printf("| Account              | Amount     |%n");
        System.out.printf("+----------------------+------------+%n");

        for (Account acct : accounts) {
            String name = acct.name.toLowerCase();
            if (name.contains("capital")) {
                System.out.printf("| %-20s | %10.2f |%n", acct.name, acct.getAmount());
                totalEquity += acct.getAmount();
            }
            if (name.contains("drawing")) drawing += acct.getAmount();
            if (name.contains("revenue") || name.contains("service")) totalRevenue += acct.getAmount();
            if (name.contains("expense") || name.contains("rent") || name.contains("salaries")) totalExpenses += acct.getAmount();
        }

        double netIncome = totalRevenue - totalExpenses;
        totalEquity = totalEquity + netIncome - drawing;
        System.out.printf("| %-20s | %10.2f |%n", "Net Income / (Loss)", netIncome);
        System.out.printf("| %-20s | %10.2f |%n", "Drawing", drawing);
        System.out.printf("+----------------------+------------+%n");
        System.out.printf("| %-20s | %10.2f |%n", "Total Equity", totalEquity);
        System.out.printf("+----------------------+------------+%n");

        System.out.printf("\nCheck: Total Assets = Total Liabilities + Equity -> %.2f = %.2f%n",
                totalAssets, totalLiabilities + totalEquity);
    }

    public static void editJournalEntry() {
        if (journal.isEmpty()) {
            System.out.println("No journal entries to edit.");
            return;
        }
        showJournal();
        System.out.print("Enter the entry number to edit: ");
        int idx = Integer.parseInt(input.nextLine()) - 1;
        if (idx < 0 || idx >= journal.size()) {
            System.out.println("Invalid entry number.");
            return;
        }

        JournalEntry entry = journal.get(idx);
        System.out.println("Editing Entry: " + entry.getDate() + " - " + entry.getDescription());

        System.out.print("Enter new date (leave blank to keep current): ");
        String date = input.nextLine().trim();
        if (!date.isEmpty()) entry.setDate(date);

        System.out.print("Enter new description (leave blank to keep current): ");
        String desc = input.nextLine().trim();
        if (!desc.isEmpty()) entry.setDescription(desc);

        System.out.print("Do you want to edit the debit/credit accounts? (yes/no): ");
        String editLines = input.nextLine().trim();

        if (editLines.equalsIgnoreCase("yes")) {
            for (JournalLine line : entry.lines) {
                for (Account acct : accounts) {
                    if (acct.name.equalsIgnoreCase(line.accountTitle)) {
                        acct.addTransaction(-line.debit, -line.credit);
                    }
                }
            }

            entry.lines.clear();

            System.out.println("Re-enter debit and credit accounts for this entry.");
            entry.lines.addAll(createJournalLines());

            accounts.removeIf(acct -> acct.transactions.isEmpty() || acct.getAmount() == 0);
        }

        System.out.println("Journal entry edited successfully.");
    }

    public static ArrayList<JournalLine> createJournalLines() {
        ArrayList<JournalLine> lines = new ArrayList<>();

        System.out.println("Enter Debit Accounts (type 'done' to finish):");
        while (true) {
            System.out.print("Account: ");
            String acct = input.nextLine().trim();
            if (acct.equalsIgnoreCase("done")) break;
            double debit = getPositiveDouble("Debit Amount: ");
            lines.add(new JournalLine(acct, debit, 0));
            updateAccount(acct, debit, 0);
        }

        System.out.println("Enter Credit Accounts (type 'done' to finish):");
        while (true) {
            System.out.print("Account: ");
            String acct = input.nextLine().trim();
            if (acct.equalsIgnoreCase("done")) break;
            double credit = getPositiveDouble("Credit Amount: ");
            lines.add(new JournalLine(acct, 0, credit));
            updateAccount(acct, 0, credit);
        }

        return lines;
    }

    public static void deleteJournalEntry() {
        if (journal.isEmpty()) {
            System.out.println("No journal entries to delete.");
            return;
        }
        showJournal();
        System.out.print("Enter the entry number to delete: ");
        int idx = Integer.parseInt(input.nextLine()) - 1;
        if (idx < 0 || idx >= journal.size()) {
            System.out.println("Invalid entry number.");
            return;
        }

        JournalEntry entry = journal.get(idx);

        for (JournalLine line : entry.lines) {
            for (Account acct : accounts) {
                if (acct.name.equalsIgnoreCase(line.accountTitle)) {
                    acct.addTransaction(-line.debit, -line.credit);
                }
            }
        }

        journal.remove(idx);

        accounts.removeIf(acct -> acct.transactions.isEmpty() || acct.getAmount() == 0);

        System.out.println("Journal entry deleted successfully.");
    }
}