package Model;

/**
 * Participant of an event (name and email).
 */
public class Attendee {
    private String name;
    private String email;

    public Attendee(String name, String email) {
        this.name = name.trim();
        this.email = email.trim();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        if (email.isEmpty()) {
            return name;
        }
        return name + " <" + email + ">";
    }
}
