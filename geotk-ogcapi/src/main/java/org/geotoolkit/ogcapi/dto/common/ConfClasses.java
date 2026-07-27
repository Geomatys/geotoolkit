/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogcapi.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * ConfClasses
 */
@JsonPropertyOrder({
    ConfClasses.JSON_PROPERTY_CONFORMS_TO
})
public final class ConfClasses extends DataTransferObject {

    public static final String JSON_PROPERTY_CONFORMS_TO = "conformsTo";

    private List<String> conformsTo = new ArrayList<>();

    public ConfClasses() {
    }

    public ConfClasses conformsTo(List<String> conformsTo) {
        this.conformsTo = conformsTo;
        return this;
    }

    public ConfClasses addConformsToItem(String conformsToItem) {
        if (this.conformsTo == null) {
            this.conformsTo = new ArrayList<>();
        }
        this.conformsTo.add(conformsToItem);
        return this;
    }

    /**
     * Get conformsTo
     *
     * @return conformsTo
     */
    @JsonProperty(JSON_PROPERTY_CONFORMS_TO)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<String> getConformsTo() {
        return conformsTo;
    }

    @JsonProperty(JSON_PROPERTY_CONFORMS_TO)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setConformsTo(List<String> conformsTo) {
        this.conformsTo = conformsTo;
    }

    /**
     * Returns true if the given conformance class is in the list.
     *
     * This method checks for minor errors, if conformance URI may start with http or https.
     */
    public boolean isConformTo(String conformance) {
        if (conformsTo.contains(conformance)) return true;

        if (conformance.startsWith("https")) {
            return conformsTo.contains(conformance.replaceFirst("https", "http"));
        } else {
            return conformsTo.contains(conformance.replaceFirst("http", "https"));
        }
    }

    /**
     * Return true if this confClasses object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfClasses confClasses = (ConfClasses) o;
        return Objects.equals(this.conformsTo, confClasses.conformsTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conformsTo);
    }

}
