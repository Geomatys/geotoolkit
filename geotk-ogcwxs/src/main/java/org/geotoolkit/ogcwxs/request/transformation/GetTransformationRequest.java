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

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve the transformation(s) a WCTS would use between a source and a target CRS.
 *
 * <blockquote>
 * "The GetTransformation operation allows clients to retrieve the definition of the
 * transformation(s) from one coordinate reference system into another. The definition(s) in
 * the operation response are encoded in XML using GML 3.1.1. Implementation of the
 * GetTransformation operation is optional for a web coordinate transformation server."
 * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, §11.1</a>
 * </blockquote>
 *
 * <pre>
 * www.lat-lon.de/transform&amp;service=WCTS&amp;request=GetTransformation&amp;version=0.0.0
 *     &amp;sourceCRS=urn:ogc:def:crs:EPSG:6.3:4277&amp;targetCRS=urn:ogc:def:crs:EPSG:6.3:2770
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetTransformationRequest extends RequestParameters {

    private String sourceCRS;
    private String targetCRS;

    /**
     * <blockquote>
     * "Identifiers of SourceCRS and desired TargetCRS."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 22, §11.2.1</a>
     * </blockquote>
     *
     * Source CRS URI. (required)
     *
     * @see #getTargetCRS()
     */
    public String getSourceCRS() {
        return sourceCRS;
    }

    /**
     * @param sourceCRS the source CRS to set
     * @see #getSourceCRS()
     */
    public void setSourceCRS(String sourceCRS) {
        this.sourceCRS = sourceCRS;
    }

    /**
     * @param sourceCRS the source CRS to set
     * @see #getSourceCRS()
     */
    public GetTransformationRequest sourceCRS(String sourceCRS) {
        setSourceCRS(sourceCRS);
        return this;
    }

    /**
     * <blockquote>
     * "Identifiers of SourceCRS and desired TargetCRS."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 22, §11.2.1</a>
     * </blockquote>
     *
     * Target CRS URI. (required)
     *
     * @see #getSourceCRS()
     */
    public String getTargetCRS() {
        return targetCRS;
    }

    /**
     * @param targetCRS the target CRS to set
     * @see #getTargetCRS()
     */
    public void setTargetCRS(String targetCRS) {
        this.targetCRS = targetCRS;
    }

    /**
     * @param targetCRS the target CRS to set
     * @see #getTargetCRS()
     */
    public GetTransformationRequest targetCRS(String targetCRS) {
        setTargetCRS(targetCRS);
        return this;
    }
}
