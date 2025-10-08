package main.java.com.expensetracker.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.core5.http.ContentType;

import java.util.*;

public class ConsoleApp {
    private static final String BASE_URL = "http://localhost:8080"; // Backend URL
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== EXPENSE TRACKER CLI ===");
            System.out.println("1. Register User");
            System.out.println("2. View Users");
            System.out.println("3. Delete User");
            System.out.println("4. Add Expense");
            System.out.println("5. View All Expenses");
            System.out.println("6. View Expense by ID");
            System.out.println("7. Delete Expense");
            System.out.println("8. Exit");
            System.out.print("Choice: ");
            int ch = sc.nextInt(); sc.nextLine();

            try {
                switch (ch) {
                    case 1 -> registerUser();
                    case 2 -> viewUsers();
                    case 3 -> deleteUser();
                    case 4 -> addExpense();
                    case 5 -> viewExpenses();
                    case 6 -> viewExpenseById();
                    case 7 -> deleteExpense();
                    case 8 -> { System.out.println("Exiting..."); return; }
                    default -> System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    // ---------------- USER METHODS ----------------
    private static void registerUser() {
        try {
            System.out.print("Name: ");
            String name = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();

            Map<String, String> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);
            user.put("password", password);

            String json = mapper.writeValueAsString(user);
            Request.post(BASE_URL + "/users/register")
                    .bodyString(json, ContentType.APPLICATION_JSON)
                    .execute().returnContent();
            System.out.println("User registered successfully!");
        } catch (HttpResponseException e) {
            switch (e.getStatusCode()) {
                case 400 -> System.out.println("Invalid input. Please check your data.");
                case 409 -> System.out.println("User already exists with this email!");
                default -> System.out.println("User already exists with this email!");
            }
        } catch (Exception e) {
            System.out.println("Failed to register user: " + e.getMessage());
        }
    }

    private static void viewUsers() {
        try {
            Content res = Request.get(BASE_URL + "/users").execute().returnContent();
            List<Map<String, Object>> users = mapper.readValue(res.asString(), new TypeReference<>() {});
            if (users.isEmpty()) System.out.println("No users found!");
            else printUsersTable(users);
        } catch (HttpResponseException e) {
            System.out.println("Error fetching users: HTTP " + e.getStatusCode());
        } catch (Exception e) {
            System.out.println("Failed to fetch users: " + e.getMessage());
        }
    }

  private static void deleteUser() {
    try {
        System.out.print("Enter User ID to delete: ");
        int id = sc.nextInt(); sc.nextLine();

        var response = Request.delete(BASE_URL + "/users/" + id)
                              .execute()
                              .returnResponse();

        int status = response.getCode(); // HTTP status code

        if (status == 204) {
            System.out.println("User deleted successfully!");
        } else if (status == 404) {
            System.out.println("User does not exist!");
        } else {
            System.out.println("Error deleting user: HTTP " + status);
        }

    } catch (HttpResponseException e) {
        System.out.println("Error deleting user: HTTP " + e.getStatusCode());
    } catch (Exception e) {
        System.out.println("Failed to delete user: " + e.getMessage());
    }
}


    // ---------------- EXPENSE METHODS ----------------
    private static void addExpense() {
        try {
            System.out.print("User ID: ");
            int userId = sc.nextInt(); sc.nextLine();
            System.out.print("Category ID: ");
            int categoryId = sc.nextInt(); sc.nextLine();
            System.out.print("Description: ");
            String description = sc.nextLine();
            System.out.print("Amount: ");
            double amount = sc.nextDouble(); sc.nextLine();
            System.out.print("Expense Date (yyyy-mm-dd): ");
            String expenseDate = sc.nextLine();

            Map<String, Object> expense = new HashMap<>();
            expense.put("userId", userId);
            expense.put("categoryId", categoryId);
            expense.put("description", description);
            expense.put("amount", amount);
            expense.put("expenseDate", expenseDate);

            String json = mapper.writeValueAsString(expense);
            Request.post(BASE_URL + "/expenses")
                    .bodyString(json, ContentType.APPLICATION_JSON)
                    .execute().returnContent();
            System.out.println("Expense added successfully!");
        } catch (HttpResponseException e) {
            switch (e.getStatusCode()) {
                case 400 -> System.out.println("Invalid input. Please check your data.");
                case 404 -> System.out.println("User or category not found!");
                default -> System.out.println("Error adding expense: HTTP " + e.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Failed to add expense: " + e.getMessage());
        }
    }

    private static void viewExpenses() {
        try {
            Content res = Request.get(BASE_URL + "/expenses").execute().returnContent();
            List<Map<String, Object>> expenses = mapper.readValue(res.asString(), new TypeReference<>() {});
            if (expenses.isEmpty()) System.out.println("No expenses found!");
            else printExpensesTable(expenses);
        } catch (HttpResponseException e) {
            System.out.println("Error fetching expenses: HTTP " + e.getStatusCode());
        } catch (Exception e) {
            System.out.println("Failed to fetch expenses: " + e.getMessage());
        }
    }

   private static void viewExpenseById() {
    try {
        System.out.print("Enter Expense ID: ");
        int id = sc.nextInt(); sc.nextLine();
        Content res = Request.get(BASE_URL + "/expenses/" + id).execute().returnContent();

        String json = res.asString().trim();
        if (json.isEmpty() || json.equals("null")) {
            System.out.println("Expense not found!");
            return;
        }

        Map<String, Object> expense = mapper.readValue(json, new TypeReference<>() {});

        System.out.printf("%-5s %-20s %-10s %-12s %-12s %-20s\n",
                          "ID", "UserID", "CategoryID", "Amount", "Date", "Description");
        System.out.println("-------------------------------------------------------------------------------");

        Integer expId = (Integer) expense.get("id");
        Integer userId = (Integer) expense.get("userId");
        Integer categoryId = (Integer) expense.get("categoryId");
        Double amount = ((Number) expense.get("amount")).doubleValue();
        String date = (String) expense.get("expenseDate");
        String description = (String) expense.get("description");

        System.out.printf("%-5s %-20s %-10s %-12s %-12s %-20s\n",
                          expId, userId, categoryId, amount, date, description);

    } catch (HttpResponseException e) {
        if (e.getStatusCode() == 404) {
            System.out.println("Expense not found!");
        } else {
            System.out.println("Error fetching expense: HTTP " + e.getStatusCode());
        }
    } catch (Exception e) {
        System.out.println("Failed to fetch expense: " + e.getMessage());
    }
} 

private static void deleteExpense() {
    try {
        System.out.print("Enter Expense ID to delete: ");
        int id = sc.nextInt(); sc.nextLine();

        // Execute DELETE request and get status code only
        var response = Request.delete(BASE_URL + "/expenses/" + id)
                              .execute()
                              .returnResponse();

        int status = response.getCode(); // HTTP status code

       if (status == 204) {
    System.out.println("Expense deleted successfully!");
} else if (status == 404) {
    System.out.println("Expense does not exist!");
} else {
    System.out.println("Error deleting expense: HTTP " + status);
}


    } catch (HttpResponseException e) {
        System.out.println("Error deleting expense: HTTP " + e.getStatusCode());
    } catch (Exception e) {
        System.out.println("Failed to delete expense: " + e.getMessage());
    }
}

    // ---------------- HELPERS ----------------
    private static void printUsersTable(List<Map<String, Object>> users) {
        System.out.printf("%-5s %-20s %-25s\n", "ID", "Name", "Email");
        System.out.println("----------------------------------------------");
        for (Map<String, Object> u : users) {
            System.out.printf("%-5s %-20s %-25s\n", u.get("id"), u.get("name"), u.get("email"));
        }
    }

    private static void printExpensesTable(List<Map<String, Object>> expenses) {
        System.out.printf("%-5s %-20s %-10s %-12s %-12s %-20s\n",
                          "ID", "UserID", "CategoryID", "Amount", "Date", "Description");
        System.out.println("-------------------------------------------------------------------------------");
        for (Map<String, Object> e : expenses) {
            Integer id = (Integer) e.get("id");
            Integer userId = (Integer) e.get("userId");
            Integer categoryId = (Integer) e.get("categoryId");
            Double amount = ((Number) e.get("amount")).doubleValue();
            String date = (String) e.get("expenseDate");
            String description = (String) e.get("description");

            System.out.printf("%-5s %-20s %-10s %-12s %-12s %-20s\n",
                              id, userId, categoryId, amount, date, description);
        }
    }
}
