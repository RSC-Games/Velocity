package velocity.animation.parser;

import java.util.Stack;

import velocity.animation.parser.ops.Directive;

/**
 * The most hacky part of the entire parser. Represents an animator state.
 */
public class State {
    /**
     * Determined maximum stack height for a parameter/value relationship.
     */
    static int maxHeight = 0;

    /**
     * The value case associated with this state.
     */
    Directive valueCase;

    /**
     * The value label in the file associated with the value case.
     */
    String condition;

    /**
     * Conditional size.
     */
    int sz;

    /**
     * Create an animator state.
     * 
     * @param names Particular parameter state required for this state.
     * @param valueCase The associated value case.
     */
    public State(Stack<String> names, Directive valueCase) {
        this.condition = buildName(names);
        this.valueCase = valueCase;
    }

    /**
     * Generate the condition from the provided parameter states.
     * 
     * @param cond The conditions.
     * @return The generated conditional.
     */
    public String buildName(Stack<String> cond) {
        Stack<String> b2f = new Stack<String>();

        while (cond.size() > 0) {
            b2f.push(cond.pop());
        }

        sz = b2f.size();
        if (sz > maxHeight)
            maxHeight = sz;

        String out = b2f.pop();

        while (b2f.size() > 0) {
            out += "~" + b2f.pop();
        }

        return out;
    }

    /**
     * Get this state's value case.
     * 
     * @return The value case.
     */
    public Directive getValueCase() {
        return valueCase;
    }

    /**
     * Get this state's condition.
     * 
     * @return The conditional.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Return this state's string representation.
     * 
     * @return The string representation.
     */
    public String toString() {
        return this.condition;
    }
}
