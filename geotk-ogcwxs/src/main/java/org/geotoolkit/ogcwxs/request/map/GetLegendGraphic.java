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
package org.geotoolkit.ogcwxs.request.map;

import java.util.Map;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.wms.xml.WMSVersion;

/**
 * To retrieve a legend graphic image for a layer.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetLegendGraphic extends RequestParameters {

    private WMSVersion version;
    private String layer;
    private String style;
    private String format = "image/png";
    private Integer width;
    private Integer height;
    private String sld;
    private String sldBody;
    private String sldVersion;
    private String rule;
    private Double scale;
    private String exceptions;
    private Map<String, String> dimensions;

    /**
     * The WMS version to request. Defaults to {@link WMSVersion#v130} if left {@code null}.
     *
     * @return the version
     */
    public WMSVersion getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public void setVersion(WMSVersion version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public GetLegendGraphic version(WMSVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * Layer for which a legend is requested. (required)
     *
     * @return the layer
     */
    public String getLayer() {
        return layer;
    }

    /**
     * @param layer the layer to set
     * @see #getLayer()
     */
    public void setLayer(String layer) {
        this.layer = layer;
    }

    /**
     * @param layer the layer to set
     * @see #getLayer()
     */
    public GetLegendGraphic layer(String layer) {
        setLayer(layer);
        return this;
    }

    /**
     * Style of the layer for which a legend is requested. Leave {@code null} to use the default style.
     * (optional)
     *
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     * @see #getStyle()
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * @param style the style to set
     * @see #getStyle()
     */
    public GetLegendGraphic style(String style) {
        setStyle(style);
        return this;
    }

    /**
     * Output mime-type of the legend graphic. (optional, default to "image/png")
     *
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     * @see #getFormat()
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @param format the format to set
     * @see #getFormat()
     */
    public GetLegendGraphic format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * Width in pixels of a legend symbol. (optional)
     *
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     * @see #getWidth()
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @param width the width to set
     * @see #getWidth()
     */
    public GetLegendGraphic width(Integer width) {
        setWidth(width);
        return this;
    }

    /**
     * Height in pixels of a legend symbol. (optional)
     *
     * @return the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     * @see #getHeight()
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * @param height the height to set
     * @see #getHeight()
     */
    public GetLegendGraphic height(Integer height) {
        setHeight(height);
        return this;
    }

    /**
     * URL of a Styled Layer Descriptor document to apply. Mutually exclusive with {@link #getSldBody()}
     * and {@link #getStyle()}. (optional)
     *
     * @return the sld
     */
    public String getSld() {
        return sld;
    }

    /**
     * @param sld the sld to set
     * @see #getSld()
     */
    public void setSld(String sld) {
        this.sld = sld;
    }

    /**
     * @param sld the sld to set
     * @see #getSld()
     */
    public GetLegendGraphic sld(String sld) {
        setSld(sld);
        return this;
    }

    /**
     * Inline Styled Layer Descriptor document to apply. Takes precedence over {@link #getSld()} and
     * {@link #getStyle()}. (optional)
     *
     * @return the sldBody
     */
    public String getSldBody() {
        return sldBody;
    }

    /**
     * @param sldBody the sldBody to set
     * @see #getSldBody()
     */
    public void setSldBody(String sldBody) {
        this.sldBody = sldBody;
    }

    /**
     * @param sldBody the sldBody to set
     * @see #getSldBody()
     */
    public GetLegendGraphic sldBody(String sldBody) {
        setSldBody(sldBody);
        return this;
    }

    /**
     * Version of the SLD document referenced by {@link #getSld()} or {@link #getSldBody()}. (optional)
     *
     * @return the sldVersion
     */
    public String getSldVersion() {
        return sldVersion;
    }

    /**
     * @param sldVersion the sldVersion to set
     * @see #getSldVersion()
     */
    public void setSldVersion(String sldVersion) {
        this.sldVersion = sldVersion;
    }

    /**
     * @param sldVersion the sldVersion to set
     * @see #getSldVersion()
     */
    public GetLegendGraphic sldVersion(String sldVersion) {
        setSldVersion(sldVersion);
        return this;
    }

    /**
     * Name of a single rule of the style to draw in the legend. (optional)
     *
     * @return the rule
     */
    public String getRule() {
        return rule;
    }

    /**
     * @param rule the rule to set
     * @see #getRule()
     */
    public void setRule(String rule) {
        this.rule = rule;
    }

    /**
     * @param rule the rule to set
     * @see #getRule()
     */
    public GetLegendGraphic rule(String rule) {
        setRule(rule);
        return this;
    }

    /**
     * Scale denominator for which the legend should be representative, used to select the matching
     * rule(s) of scale-dependent styles. (optional)
     *
     * @return the scale
     */
    public Double getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     * @see #getScale()
     */
    public void setScale(Double scale) {
        this.scale = scale;
    }

    /**
     * @param scale the scale to set
     * @see #getScale()
     */
    public GetLegendGraphic scale(Double scale) {
        setScale(scale);
        return this;
    }

    /**
     * Format in which to report exceptions. Its accepted values depend on the requested
     * {@link #getVersion()} : short tokens such as {@code "XML"}, {@code "INIMAGE"} or {@code "BLANK"} for
     * 1.3.0, versus mime-types such as {@code "application/vnd.ogc.se_xml"} for earlier versions.
     * (optional)
     *
     * @return the exceptions
     */
    public String getExceptions() {
        return exceptions;
    }

    /**
     * @param exceptions the exceptions to set
     * @see #getExceptions()
     */
    public void setExceptions(String exceptions) {
        this.exceptions = exceptions;
    }

    /**
     * @param exceptions the exceptions to set
     * @see #getExceptions()
     */
    public GetLegendGraphic exceptions(String exceptions) {
        setExceptions(exceptions);
        return this;
    }

    /**
     * Additional vendor-specific dimensions to request, as {@code DIM_xxx} parameter name (without the
     * {@code DIM_} prefix) to value. (optional)
     *
     * @return the dimensions
     */
    public Map<String, String> getDimensions() {
        return dimensions;
    }

    /**
     * @param dimensions the dimensions to set
     * @see #getDimensions()
     */
    public void setDimensions(Map<String, String> dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * @param dimensions the dimensions to set
     * @see #getDimensions()
     */
    public GetLegendGraphic dimensions(Map<String, String> dimensions) {
        setDimensions(dimensions);
        return this;
    }
}
