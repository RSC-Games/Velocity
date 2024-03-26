package velocity.animation;

import java.util.ArrayList;
import java.util.HashMap;

import velocity.animation.parser.Parameter;
import velocity.animation.parser.State;
import velocity.animation.parser.StateParser;
import velocity.renderer.RendererImage;

public class AnimStateMachine implements PluginAnimator {
    ArrayList<AnimParam> params;
    HashMap<String, AnimState> aStates;
    AnimState currentState = null;

    public AnimStateMachine(String path) {
        StateParser parser = new StateParser(path);
        parser.genState();

        ArrayList<Parameter> outP = parser.getParameters();
        ArrayList<State> states = parser.getStates();

        this.params = buildParameters(outP);
        this.aStates = genStateLUT(states);
    }

    private ArrayList<AnimParam> buildParameters(ArrayList<Parameter> outP) {
        ArrayList<AnimParam> bParam = new ArrayList<AnimParam>();
        for (Parameter p : outP) {
            bParam.add(new AnimParam(p.name, p.type));
        }

        return bParam;
    }

    private HashMap<String, AnimState> genStateLUT(ArrayList<State> states) {
        HashMap<String, AnimState> animStates = new HashMap<String, AnimState>();

        for (State s : states) {
            //System.out.println("got cond " + s.getCondition());
            animStates.put(
                s.getCondition(),
                new AnimState(s.getValueCase())
            );
        }

        return animStates;
    }

    public void setBool(String param, boolean val) {
        setString(param, "" + val);
    }

    public void setFloat(String param, float val) {
        setString(param, "" + val);
    }

    public void setInt(String param, int val) {
        setString(param, "" + val);
    }

    //^^ The others just funnel into here.
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

    public void animTick() {
        currentState.tick();
    }

    public RendererImage getDrawFrame() {
        return currentState.getDrawFrame();
    }
}
