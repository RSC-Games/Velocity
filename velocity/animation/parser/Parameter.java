package velocity.animation.parser;

public class Parameter {
    public final String name;
    public final String type;

    public Parameter(String type, String name) {
        this.name = name;
        this.type = type;
    }

    public boolean equals(Parameter other) {
        return this.name.equals(other.name) && this.type.equals(other.type);
    }
}
