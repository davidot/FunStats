package com.davidot.funstats.results;

/**
 * todo
 *
 * @author davidot
 */
public class Variable {

    private String name;

    private Visibility visibility;

    private String type;

    private String containingClass;

    public Variable(String name, Visibility visibility, String type, String containingClass) {
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

    public Visibility getVisibility() {
        return visibility;
    }

    public String getType() {
        return type;
    }
}
