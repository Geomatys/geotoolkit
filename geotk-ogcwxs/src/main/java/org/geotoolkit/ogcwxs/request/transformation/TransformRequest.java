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
import org.geotoolkit.wcts.xml.v030.InterpolationMethodType;

/**
 * To transform input data from a source CRS to a target CRS, or through a named transformation.
 *
 * <blockquote>
 * "The Transform operation allows WCTS clients to request transformation of coordinates from
 * one coordinate reference system (CRS) into another. Input data is provided either inline in
 * the operation request as a GML Feature or as a reference to a remote resource or local
 * payload (attachment to the message), in a supported format. The remote resource is
 * referenced using a URL, which could be a request to a specific server providing the data.
 * This server may be a WFS or WCS, for example. The WCTS may optionally support user-defined
 * coordinate transformations, in which case it is necessary for the client to provide the
 * parameters for the transformation."
 * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, §9.1</a>
 * </blockquote>
 *
 * <ul>
 *   <li>Either {@link #getSourceCRS()}/{@link #getTargetCRS()} or {@link #getTransformation()}
 *       must be set, but not both.</li>
 *   <li>{@link #getInputData()} is mandatory.</li>
 * </ul>
 *
 * <pre>
 * &lt;Transform xmlns="http://www.opengeospatial.net/wcts" service="WCTS" version="0.0.0"&gt;
 *   &lt;SourceCRS&gt;urn:ogc:def:crs:EPSG:6.0:4326&lt;/SourceCRS&gt;
 *   &lt;TargetCRS&gt;urn:ogc:def:crs:EPSG:6.0:32611&lt;/TargetCRS&gt;
 *   &lt;InputData&gt;
 *     &lt;gml:FeatureCollection&gt;...&lt;/gml:FeatureCollection&gt;
 *   &lt;/InputData&gt;
 *   &lt;OutputFormat&gt;text/xml; gmlVersion=3.1.0&lt;/OutputFormat&gt;
 * &lt;/Transform&gt;
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class TransformRequest extends RequestParameters {

    private String sourceCRS;
    private String targetCRS;
    private String transformation;
    private List<String> inputData;
    private InterpolationMethodType interpolationMethod;
    private String outputFormat;
    private Boolean store;

    /**
     * <blockquote>
     * "Included when the server is permitted to select any coordinate operation which it knows
     * about that can transform coordinates from the identified sourceCRS to the targetCRS."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 12 note a, §9.2.1</a>
     * </blockquote>
     *
     * Source CRS URI. (required unless {@link #getTransformation()} is set)
     *
     * @see #getTargetCRS()
     * @see #getTransformation()
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
    public TransformRequest sourceCRS(String sourceCRS) {
        setSourceCRS(sourceCRS);
        return this;
    }

    /**
     * <blockquote>
     * "Included when the server is permitted to select any coordinate operation which it knows
     * about that can transform coordinates from the identified sourceCRS to the targetCRS."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 12 note a, §9.2.1</a>
     * </blockquote>
     *
     * Target CRS URI. (required unless {@link #getTransformation()} is set)
     *
     * @see #getSourceCRS()
     * @see #getTransformation()
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
    public TransformRequest targetCRS(String targetCRS) {
        setTargetCRS(targetCRS);
        return this;
    }

    /**
     * <blockquote>
     * "Included when client is specifying a specific coordinate operation, possibly a
     * user-defined coordinate operation. In this use, this element shall either: reference a
     * well-known coordinate operation, whose definition is known to the WCTS server; contain a
     * URL from which that definition object can be retrieved, using GML encoding; or contain
     * the coordinate operation definition object, using GML encoding, and a xlink:href value
     * containing the URN that references this definition."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 12 note b, §9.2.1</a>
     * </blockquote>
     *
     * Named transformation URI, used instead of {@link #getSourceCRS()}/{@link #getTargetCRS()}.
     *
     * @see #getSourceCRS()
     * @see #getTargetCRS()
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
    public TransformRequest transformation(String transformation) {
        setTransformation(transformation);
        return this;
    }

    /**
     * <blockquote>
     * "Data to be transformed" (mandatory, one or more). "The InputData can be either an inline
     * gml:Feature or a 'Reference' to geospatial data supported by the WCTS. The reference is
     * used to address a local payload or a remote resource."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 12/13, §9.2.1</a>
     * </blockquote>
     *
     * References to the input data to transform. (required)
     */
    public List<String> getInputData() {
        return inputData;
    }

    /**
     * @param inputData the input data references to set
     * @see #getInputData()
     */
    public void setInputData(List<String> inputData) {
        this.inputData = inputData;
    }

    /**
     * @param inputData the input data references to set
     * @see #getInputData()
     */
    public TransformRequest inputData(List<String> inputData) {
        setInputData(inputData);
        return this;
    }

    /**
     * <blockquote>
     * "Identifier of interpolation method which should be used to transform coverage" &mdash;
     * included when transforming a coverage using interpolation.
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 12, §9.2.1</a>
     * </blockquote>
     */
    public InterpolationMethodType getInterpolationMethod() {
        return interpolationMethod;
    }

    /**
     * @param interpolationMethod the interpolation method to set
     * @see #getInterpolationMethod()
     */
    public void setInterpolationMethod(InterpolationMethodType interpolationMethod) {
        this.interpolationMethod = interpolationMethod;
    }

    /**
     * @param interpolationMethod the interpolation method to set
     * @see #getInterpolationMethod()
     */
    public TransformRequest interpolationMethod(InterpolationMethodType interpolationMethod) {
        setInterpolationMethod(interpolationMethod);
        return this;
    }

    /**
     * <blockquote>
     * "Identifier of output format to be used for the transformed features or coverage"
     * &mdash; included when output data is not to be returned in the operation response.
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 12, §9.2.1</a>
     * </blockquote>
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat the output format to set
     * @see #getOutputFormat()
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @param outputFormat the output format to set
     * @see #getOutputFormat()
     */
    public TransformRequest outputFormat(String outputFormat) {
        setOutputFormat(outputFormat);
        return this;
    }

    /**
     * <blockquote>
     * "Specifies if transformed data to be stored as remote resource or returned directly in
     * response message" (Boolean, default is {@code false}: return directly in response).
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, Table 12, §9.2.1</a>
     * </blockquote>
     *
     * Whether the server should store the result for later retrieval. Defaults to {@code false}
     * server-side if left {@code null}.
     */
    public Boolean getStore() {
        return store;
    }

    /**
     * @param store whether to store the result to set
     * @see #getStore()
     */
    public void setStore(Boolean store) {
        this.store = store;
    }

    /**
     * @param store whether to store the result to set
     * @see #getStore()
     */
    public TransformRequest store(Boolean store) {
        setStore(store);
        return this;
    }
}
