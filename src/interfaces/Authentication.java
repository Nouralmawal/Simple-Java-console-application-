package interfaces;

public interface Authentication {
    boolean login(String email, String password);

    void register(String email, String password);
}
