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

import java.awt.Color;
import java.util.List;
import java.util.Map;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.wms.xml.WMSVersion;
import org.opengis.geometry.Envelope;

/**
 * To retrieve a rendered map image.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GetMap extends RequestParameters {

    private WMSVersion version;
    private List<String> layers;
    private List<String> styles;
    private Envelope bbox;
    private Integer width;
    private Integer height;
    private String format = "image/png";
    private Boolean transparent;
    private Color bgColor;
    private String exceptions;
    private String sld;
    private String sldBody;
    private String sldVersion;
    private Double elevation;
    private String time;
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
    public GetMap version(WMSVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * Layer(s) to display on the map. (required)
     *
     * @return the layers
     */
    public List<String> getLayers() {
        return layers;
    }

    /**
     * @param layers the layers to set
     * @see #getLayers()
     */
    public void setLayers(List<String> layers) {
        this.layers = layers;
    }

    /**
     * @param layers the layers to set
     * @see #getLayers()
     */
    public GetMap layers(List<String> layers) {
        setLayers(layers);
        return this;
    }

    /**
     * Rendering style(s) to apply, one per requested layer. Leave {@code null} or empty to use the
     * default style of each layer. (optional)
     *
     * @return the styles
     */
    public List<String> getStyles() {
        return styles;
    }

    /**
     * @param styles the styles to set
     * @see #getStyles()
     */
    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    /**
     * @param styles the styles to set
     * @see #getStyles()
     */
    public GetMap styles(List<String> styles) {
        setStyles(styles);
        return this;
    }

    /**
     * Bounding box of the requested map, carrying its coordinate reference system. Only its first two
     * dimensions are used, as {@code minx,miny,maxx,maxy}; elevation and time are set through
     * {@link #getElevation()} and {@link #getTime()}. The axis order must already match what is expected
     * by {@link #getVersion()} : longitude/easting first for versions prior to 1.3.0, or the
     * authority-defined axis order of the CRS for 1.3.0 (e.g. latitude before longitude for
     * {@code EPSG:4326}). (required)
     *
     * @return the bbox
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public GetMap bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * Width in pixels of the returned map. (required)
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
    public GetMap width(Integer width) {
        setWidth(width);
        return this;
    }

    /**
     * Height in pixels of the returned map. (required)
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
    public GetMap height(Integer height) {
        setHeight(height);
        return this;
    }

    /**
     * Output mime-type of the map. (optional, default to "image/png")
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
    public GetMap format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * Whether the background of the map should be transparent. (optional)
     *
     * @return the transparent
     */
    public Boolean getTransparent() {
        return transparent;
    }

    /**
     * @param transparent the transparent to set
     * @see #getTransparent()
     */
    public void setTransparent(Boolean transparent) {
        this.transparent = transparent;
    }

    /**
     * @param transparent the transparent to set
     * @see #getTransparent()
     */
    public GetMap transparent(Boolean transparent) {
        setTransparent(transparent);
        return this;
    }

    /**
     * Background color of the map. (optional)
     *
     * @return the bgColor
     */
    public Color getBgColor() {
        return bgColor;
    }

    /**
     * @param bgColor the bgColor to set
     * @see #getBgColor()
     */
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    /**
     * @param bgColor the bgColor to set
     * @see #getBgColor()
     */
    public GetMap bgColor(Color bgColor) {
        setBgColor(bgColor);
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
    public GetMap exceptions(String exceptions) {
        setExceptions(exceptions);
        return this;
    }

    /**
     * URL of a Styled Layer Descriptor document to apply. Mutually exclusive with {@link #getSldBody()}
     * and {@link #getStyles()}. (optional)
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
    public GetMap sld(String sld) {
        setSld(sld);
        return this;
    }

    /**
     * Inline Styled Layer Descriptor document to apply. Takes precedence over {@link #getSld()} and
     * {@link #getStyles()}. (optional)
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
    public GetMap sldBody(String sldBody) {
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
    public GetMap sldVersion(String sldVersion) {
        setSldVersion(sldVersion);
        return this;
    }

    /**
     * Elevation value to request on layers declaring a vertical dimension. (optional)
     *
     * @return the elevation
     */
    public Double getElevation() {
        return elevation;
    }

    /**
     * @param elevation the elevation to set
     * @see #getElevation()
     */
    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    /**
     * @param elevation the elevation to set
     * @see #getElevation()
     */
    public GetMap elevation(Double elevation) {
        setElevation(elevation);
        return this;
    }

    /**
     * Time value or period to request on layers declaring a temporal dimension. (optional)
     *
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     * @see #getTime()
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @param time the time to set
     * @see #getTime()
     */
    public GetMap time(String time) {
        setTime(time);
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
    public GetMap dimensions(Map<String, String> dimensions) {
        setDimensions(dimensions);
        return this;
    }

}
