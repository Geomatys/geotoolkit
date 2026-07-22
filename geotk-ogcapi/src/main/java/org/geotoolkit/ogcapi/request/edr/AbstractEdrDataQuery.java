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
package org.geotoolkit.ogcapi.request.edr;

import java.util.List;

/**
 * Adds the parameter/CRS selection shared by every EDR query type that returns coverage data
 * ({@code position}, {@code radius}, {@code area}, {@code cube}, {@code trajectory},
 * {@code corridor}, {@code location}). {@code GetLocations} (the list of locations) is the only
 * query type that does not extend this class, since it returns a feature collection of location
 * metadata rather than a coverage, and has no parameter/CRS selection.
 *
 * @author Quentin Bialota (Geomatys)
 */
public abstract class AbstractEdrDataQuery extends AbstractEdrQuery {

    private List<String> parameterName;

    private String crs;

    /**
     * @return the requested parameter names, or {@code null} for all parameters.
     */
    public List<String> getParameterName() {
        return parameterName;
    }

    /**
     * @param parameterName the requested parameter names.
     * @see #getParameterName()
     */
    public void setParameterName(List<String> parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * @return the coordinate reference system of the response, or {@code null} for the default.
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the coordinate reference system of the response.
     * @see #getCrs()
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }
}
