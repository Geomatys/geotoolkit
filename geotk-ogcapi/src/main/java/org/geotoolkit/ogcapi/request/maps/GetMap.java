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
package org.geotoolkit.ogcapi.request.maps;

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * To retrieve a map.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetMap extends RequestParameters {

    private String collectionId;
    private String styleId;
    private Integer width;
    private Integer height;
    private Double scaleDenominator;
    private Double mmPerPixel;
    private Envelope bbox;
    private DirectPosition center;
    private List<String> subset;
    private String subsetCrs;
    private String bgcolor;
    private Boolean transparent;
    private String voidColor;
    private Boolean voidTransparent;
    private String datetime;
    private List<String> collections;
    private String crs;
    private Double orientation;
    private String crsProjMethod;
    private String crsProjCenter;
    private String crsProjParams;
    private String crsDatum;
    private String format;

    /**
     * Local identifier of a collection.
     *
     * Set to retrieve the map of a single collection ({@code /collections/{collectionId}/map}).
     * Leave {@code null} to retrieve a map of the whole dataset ({@code /map}). (optional)
     *
     * @return the collectionId
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public GetMap collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * An identifier representing a specific style.
     * Set to retrieve a styled map ({@code .../styles/{styleId}/map}).
     * Leave {@code null} to retrieve the default map. (optional)
     *
     * @return the styleId
     */
    public String getStyleId() {
        return styleId;
    }

    /**
     * @param styleId the styleId to set
     * @see #getStyleId()
     */
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    /**
     * @param styleId the styleId to set
     * @see #getStyleId()
     */
    public GetMap styleId(String styleId) {
        setStyleId(styleId);
        return this;
    }

    /**
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
     * Either a date-time or an interval, half-bounded or bounded. Date and time expressions adhere to
     * RFC 3339, section 5.6. Examples: a date-time: "2018-02-12T23:20:50Z"; a bounded interval:
     * "2018-02-12T00:00:00Z/2018-03-18T12:31:12Z"; half-bounded intervals: "2018-02-12T00:00:00Z/.." or
     * "../2018-03-18T12:31:12Z". (optional)
     *
     * @return the datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public GetMap datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * The collections that should be included in the response. If the parameter is missing, some or
     * all collections will be included. The collections are rendered in the order specified, with the
     * last one showing on top, unless the priority is overridden by styling rules. (optional)
     *
     * @return the collections
     */
    public List<String> getCollections() {
        return collections;
    }

    /**
     * @param collections the collections to set
     * @see #getCollections()
     */
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    /**
     * @param collections the collections to set
     * @see #getCollections()
     */
    public GetMap collections(List<String> collections) {
        setCollections(collections);
        return this;
    }

    /**
     * Retrieve only part of the data by slicing or trimming along one or more axis.
     * For trimming: {axisAbbrev}({low}:{high}) (preserves dimensionality).
     * For slicing: {axisAbbrev}({value}) (reduces dimensionality).
     * An asterisk ({@code *}) can be used instead of {low} or {high} to indicate the minimum/maximum value. (optional)
     *
     * @return the subset
     */
    public List<String> getSubset() {
        return subset;
    }

    /**
     * @param subset the subset to set
     * @see #getSubset()
     */
    public void setSubset(List<String> subset) {
        this.subset = subset;
    }

    /**
     * @param subset the subset to set
     * @see #getSubset()
     */
    public GetMap subset(List<String> subset) {
        setSubset(subset);
        return this;
    }

    /**
     * Crs for the specified subset. (optional)
     *
     * @return the subsetCrs
     */
    public String getSubsetCrs() {
        return subsetCrs;
    }

    /**
     * @param subsetCrs the subsetCrs to set
     * @see #getSubsetCrs()
     */
    public void setSubsetCrs(String subsetCrs) {
        this.subsetCrs = subsetCrs;
    }

    /**
     * @param subsetCrs the subsetCrs to set
     * @see #getSubsetCrs()
     */
    public GetMap subsetCrs(String subsetCrs) {
        setSubsetCrs(subsetCrs);
        return this;
    }

    /**
     * Reproject the output to the given crs. (optional)
     *
     * @return the crs
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the crs to set
     * @see #getCrs()
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * @param crs the crs to set
     * @see #getCrs()
     */
    public GetMap crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * Web color name or hexadecimal 0x[AA]RRGGBB color value for the background color (default to
     * 0x9C9C9C gray). If alpha is not specified, full opacity is assumed. (optional, default to
     * 0xFFFFFF)
     *
     * @return the bgcolor
     */
    public String getBgColor() {
        return bgcolor;
    }

    /**
     * @param bgcolor the bgcolor to set
     * @see #getBgColor()
     */
    public void setBgColor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    /**
     * @param bgcolor the bgcolor to set
     * @see #getBgColor()
     */
    public GetMap bgColor(String bgcolor) {
        setBgColor(bgcolor);
        return this;
    }

    /**
     * Background transparency of the map (defaults to {@code true} without a {@code bgcolor}
     * specified, but to {@code false} when a {@code bgcolor} is used). (optional, default to true)
     *
     * @return the transparency
     */
    public Boolean getTransparency() {
        return transparent;
    }

    /**
     * @param transparent the transparency to set
     * @see #getTransparency()
     */
    public void setTransparency(Boolean transparent) {
        this.transparent = transparent;
    }

    /**
     * @param transparent the transparency to set
     * @see #getTransparency()
     */
    public GetMap transparency(Boolean transparent) {
        setTransparency(transparent);
        return this;
    }

    /**
     * Web color name or hexadecimal 0x[AA]RRGGBB color value for the void color, for parts of the map
     * outside of the valid area of the projection / CRS. If not specified, defaults to the same as
     * {@code bgcolor}. (optional)
     *
     * @return the voidColor
     */
    public String getVoidColor() {
        return voidColor;
    }

    /**
     * @param voidColor the voidColor to set
     * @see #getVoidColor()
     */
    public void setVoidColor(String voidColor) {
        this.voidColor = voidColor;
    }

    /**
     * @param voidColor the voidColor to set
     * @see #getVoidColor()
     */
    public GetMap voidColor(String voidColor) {
        setVoidColor(voidColor);
        return this;
    }

    /**
     * Background transparency for parts of the map outside of the valid areas of the CRS / projection
     * (defaults to the same as {@code transparent} if not specified). (optional)
     *
     * @return the voidTransparent
     */
    public Boolean getVoidTransparent() {
        return voidTransparent;
    }

    /**
     * @param voidTransparent the voidTransparent to set
     * @see #getVoidTransparent()
     */
    public void setVoidTransparent(Boolean voidTransparent) {
        this.voidTransparent = voidTransparent;
    }

    /**
     * @param voidTransparent the voidTransparent to set
     * @see #getVoidTransparent()
     */
    public GetMap voidTransparent(Boolean voidTransparent) {
        setVoidTransparent(voidTransparent);
        return this;
    }

    /**
     * Width of the map in pixels. If omitted and {@code height} is specified, defaults to the width
     * maintaining a 1:1 aspect ratio. If both are omitted, the server selects default dimensions. When
     * used together with {@code center} and/or {@code scale-denominator}, this takes on a subsetting
     * role rather than scaling. (optional)
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
     * Height of the map in pixels. If omitted and {@code width} is specified, defaults to the height
     * maintaining a 1:1 aspect ratio. If both are omitted, the server selects default dimensions. When
     * used together with {@code center} and/or {@code scale-denominator}, this takes on a subsetting
     * role rather than scaling. (optional)
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
     * Coordinates of the center point for subsetting, in conjunction with the {@code width} and/or
     * {@code height} parameters, taking into consideration the scale and display resolution of the
     * map. If the position carries a coordinate reference system other than [ogc:CRS84], it is sent as
     * the {@code center-crs} parameter. (optional)
     *
     * @return the center
     */
    public DirectPosition getCenter() {
        return center;
    }

    /**
     * @param center the center to set
     * @see #getCenter()
     */
    public void setCenter(DirectPosition center) {
        this.center = center;
    }

    /**
     * @param center the center to set
     * @see #getCenter()
     */
    public GetMap center(DirectPosition center) {
        setCenter(center);
        return this;
    }

    /**
     * Scale denominator of the map, specifying to how many units in the real world one of the same
     * unit on the map corresponds, taking into account the display resolution ({@code mm-per-pixel} or
     * 0.28 mm/pixel default). If omitted, the scale is implied from the dimensions of the returned map
     * compared to its spatial subset area. (optional)
     *
     * @return the scaleDenominator
     */
    public Double getScaleDenominator() {
        return scaleDenominator;
    }

    /**
     * @param scaleDenominator the scaleDenominator to set
     * @see #getScaleDenominator()
     */
    public void setScaleDenominator(Double scaleDenominator) {
        this.scaleDenominator = scaleDenominator;
    }

    /**
     * @param scaleDenominator the scaleDenominator to set
     * @see #getScaleDenominator()
     */
    public GetMap scaleDenominator(Double scaleDenominator) {
        setScaleDenominator(scaleDenominator);
        return this;
    }

    /**
     * Display resolution of the target rendering device in millimeters per pixel. Taken into account
     * for symbology rules, the {@code scale-denominator} parameter, and spatial subsetting using
     * {@code center}, {@code width} and {@code height}. (optional, default to 0.28)
     *
     * @return the mmPerPixel
     */
    public Double getMmPerPixel() {
        return mmPerPixel;
    }

    /**
     * @param mmPerPixel the mmPerPixel to set
     * @see #getMmPerPixel()
     */
    public void setMmPerPixel(Double mmPerPixel) {
        this.mmPerPixel = mmPerPixel;
    }

    /**
     * @param mmPerPixel the mmPerPixel to set
     * @see #getMmPerPixel()
     */
    public GetMap mmPerPixel(Double mmPerPixel) {
        setMmPerPixel(mmPerPixel);
        return this;
    }

    /**
     * Orientation in degrees by which the content of the map is to be rotated around the center of the
     * subset area as pivot point in a counter-clockwise direction, resulting in the viewing perspective
     * being rotated by the same orientation in a clockwise direction. (optional, default to 0)
     *
     * @return the orientation
     */
    public Double getOrientation() {
        return orientation;
    }

    /**
     * @param orientation the orientation to set
     * @see #getOrientation()
     */
    public void setOrientation(Double orientation) {
        this.orientation = orientation;
    }

    /**
     * @param orientation the orientation to set
     * @see #getOrientation()
     */
    public GetMap orientation(Double orientation) {
        setOrientation(orientation);
        return this;
    }

    /**
     * Projection operation method for a custom projection CRS, as listed by the
     * {@code /projectionsAndDatums} resource. (optional)
     *
     * @return the crsProjMethod
     */
    public String getCrsProjMethod() {
        return crsProjMethod;
    }

    /**
     * @param crsProjMethod the crsProjMethod to set
     * @see #getCrsProjMethod()
     */
    public void setCrsProjMethod(String crsProjMethod) {
        this.crsProjMethod = crsProjMethod;
    }

    /**
     * @param crsProjMethod the crsProjMethod to set
     * @see #getCrsProjMethod()
     */
    public GetMap crsProjMethod(String crsProjMethod) {
        setCrsProjMethod(crsProjMethod);
        return this;
    }

    /**
     * Projection center for a custom projection CRS, of the literal form
     * {@code Lat(centerLat),Lon(centerLon)}, mapped to the relevant operation method parameters for
     * latitude and longitude. (optional)
     *
     * @return the crsProjCenter
     */
    public String getCrsProjCenter() {
        return crsProjCenter;
    }

    /**
     * @param crsProjCenter the crsProjCenter to set
     * @see #getCrsProjCenter()
     */
    public void setCrsProjCenter(String crsProjCenter) {
        this.crsProjCenter = crsProjCenter;
    }

    /**
     * @param crsProjCenter the crsProjCenter to set
     * @see #getCrsProjCenter()
     */
    public GetMap crsProjCenter(String crsProjCenter) {
        setCrsProjCenter(crsProjCenter);
        return this;
    }

    /**
     * Operation method parameters for a custom projection CRS, as listed for the selected
     * {@code crs-proj-method} by the {@code /projectionsAndDatums} resource. (optional)
     *
     * @return the crsProjParams
     */
    public String getCrsProjParams() {
        return crsProjParams;
    }

    /**
     * @param crsProjParams the crsProjParams to set
     * @see #getCrsProjParams()
     */
    public void setCrsProjParams(String crsProjParams) {
        this.crsProjParams = crsProjParams;
    }

    /**
     * @param crsProjParams the crsProjParams to set
     * @see #getCrsProjParams()
     */
    public GetMap crsProjParams(String crsProjParams) {
        setCrsProjParams(crsProjParams);
        return this;
    }

    /**
     * Datum for a custom projection CRS, implying a particular ellipsoid, as listed by the
     * {@code /projectionsAndDatums} resource. (optional)
     *
     * @return the crsDatum
     */
    public String getCrsDatum() {
        return crsDatum;
    }

    /**
     * @param crsDatum the crsDatum to set
     * @see #getCrsDatum()
     */
    public void setCrsDatum(String crsDatum) {
        this.crsDatum = crsDatum;
    }

    /**
     * @param crsDatum the crsDatum to set
     * @see #getCrsDatum()
     */
    public GetMap crsDatum(String crsDatum) {
        setCrsDatum(crsDatum);
        return this;
    }

    /**
     * The format of the map response. Accepted values are 'png', 'jpg' or 'tiff' (GeoTIFF). (optional)
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

}
