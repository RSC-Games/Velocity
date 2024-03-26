package velocity.animation.parser;

import java.util.Stack;

import velocity.animation.parser.ops.Directive;

public class State {
    Directive valueCase;
    String condition;
    int sz;
    static int maxHeight = 0;

    public State(Stack<String> names, Directive valueCase) {
        this.condition = buildName(names);
        this.valueCase = valueCase;
    }

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

    public Directive getValueCase() {
        return valueCase;
    }

    public String getCondition() {
        return condition;
    }

    public String toString() {
        return this.condition;
    }
}
