package org.geotoolkit.ogcapi.model.coverage;

/**
 * @author Quentin BIALOTA
 */
public enum CoverageResponseType {
    DataRecord("DataRecord"),
    DomainSet("DomainSet");

    private final String value;

    CoverageResponseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CoverageResponseType fromValue(String value) {
        for (CoverageResponseType status : CoverageResponseType.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid coverage response type: " + value);
    }
}
