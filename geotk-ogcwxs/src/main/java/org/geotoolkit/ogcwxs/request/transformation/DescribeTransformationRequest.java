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
 * To retrieve a description of one or more named transformations known by a WCTS.
 *
 * <blockquote>
 * "The DescribeTransformation operation allows WCTS clients to retrieve the definitions of
 * one or more identified coordinate operations. The definitions in the operation response are
 * encoded in XML using GML 3.1.1. Implementation of the DescribeTransformation operation is
 * optional for a web coordinate transformation server."
 * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, §12.1</a>
 * </blockquote>
 *
 * <pre>
 * www.lat-lon.de/transform&amp;service=WCTS&amp;request=DescribeTransformation&amp;version=0.0.20
 *     &amp;transformation=urn:ogc:def:coordinateOperation:EPSG:6.3:19916
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DescribeTransformationRequest extends RequestParameters {

    private List<String> transformation;

    /**
     * <blockquote>
     * "Identifier of desired coordinate operation" &mdash; one or more (mandatory), one for each
     * coordinate operation desired.
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 25, §12.2.1</a>
     * </blockquote>
     *
     * Transformation URI(s) to describe. (required)
     */
    public List<String> getTransformation() {
        return transformation;
    }

    /**
     * @param transformation the transformation URI(s) to set
     * @see #getTransformation()
     */
    public void setTransformation(List<String> transformation) {
        this.transformation = transformation;
    }

    /**
     * @param transformation the transformation URI(s) to set
     * @see #getTransformation()
     */
    public DescribeTransformationRequest transformation(List<String> transformation) {
        setTransformation(transformation);
        return this;
    }
}
