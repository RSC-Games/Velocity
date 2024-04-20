package velocity.animation;

import java.util.ArrayList;
import java.util.HashMap;

import velocity.animation.parser.Parameter;
import velocity.animation.parser.State;
import velocity.animation.parser.StateParser;
import velocity.renderer.RendererImage;

/**
 * One of multiple different PluginAnimator types. This one parses a
 * nested file.
 */
public class AnimStateMachine implements PluginAnimator {
    /**
     * All params contained within the parsed file.
     */
    ArrayList<AnimParam> params;

    /**
     * All states, addressable by name, referred to by the parameters.
     */
    HashMap<String, AnimState> aStates;

    /**
     * Current animator state. None by default.
     */
    AnimState currentState = null;

    /**
     * Create the animator state machine.
     * 
     * @param path The path to the animator file.
     */
    public AnimStateMachine(String path) {
        StateParser parser = new StateParser(path);
        parser.genState();

        ArrayList<Parameter> outP = parser.getParameters();
        ArrayList<State> states = parser.getStates();

        this.params = buildParameters(outP);
        this.aStates = genStateLUT(states);
    }

    /**
     * Build the list of parameters from the parsed data.
     * 
     * @param outP The identified parameters by the parser.
     * @return A processed array of parameters.
     */
    private ArrayList<AnimParam> buildParameters(ArrayList<Parameter> outP) {
        ArrayList<AnimParam> bParam = new ArrayList<AnimParam>();
        for (Parameter p : outP) {
            bParam.add(new AnimParam(p.name, p.type));
        }

        return bParam;
    }

    /**
     * Generate the state look up table for transitions and state changes.
     * 
     * @param states The parsed states.
     * @return The generated executable states.
     */
    private HashMap<String, AnimState> genStateLUT(ArrayList<State> states) {
        HashMap<String, AnimState> animStates = new HashMap<String, AnimState>();

        for (State s : states) {
            animStates.put(
                s.getCondition(),
                new AnimState(s.getValueCase())
            );
        }

        return animStates;
    }

    /**
     * Set a boolean parameter.
     * 
     * @param param The parameter to set.
     * @param val The new value to set.
     */
    public void setBool(String param, boolean val) {
        setString(param, "" + val);
    }

    /**
     * Set a float parameter.
     * 
     * @param param The parameter to set.
     * @param val The new value to set.
     */
    public void setFloat(String param, float val) {
        setString(param, "" + val);
    }

    /**
     * Set a integer parameter.
     * 
     * @param param The parameter to set.
     * @param val The new value to set.
     */
    public void setInt(String param, int val) {
        setString(param, "" + val);
    }

    /**
     * Set a string parameter. The other functions trampoline here.
     * 
     * @param param The parameter to set.
     * @param val The new value to set.
     */
    public void setString(String param, String val) {
        AnimParam findP = null;

        for (AnimParam p : params) {
            if (p.name.equals(param)) {
                findP = p;
                break;
            }
        }

        if (findP == null) {
            System.out.println("Cannot find parameter " + param);
            return;
        }

        findP.setValue(val);

        // Regenerate current state from all values.
        generateState();
    }

    /**
     * Generate the state transition required when a parameter is updated.
     */
    public void generateState() {
        String paramVal = "";

        for (AnimParam p : params) {
            paramVal += p.getCurValue() + "~";
        }

        paramVal = paramVal.substring(0, paramVal.length() - 1);

        AnimState s = aStates.get(paramVal);

        if (s == null) {
            //System.out.println("Cannot find state " + paramVal);
            return;
        }

        // Set current usable state.
        this.currentState = s;
    }

    /**
     * Update the current animator state.
     */
    public void animTick() {
        currentState.tick();
    }

    /**
     * Get the current frame to draw from the current state.
     */
    public RendererImage getDrawFrame() {
        return currentState.getDrawFrame();
    }
}
