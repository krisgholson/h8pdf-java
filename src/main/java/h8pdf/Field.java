package h8pdf;

public class Field {
    String name;
    String value;
    String type;
    boolean readOnly;
    boolean required;

    public Field(String name, String value) {
        setName(name);
        setValue(value);
    }

    public Field(String name, String value, String type, boolean readOnly, boolean required) {
        setName(name);
        setValue(value);
        setType(type);
        setReadOnly(readOnly);
        setRequired(required);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
