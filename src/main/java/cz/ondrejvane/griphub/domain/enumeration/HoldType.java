package cz.ondrejvane.griphub.domain.enumeration;

/**
 * The HoldType enumeration.
 */
public enum HoldType {
    CRIMP("crimp"),
    SLOPER("sloper"),
    JUG("jug"),
    PINCH("pinch"),
    FOOTHOLD("foothold"),
    UNDERCLING("undercling");

    private final String value;

    HoldType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
