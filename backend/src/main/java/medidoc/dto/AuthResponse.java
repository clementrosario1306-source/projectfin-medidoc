package medidoc.dto;

public class AuthResponse {

    private boolean success;
    private String message;
    private String token;
    private String role;
    private String uniqueId;
    private String fullName;
    private Object data;

    public AuthResponse() {}

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static AuthResponse success(String message, String token,
            String role, String uniqueId, String fullName, Object data) {
        AuthResponse r = new AuthResponse();
        r.success = true;
        r.message = message;
        r.token = token;
        r.role = role;
        r.uniqueId = uniqueId;
        r.fullName = fullName;
        r.data = data;
        return r;
    }

    public static AuthResponse error(String message) {
        return new AuthResponse(false, message);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}