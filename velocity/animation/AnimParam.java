package velocity.animation;

public class AnimParam {
    String name;
    String dtype;
    String currentValue = "";

    public AnimParam(String name, String dtype) {
        this.name = name;
        this.dtype = dtype;
    }

    public String getName() {
        return this.name;
    }

    public String getCurValue() {
        return this.currentValue;
    }

    public void setValue(String val) {
        validateField(val);  // Crashes if not a valid field.

        this.currentValue = val;
    }

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
