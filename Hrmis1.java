import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Hrmis1{
    private Connection connection;

    public Hrmis1() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/Hrmis1_db?useSSL=false";
            String user = "root";
            String password = "your_password";
            connection = DriverManager.getConnection(url, user, password);

            if (connection != null) {
                System.out.println("Connected to the database!");
            }
        } catch (SQLException e) {
            System.out.println("Connection failed. Check output console");
            e.printStackTrace();
        }
    }

    public void addUser(User user) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                users.add(new User(username, password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateUserPassword(String username, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String username) {
        String query = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addEmployee(Employee employee) {
        String query = "INSERT INTO employees (id, name, position, salary) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, employee.getId());
            stmt.setString(2, employee.getName());
            stmt.setString(3, employee.getPosition());
            stmt.setDouble(4, employee.getSalary());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Department> getDepartments() {
        List<Department> departments = new ArrayList<>();
        String query = "SELECT * FROM departments";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                Department dept = new Department(name);
                departments.add(dept);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    public void addDepartment(Department department) {
        String query = "INSERT INTO departments (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, department.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listAllDepartments() {
        StringBuilder allDepartments = new StringBuilder();
        for (Department dept : getDepartments()) {
            allDepartments.append(dept.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, allDepartments.toString(), "Departments and Employees", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        Hrmis1 system = new Hrmis1();

        User admin = new User("admin", "admin123");
        system.addUser(admin);

        Department itDept = new Department("IT");
        Department hrDept = new Department("HR");
        system.addDepartment(itDept);
        system.addDepartment(hrDept);

        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");
        User loggedInUser = system.getUsers().stream()
                .filter(user -> user.getUsername().equals(username) && user.authenticate(password))
                .findFirst()
                .orElse(null);

        if (loggedInUser != null) {
            JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

            String empName = JOptionPane.showInputDialog("Enter Employee Name:");
            String empId = JOptionPane.showInputDialog("Enter Employee ID:");
            String empPosition = JOptionPane.showInputDialog("Enter Employee Position:");
            double empSalary = Double.parseDouble(JOptionPane.showInputDialog("Enter Employee Salary:"));

            Employee emp = new Employee(empId, empName, empPosition, empSalary);

            String[] options = {"IT", "HR"};
            int deptChoice = JOptionPane.showOptionDialog(null, "Select Department", "Department Selection",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (deptChoice == 0) {
                itDept.addEmployee(emp);
            } else {
                hrDept.addEmployee(emp);
            }

            system.addEmployee(emp);
            system.listAllDepartments();

        } else {
            JOptionPane.showMessageDialog(null, "Login failed! Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}

class Employee {
    private String id;
    private String name;
    private String position;
    private double salary;

    public Employee(String id, String name, String position, double salary) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "Employee ID: " + id + "\nName: " + name + "\nPosition: " + position + "\nSalary: $" + salary;
    }
}

class Department {
    private String name;
    private List<Employee> employees;

    public Department(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Department: ").append(name).append("\nEmployees:\n");
        for (Employee emp : employees) {
            sb.append(emp.toString()).append("\n");
        }
        return sb.toString();
    }
}
