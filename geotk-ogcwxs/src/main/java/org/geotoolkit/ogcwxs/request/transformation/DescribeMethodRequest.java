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
package org.geotoolkit.ogcwxs.request.transformation;

import java.util.List;
import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve a description of one or more coordinate operation methods known by a WCTS.
 *
 * <blockquote>
 * "The DescribeMethod operation allows WCTS clients to retrieve the definitions of one or
 * more identified operation methods, primarily for methods than can be used in user-defined
 * coordinate transformations. The definitions in the operation response are encoded in XML
 * using a GML application schema defined in Subclause 14.3.2. Implementation of the
 * DescribeMethod operation is optional for a web coordinate transformation server."
 * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, §14.1</a>
 * </blockquote>
 *
 * <pre>
 * http://www.lat-lon.de/transform&amp;service=WCTS&amp;request=DescribeMethod&amp;version=0.0.0
 *     &amp;methods=urn:ogc:def:method:EPSG:6.3:9807
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DescribeMethodRequest extends RequestParameters {

    private List<String> method;

    /**
     * <blockquote>
     * "Identifier of desired operation method" &mdash; one or more (mandatory), one for each
     * identifier input.
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 31, §14.2.1</a>
     * </blockquote>
     *
     * Method URI(s) to describe. (required)
     */
    public List<String> getMethod() {
        return method;
    }

    /**
     * @param method the method URI(s) to set
     * @see #getMethod()
     */
    public void setMethod(List<String> method) {
        this.method = method;
    }

    /**
     * @param method the method URI(s) to set
     * @see #getMethod()
     */
    public DescribeMethodRequest method(List<String> method) {
        setMethod(method);
        return this;
    }
}
