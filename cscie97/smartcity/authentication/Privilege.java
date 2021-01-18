package cscie97.smartcity.authentication;

/**
 * Class represents privileges (entitlements) that users may have
 * @author Mariam Gogia
 */
public abstract class Privilege implements Visitable{
    private String id;
    private String name;
    private String description;

    public Privilege(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Composite pattern methods
    abstract void add(Privilege privilege);
    abstract void remove(Privilege privilege);


    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void acceptVisitor(Visitor visitor) {
        visitor.visitPrivilege(this);
    }

    public String toString() {
        return "ID: " +id + "\nName: "+name + "\nDescription: " +description;
    }
}
