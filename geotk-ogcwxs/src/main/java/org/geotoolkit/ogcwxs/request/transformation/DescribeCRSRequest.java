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
 * To retrieve a description of one or more CRSs known by a WCTS.
 *
 * <blockquote>
 * "The DescribeCRS operation allows WCTS clients to retrieve the definitions of one or more
 * identified coordinate reference systems (CRSs). The definitions in the operation response
 * are encoded in XML using GML 3.1.1. Implementation of the DescribeCRS operation is optional
 * for a web coordinate transformation server."
 * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, §13.1</a>
 * </blockquote>
 *
 * <pre>
 * http://www.lat-lon.de/transform&amp;service=WCTS&amp;request=DescribeCRS&amp;version=0.0.20
 *     &amp;CRSs=urn:ogc:def:crs:EPSG:6.3:4277
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DescribeCRSRequest extends RequestParameters {

    private List<String> crs;

    /**
     * <blockquote>
     * "Identifier of desired coordinate reference system" &mdash; one or more (mandatory), one
     * for each CRS desired.
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 28, §13.2.1</a>
     * </blockquote>
     *
     * CRS URI(s) to describe. (required)
     */
    public List<String> getCrs() {
        return crs;
    }

    /**
     * @param crs the CRS URI(s) to set
     * @see #getCrs()
     */
    public void setCrs(List<String> crs) {
        this.crs = crs;
    }

    /**
     * @param crs the CRS URI(s) to set
     * @see #getCrs()
     */
    public DescribeCRSRequest crs(List<String> crs) {
        setCrs(crs);
        return this;
    }
}
