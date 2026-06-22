package Model;

/**
 * A guest invited to an event, identified by a name and an optional e-mail.
 */
public class Attendee {
    private String name;
    private String email;

    /**
     * Creates an attendee.
     *
     * @param name  the guest's name
     * @param email the guest's e-mail (may be empty)
     */
    public Attendee(String name, String email) {
        this.name = name.trim();
        this.email = email.trim();
    }

    /** @return the guest's name */
    public String getName() {
        return name;
    }

    /** @return the guest's e-mail (possibly empty) */
    public String getEmail() {
        return email;
    }

    /**
     * @return {@code "name <email>"}, or just the name when no e-mail is set
     */
    @Override
    public String toString() {
        if (email.isEmpty()) {
            return name;
        }
        return name + " <" + email + ">";
    }
}
