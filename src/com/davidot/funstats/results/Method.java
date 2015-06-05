package com.davidot.funstats.results;

/**
 * todo
 *
 * @author davidot
 */
public class Method {

    private String name;

    private Visibility visibility;

    private Variable[] params;

    private String containingClass;

    public Method(String name, Visibility visibility, Variable[] params, String containingClass) {
        this.name = name;
        this.visibility = visibility;
        this.params = params;
        this.containingClass = containingClass;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Variable v:params) {
            builder.append(v.getType()).append(" ").append(v.getName());
        }
        return visibility + " " + builder.toString() + " " + name + System.lineSeparator();
    }

    public String getName() {
        return name;
    }

    public Visibility getVisibility() {
        return visibility;
    }

}
