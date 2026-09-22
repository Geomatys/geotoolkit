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
import org.geotoolkit.wcts.xml.v030.CoverageTypeType;
import org.geotoolkit.wcts.xml.v030.GeometryTypeType;
import org.geotoolkit.wcts.xml.v030.InterpolationMethodType;

/**
 * To check whether a WCTS can transform between two CRSs, or perform a named transformation or
 * method, for given geometry/coverage types.
 *
 * <blockquote>
 * "The IsTransformable operation allows WCTS clients to check if coordinate transformation of
 * a specified set of geometry primitives or coverage types is possible between two coordinate
 * reference systems. This check will succeed only if the identified types are supported, and
 * if there is a valid sequence of one or more known transformations to transform the
 * coordinates from the source CRS to the target CRS. This check will NOT evaluate if this
 * transformation makes any sense, nor if it is possible to be performed for each specific
 * coordinate expression that may be submitted by a Transform request."
 * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, §10.1</a>
 * </blockquote>
 *
 * <ul>
 *   <li>Exactly one of {@link #getSourceCRS()}/{@link #getTargetCRS()}, {@link #getTransformation()}
 *       or {@link #getMethod()} must be set.</li>
 *   <li>At least one of {@link #getGeometryType()} or {@link #getCoverageType()} must be set.</li>
 * </ul>
 *
 * <pre>
 * &lt;IsTransformable xmlns="http://www.opengeospatial.net/wcts" service="WCTS" version="0.0.0"&gt;
 *   &lt;SourceCRS&gt;urn:ogc:def:crs:EPSG:6.0:4326&lt;/SourceCRS&gt;
 *   &lt;TargetCRS&gt;urn:ogc:def:crs:EPSG:6.0:23032&lt;/TargetCRS&gt;
 *   &lt;GeometryType&gt;LineString&lt;/GeometryType&gt;
 * &lt;/IsTransformable&gt;
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class IsTransformableRequest extends RequestParameters {

    private String sourceCRS;
    private String targetCRS;
    private String transformation;
    private String method;
    private List<GeometryTypeType> geometryType;
    private List<CoverageTypeType> coverageType;
    private List<InterpolationMethodType> interpolationMethod;

    /**
     * <blockquote>
     * "Included when the server is permitted to select any coordinate operation which it knows
     * about that can transform coordinates from the identified sourceCRS to the targetCRS
     * identified."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 18 note a, §10.2.1</a>
     * </blockquote>
     *
     * Source CRS URI. (mutually exclusive with {@link #getTransformation()} and {@link #getMethod()})
     *
     * @see #getTargetCRS()
     * @see #getTransformation()
     * @see #getMethod()
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
    public IsTransformableRequest sourceCRS(String sourceCRS) {
        setSourceCRS(sourceCRS);
        return this;
    }

    /**
     * <blockquote>
     * "Included when the server is permitted to select any coordinate operation which it knows
     * about that can transform coordinates from the identified sourceCRS to the targetCRS
     * identified."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 18 note a, §10.2.1</a>
     * </blockquote>
     *
     * Target CRS URI. (mutually exclusive with {@link #getTransformation()} and {@link #getMethod()})
     *
     * @see #getSourceCRS()
     * @see #getTransformation()
     * @see #getMethod()
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
    public IsTransformableRequest targetCRS(String targetCRS) {
        setTargetCRS(targetCRS);
        return this;
    }

    /**
     * <blockquote>
     * "Included when client is specifying a specific well-known coordinate operation, whose
     * definition is known to the WCTS server."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 18 note b, §10.2.1</a>
     * </blockquote>
     *
     * Identification of the desired coordinate operation. (mutually exclusive with
     * {@link #getSourceCRS()}/{@link #getTargetCRS()} and {@link #getMethod()})
     *
     * @see #getSourceCRS()
     * @see #getMethod()
     */
    public String getTransformation() {
        return transformation;
    }

    /**
     * @param transformation the transformation to set
     * @see #getTransformation()
     */
    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    /**
     * @param transformation the transformation to set
     * @see #getTransformation()
     */
    public IsTransformableRequest transformation(String transformation) {
        setTransformation(transformation);
        return this;
    }

    /**
     * <blockquote>
     * "Included when client is considering specifying a specific user-defined coordinate
     * operation that uses a well-known operation method."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 18 note c, §10.2.1</a>
     * </blockquote>
     *
     * Identification of the desired operation method. (mutually exclusive with
     * {@link #getSourceCRS()}/{@link #getTargetCRS()} and {@link #getTransformation()})
     *
     * @see #getSourceCRS()
     * @see #getTransformation()
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     * @see #getMethod()
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @param method the method to set
     * @see #getMethod()
     */
    public IsTransformableRequest method(String method) {
        setMethod(method);
        return this;
    }

    /**
     * <blockquote>
     * "Identifier of type of GML 3 geometric primitive that will be requested to be
     * transformed" &mdash; included for transforming specific geometric primitive types.
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 17, §10.2.1</a>
     * </blockquote>
     *
     * <p>Either at least one geometry type or at least one {@link #getCoverageType() coverage
     * type} shall be included.</p>
     *
     * @see #getCoverageType()
     */
    public List<GeometryTypeType> getGeometryType() {
        return geometryType;
    }

    /**
     * @param geometryType the geometry types to set
     * @see #getGeometryType()
     */
    public void setGeometryType(List<GeometryTypeType> geometryType) {
        this.geometryType = geometryType;
    }

    /**
     * @param geometryType the geometry types to set
     * @see #getGeometryType()
     */
    public IsTransformableRequest geometryType(List<GeometryTypeType> geometryType) {
        setGeometryType(geometryType);
        return this;
    }

    /**
     * <blockquote>
     * "Identifier of coverage type that will be requested to be transformed" &mdash; included
     * for transforming coverages.
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 17, §10.2.1</a>
     * </blockquote>
     *
     * <p>Either at least one coverage type or at least one {@link #getGeometryType() geometry
     * type} shall be included.</p>
     *
     * @see #getGeometryType()
     * @see #getInterpolationMethod()
     */
    public List<CoverageTypeType> getCoverageType() {
        return coverageType;
    }

    /**
     * @param coverageType the coverage types to set
     * @see #getCoverageType()
     */
    public void setCoverageType(List<CoverageTypeType> coverageType) {
        this.coverageType = coverageType;
    }

    /**
     * @param coverageType the coverage types to set
     * @see #getCoverageType()
     */
    public IsTransformableRequest coverageType(List<CoverageTypeType> coverageType) {
        setCoverageType(coverageType);
        return this;
    }

    /**
     * <blockquote>
     * "Identifier of interpolation method which should be used to transform coverage"
     * &mdash; one or more interpolation methods shall be included only if one or more coverage
     * types are included.
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 17 note c, §10.2.1</a>
     * </blockquote>
     *
     * @see #getCoverageType()
     */
    public List<InterpolationMethodType> getInterpolationMethod() {
        return interpolationMethod;
    }

    /**
     * @param interpolationMethod the interpolation methods to set
     * @see #getInterpolationMethod()
     */
    public void setInterpolationMethod(List<InterpolationMethodType> interpolationMethod) {
        this.interpolationMethod = interpolationMethod;
    }

    /**
     * @param interpolationMethod the interpolation methods to set
     * @see #getInterpolationMethod()
     */
    public IsTransformableRequest interpolationMethod(List<InterpolationMethodType> interpolationMethod) {
        setInterpolationMethod(interpolationMethod);
        return this;
    }
}
