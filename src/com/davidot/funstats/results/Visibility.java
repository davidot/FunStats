package com.davidot.funstats.results;

/**
 * todo
 *
 * @author davidot
 */
public enum Visibility {

    PUBLIC("public"),
    PROTECTED("protected"),
    PACKAGE_PRIVATE(null),
    PRIVATE("private"),
    INLINE(null),
    PARAMETER(null);


    private final String string;

    Visibility(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        String name = name();
        return name.substring(0,1) + name.substring(1).toLowerCase();
    }
}
