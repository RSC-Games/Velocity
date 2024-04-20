package velocity.animation.parser;

/**
 * AST definition of a parameter.
 */
public class Parameter {
    /**
     * Name of a parameter.
     */
    public final String name;

    /**
     * Data type of the parameter.
     */
    public final String type;

    /**
     * Create the parameter representation.
     * 
     * @param type Parameter type.
     * @param name Parameter name.
     */
    public Parameter(String type, String name) {
        this.name = name;
        this.type = type;
    }

    /**
     * Compare two parameters against each other.
     * 
     * @param other The other parameter.
     * @return Whether they are equivalent.
     */
    public boolean equals(Parameter other) {
        return this.name.equals(other.name) && this.type.equals(other.type);
    }
}
