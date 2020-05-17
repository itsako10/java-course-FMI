package bg.sofia.uni.fmi.mjt.jira.issues;

public class Component {
    private String name;
    private String shortName;

    public Component(String name, String shortName) {
        if(name == null || shortName == null) {
            throw new RuntimeException("Null value!");
        }
        this.name = name;
        this.shortName = shortName;
    }

    public boolean equals(Component obj) {
        if(obj.name.equals(this.name) && obj.shortName.equals(this.shortName)) {
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }
}
