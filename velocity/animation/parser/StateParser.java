package velocity.animation.parser;

import java.util.ArrayList;
import java.util.Stack;

import velocity.animation.parser.ops.*;

/**
 * Takes the AST and generates a state diagram for the animator state
 * machine.
 */
public class StateParser {
    /**
     * The previous state AST parser.
     */
    AFParserAST ast;

    /**
     * The accumulated parameters found.
     */
    ArrayList<Parameter> acc = new ArrayList<Parameter>();

    /**
     * Currently found conditional names.
     */
    Stack<String> names = new Stack<String>();

    /**
     * Last determined parameter state?
     */
    Stack<Parameter> lastState = new Stack<Parameter>();
    ArrayList<State> states = new ArrayList<State>();

    /**
     * Called by the animator state machine. Not meant for public
     * usage.
     * 
     * @param path Animator path.
     */
    public StateParser(String path) {
        AFTokenizer t = new AFTokenizer(path);
        AFParserCST cst = new AFParserCST(t);
        this.ast = new AFParserAST(cst);
    }

    /**
     * Build an animator state hashmap for later key lookups.
     * Multiple parsing passes will be required.
     */
    public void genState() {
        AnimRoot r = ast.buildTree();
        this.acc.clear();

        this.acc = findStates(r);

        /*
        for (Parameter p : acc) {
            System.out.println("Found param " + p.name + " of type " + p.type);
        } 

        for (State s : states) {
            System.out.println(s + " : " + s.getValueCase());
        }
        */
    }

    /**
     * Get the found parameter list.
     * 
     * @return The found parameters.
     */
    public ArrayList<Parameter> getParameters() {
        return acc;
    }

    /**
     * Get the possible states.
     * 
     * @return The list of built states.
     */
    public ArrayList<State> getStates() {
        return states;
    }

    /**
     * Find all of the parameters from a tree.
     * 
     * @param root The root of the tree to parse.
     * @return The found parameters.
     */
    private ArrayList<Parameter> findStates(AnimRoot root) {
        recFindStates(root);
        trimDuplicateParameters();
        return acc;
    }

    /**
     * Eliminate multiple found parameters if there are duplicates.
     */
    private void trimDuplicateParameters() {
        ArrayList<Parameter> n = new ArrayList<Parameter>();

        for (Parameter p : this.acc) {
            if (!paramIsIn(p, n))
               n.add(p);
        }
        
        this.acc = n;
    }

    /**
     * Check if a parameter is in a provided list.
     * 
     * @param ip Input parameter.
     * @param n List of new parameters.
     * @return Whether it is present.
     */
    private boolean paramIsIn(Parameter ip, ArrayList<Parameter> n) {
        for (Parameter p : n) {
            if (p.equals(ip)) return true;
        }
        return false;
    }

    /**
     * Find the states recursively down a tree.
     * 
     * @param croot Current root of the tree.
     */
    @SuppressWarnings("unchecked") // Type conversion is known safe but javac doesn't know that.
    private void recFindStates(Directive croot) {
        // Find a state and record it.
        if (isState(croot)) {
            ArrayList<Token> args = croot.getArgs();
            Parameter p = new Parameter(
                args.get(0).data,  // typecode
                args.get(1).data   // type name
            );
            acc.add(p);
            lastState.push(p);
        }

        // No more branches to find here.
        if (!croot.isBranch() && croot.getOp().tok != TokenID.TOK_OP_ANIM_ROOT)
            return;

        // We have more states to find.
        // These are discovered via values. Parameters don't help here.
        if (croot.getOp().tok == TokenID.TOK_OP_VALUE)
            names.push(croot.getArgs().get(0).data);
        
        for (Directive child : croot.getChildren()) {
            recFindStates(child);
        }

        // Decrease the value stack. Generate the parameters
        // and save the value case for later parsing.
        if (croot.getOp().tok == TokenID.TOK_OP_VALUE) {
            // We only want a node with valid state info, like USE_TEX
            if (croot.hasStateInfo())
                states.add(new State((Stack<String>)names.clone(), croot));
            names.pop();
        }

        // All states were found.
        if (isState(croot))
            lastState.pop();
    }

    /**
     * Determine if a directive is a state.
     * 
     * @param d Directive type
     * @return If it's a state (so a parameter).
     */
    private boolean isState(Directive d) {
        return d.getOp().tok == TokenID.TOK_OP_PARAM;
    }
}
