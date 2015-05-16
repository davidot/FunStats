package com.davidot.funstats.results;

/**
 * todo
 *
 * @author davidot
 */
public class Variable {

    private String name;

    private VariableVisibility visibility;

    private String type;

    private String containingClass;

    public Variable(String name, VariableVisibility visibility, String type, String containingClass) {
        this.name = name;
        this.visibility = visibility;
        this.type = type;
        this.containingClass = containingClass;
    }

    @Override
    public String toString() {
        return visibility + " " + type + " " + name + System.lineSeparator();
    }

    public String getName() {
        return name;
    }

    public VariableVisibility getVisibility() {
        return visibility;
    }
}
