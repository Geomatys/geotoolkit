/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
import org.geotoolkit.ogcapi.dto.DataTransferObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Quentin BIALOTA
 * @author Guilhem LEGAL
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Conformance extends DataTransferObject implements CommonResponse {

    @JsonProperty("conformsTo")
    private List<Link> conformsTo;

    public Conformance() {
        conformsTo = new ArrayList<>();
    }

    public Conformance(List<Link> conformsTo) {
        this.conformsTo = conformsTo;
    }

    /**
     * @return the array list of link
     */
    public List<Link> getConformsTo() {
        return conformsTo;
    }

    /**
     * @param conformsTo the array list to set
     */
    public void setConformsTo(List<Link> conformsTo) {
        this.conformsTo = conformsTo;
    }
}
