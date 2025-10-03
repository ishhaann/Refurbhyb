public class User {
    private final int id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private String password;

    public User(int id, String name, String email, String phone, Role role, String password) {
        this.id = id;
        this.name = name;
        this.email = email.toLowerCase();
        this.phone = phone;
        this.role = role;
        this.password = password;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', phone='" + phone + "', role=" + role + "}";
    }
}
