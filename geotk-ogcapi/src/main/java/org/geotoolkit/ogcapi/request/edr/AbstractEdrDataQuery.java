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
 * <p>As in {@link AbstractEdrQuery}, only plain getters/setters are declared here; each subclass
 * redeclares its own fluent method per inherited field and links back to the getter below.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public abstract class AbstractEdrDataQuery extends AbstractEdrQuery {

    private List<String> parameterName;

    private String crs;

    /**
     * <blockquote><q>
     *     Comma delimited list of parameter names (available options are listed in the
     *     parameter_names section of the collections metadata response)
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_bbda46d4-04c5-426b-bea3-230d592fe1c2"> OGC API - Environmental Data Retrieval (EDR) - Position query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; all parameters are returned when omitted.</li>
     * </ul>
     * <pre>parameter-name=Visibility,Air%20Temperature</pre>
     *
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
     * <blockquote><q>
     *     Coordinate reference system identifier for the coords values and output data
     *     (available options are listed in the collections metadata response)
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_bbda46d4-04c5-426b-bea3-230d592fe1c2"> OGC API - Environmental Data Retrieval (EDR) - Position query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; the collection's default CRS is used when omitted.</li>
     * </ul>
     * <pre>
     * crs=EPSG:4326
     * crs=A_CUSTOM_LABEL
     * </pre>
     *
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
