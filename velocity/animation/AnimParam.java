package velocity.animation;

/**
 * Animator parameter. High-level abstraction of a parameter for setting
 * states.
 */
class AnimParam {
    /**
     * Parameter name.
     */
    String name;

    /**
     * Parameter data type.
     */
    String dtype;

    /**
     * Parameter set value.
     */
    String currentValue = "";

    /**
     * Create the parameter for use. 
     * 
     * @param name The parameter name.
     * @param dtype The data type.
     */
    public AnimParam(String name, String dtype) {
        this.name = name;
        this.dtype = dtype;
    }

    /**
     * Get the parameter's name.
     * 
     * @return The name of the parameter.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the current parameter value. 
     * 
     * @return The current parameter value.
     */
    public String getCurValue() {
        return this.currentValue;
    }

    /**
     * Set the parameter value. Must be the correct data type.
     * 
     * @param val The value to set.
     */
    public void setValue(String val) {
        validateField(val);  // Crashes if not a valid field.
        this.currentValue = val;
    }

    /**
     * Validate a field's type. Basically a huge assert.
     * 
     * @param type The parameter value.
     */
    private void validateField(String type) {
        if (this.dtype.equals("str"))
            return;

        else if (this.dtype.equals("float"))
            Float.parseFloat(type);

        else if (this.dtype.equals("int"))
            Integer.parseInt(type);

        else if (this.dtype.equals("bool"))
            Boolean.parseBoolean(type);
    }
}
