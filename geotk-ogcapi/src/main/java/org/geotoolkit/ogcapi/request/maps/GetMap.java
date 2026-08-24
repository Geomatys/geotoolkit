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
 * <blockquote><q>
 *     To keep the core of OGC API — Maps simple, the core only includes a mechanism to retrieve a
 *     map of the size and for the spatiotemporal extent that the server considers optimal.
 *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_9cc2269c-941a-4133-a5eb-e99252b2f62f"> OGC API - Maps - Part 1: Core - 7.2. Map resource</a></small></i>
 * </blockquote>
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
     * @return the collectionId
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     Every OGC API collection available as a map SHALL support an HTTP GET operation to a
     *     URL /collections/{collectionId}/map to retrieve a map from that collection resource.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_e9cbf542-2d25-4f55-b35d-544eff756d95"> OGC API - Maps - Part 1: Core - 19.4. Geospatial Data Resource Map</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Set to retrieve the map of a single collection ({@code /collections/{collectionId}/map}).</li>
     *     <li>Leave {@code null} to retrieve a map of the whole dataset ({@code /map}).</li>
     * </ul>
     *
     * @param collectionId the collectionId to set
     */
    public GetMap collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @return the styleId
     * @see #styleId(java.lang.String)
     */
    public String getStyleId() {
        return styleId;
    }

    /**
     * @param styleId the styleId to set
     * @see #styleId(java.lang.String)
     */
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    /**
     * <blockquote><q>
     *     Every resource for which a styled map is available SHALL support an HTTP GET operation
     *     to a …​/styles/{styleId}/map URL to retrieve a map for a particular style.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_e3ffb8a4-1b3c-41aa-9534-8a9795481bf7"> OGC API - Maps - Part 1: Core - 21.3. Styled Resource Map</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Set to retrieve a styled map ({@code .../styles/{styleId}/map}).</li>
     *     <li>Leave {@code null} to retrieve the default map.</li>
     * </ul>
     *
     * @param styleId the styleId to set
     */
    public GetMap styleId(String styleId) {
        setStyleId(styleId);
        return this;
    }

    /**
     * @return the bbox
     * @see #bbox(org.opengis.geometry.Envelope)
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #bbox(org.opengis.geometry.Envelope)
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * <blockquote><q>
     *     The {@code bbox} parameter defines how to subset a map using a bounding box (in
     *     {@code bbox-crs} coordinates).
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_02a878e9-746a-4800-9531-2f930bb367ce"> OGC API - Maps - Part 1: Core - 13.3.1. Parameter bbox</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Four or six coordinates: lower left corner, upper right corner (and optionally a min/max
     *     value on a third axis).</li>
     *     <li>Axis order and CRS are given by {@link #subsetCrs(java.lang.String)} (defaults to
     *     {@code [OGC:CRS84]}).</li>
     *     <li>A 4xx error is returned if used together with {@link #center(org.opengis.geometry.DirectPosition)}
     *     and/or {@link #subset(java.util.List)} on the same dimensions.</li>
     * </ul>
     * <pre>
     * bbox=-180,-90,180,90&amp;bbox-crs=[OGC:CRS84]
     * </pre>
     *
     * @param bbox the bbox to set
     */
    public GetMap bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * @return the datetime
     * @see #datetime(java.lang.String)
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #datetime(java.lang.String)
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * <blockquote><q>
     *     In the context of this requirements class, the datetime parameter selects a time instant or
     *     interval of the resource for which a map is requested.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_55350181-5f23-4990-be88-b29bfe35dd15"> OGC API - Maps - Part 1: Core - 14.3. datetime query parameter request and response</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Either a date-time or a bounded/half-bounded interval, per RFC 3339, section 5.6, with
     *     the {@code Z} UTC notation.</li>
     *     <li>Unbounded start/end use a double-dot ({@code ..}) or an empty string.</li>
     * </ul>
     * <pre>
     * datetime=2018-02-12T23:20:52Z
     * datetime=2018-02-12T00:00:00Z/2018-03-18T12:31:12Z
     * datetime=2018-02-12T00:00:00Z/..
     * datetime=../2018-03-18T12:31:12Z
     * </pre>
     *
     * @param datetime the datetime to set
     */
    public GetMap datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @return the collections
     * @see #collections(java.util.List)
     */
    public List<String> getCollections() {
        return collections;
    }

    /**
     * @param collections the collections to set
     * @see #collections(java.util.List)
     */
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    /**
     * <blockquote><q>
     *     Only the collections of geospatial data enumerated in the values of the collections
     *     parameter SHALL be used to generate the responses for the resource (map) to which they apply.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_9c0fc66b-bc47-4877-b5b3-efdbfbeae7fc"> OGC API - Maps - Part 1: Core - 10.2. Operation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Comma-separated list of collection identifiers, or full URLs to local geospatial resources.</li>
     *     <li>Collections are rendered starting with the first (bottom) and ending with the last
     *     (topmost), unless overridden by styling rules.</li>
     * </ul>
     * <pre>
     * www.example.com/map?collections=buildings,roads
     * </pre>
     *
     * @param collections the collections to set
     */
    public GetMap collections(List<String> collections) {
        setCollections(collections);
        return this;
    }

    /**
     * @return the subset
     * @see #subset(java.util.List)
     */
    public List<String> getSubset() {
        return subset;
    }

    /**
     * @param subset the subset to set
     * @see #subset(java.util.List)
     */
    public void setSubset(List<String> subset) {
        this.subset = subset;
    }

    /**
     * <blockquote><q>
     *     The subset parameter is a common building block used to select a subset of a geospatial
     *     resource shared with other OGC API Standards. This can be specified as an interval between
     *     lower and higher bound values or as a single value.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_02a878e9-746a-4800-9531-2f930bb367ce"> OGC API - Maps - Part 1: Core - 13.3.2. Parameter subset</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Trimming: {@code {axisAbbrev}({low}:{high})}; slicing: {@code {axisAbbrev}({value})}.</li>
     *     <li>{@code Lat}/{@code Lon} axes for geographic CRS, {@code E}/{@code N} for projected CRS,
     *     {@code h}/{@code z} for a third (vertical) dimension.</li>
     *     <li>Multiple {@code subset} parameters are interpreted as if comma-separated in a single one.</li>
     * </ul>
     * <pre>
     * subset=Lat(10:20)
     * subset=h(150)
     * subset=Lat(-90:90)&amp;subset=Lon(-180:180)
     * </pre>
     *
     * @param subset the subset to set
     */
    public GetMap subset(List<String> subset) {
        setSubset(subset);
        return this;
    }

    /**
     * @return the subsetCrs
     * @see #subsetCrs(java.lang.String)
     */
    public String getSubsetCrs() {
        return subsetCrs;
    }

    /**
     * @param subsetCrs the subsetCrs to set
     * @see #subsetCrs(java.lang.String)
     */
    public void setSubsetCrs(String subsetCrs) {
        this.subsetCrs = subsetCrs;
    }

    /**
     * <blockquote><q>
     *     A URI (or safe CURIE) of the coordinate reference system for the coordinates specified in
     *     the {@code subset} parameter. The valid values are [OGC:CRS84], the native (storage) CRS (if
     *     different), or the output {@code crs} (if specified).
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_8bcbded1-5f70-4020-b90a-596fe8b8326b"> OGC API - Maps - Part 1: Core - 13.2.2. Parameter subset-crs</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Defaults to {@code [OGC:CRS84]} if not specified.</li>
     *     <li>Ignored if no {@link #subset(java.util.List)} parameter is used.</li>
     * </ul>
     *
     * @param subsetCrs the subsetCrs to set
     */
    public GetMap subsetCrs(String subsetCrs) {
        setSubsetCrs(subsetCrs);
        return this;
    }

    /**
     * @return the crs
     * @see #crs(java.lang.String)
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the crs to set
     * @see #crs(java.lang.String)
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * <blockquote><q>
     *     A coordinate reference system of the map response. A list of all supported CRS values can be
     *     found under the collection metadata.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_5df53b56-5468-4c9d-acac-6abfddd83ccf"> OGC API - Maps - Part 1: Core - 16.2.1. Parameter crs</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>If the list of supported CRS is not present, only {@code [OGC:CRS84]} is supported.</li>
     *     <li>Defaults to the storage (native) CRS if not specified.</li>
     * </ul>
     *
     * @param crs the crs to set
     */
    public GetMap crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * @return the bgcolor
     * @see #bgColor(java.lang.String)
     */
    public String getBgColor() {
        return bgcolor;
    }

    /**
     * @param bgcolor the bgcolor to set
     * @see #bgColor(java.lang.String)
     */
    public void setBgColor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    /**
     * <blockquote><q>
     *     Hexadecimal red-green-blue color value (0-255) or case-insensitive W3C web color name for
     *     the background color (default=0xFFFFFF).
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_9571a0cb-12d6-4533-8c91-d8d69bc75762"> OGC API - Maps - Part 1: Core - 9.2.1. Parameters transparent and bgcolor</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, default {@code 0xFFFFFF}.</li>
     *     <li>Six hex digits: red, green, blue intensities in that order.</li>
     *     <li>If not specified and {@link #transparency(java.lang.Boolean)} is {@code false} (or the
     *     output format cannot encode transparency), the server uses the style's background color.</li>
     * </ul>
     *
     * @param bgcolor the bgcolor to set
     */
    public GetMap bgColor(String bgcolor) {
        setBgColor(bgcolor);
        return this;
    }

    /**
     * @return the transparency
     * @see #transparency(java.lang.Boolean)
     */
    public Boolean getTransparency() {
        return transparent;
    }

    /**
     * @param transparent the transparency to set
     * @see #transparency(java.lang.Boolean)
     */
    public void setTransparency(Boolean transparent) {
        this.transparent = transparent;
    }

    /**
     * <blockquote><q>
     *     (defaults to {@code true} without a {@code bgcolor} specified, but to {@code false} when a
     *     {@code bgcolor} is used).
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_9571a0cb-12d6-4533-8c91-d8d69bc75762"> OGC API - Maps - Part 1: Core - 9.2.1. Parameters transparent and bgcolor</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, default {@code true}.</li>
     *     <li>If {@code true} and a {@link #bgColor(java.lang.String)} is specified, the background's
     *     opacity is set to 0.</li>
     * </ul>
     *
     * @param transparent the transparency to set
     */
    public GetMap transparency(Boolean transparent) {
        setTransparency(transparent);
        return this;
    }

    /**
     * @return the voidColor
     * @see #voidColor(java.lang.String)
     */
    public String getVoidColor() {
        return voidColor;
    }

    /**
     * @param voidColor the voidColor to set
     * @see #voidColor(java.lang.String)
     */
    public void setVoidColor(String voidColor) {
        this.voidColor = voidColor;
    }

    /**
     * <blockquote><q>
     *     The void-transparent and void-color parameters indicate how parts of the map outside of the
     *     valid area of the projection and CRS will be represented.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_9571a0cb-12d6-4533-8c91-d8d69bc75762"> OGC API - Maps - Part 1: Core - 9.2.2. Parameters void-transparent and void-color</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>If not specified, defaults to the same value as {@link #bgColor(java.lang.String)}.</li>
     *     <li>Only applies to CRSs with an invalid area, such as an orthographic projection.</li>
     * </ul>
     *
     * @param voidColor the voidColor to set
     */
    public GetMap voidColor(String voidColor) {
        setVoidColor(voidColor);
        return this;
    }

    /**
     * @return the voidTransparent
     * @see #voidTransparent(java.lang.Boolean)
     */
    public Boolean getVoidTransparent() {
        return voidTransparent;
    }

    /**
     * @param voidTransparent the voidTransparent to set
     * @see #voidTransparent(java.lang.Boolean)
     */
    public void setVoidTransparent(Boolean voidTransparent) {
        this.voidTransparent = voidTransparent;
    }

    /**
     * <blockquote><q>
     *     The void-transparent and void-color parameters indicate how parts of the map outside of the
     *     valid area of the projection and CRS will be represented.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_9571a0cb-12d6-4533-8c91-d8d69bc75762"> OGC API - Maps - Part 1: Core - 9.2.2. Parameters void-transparent and void-color</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>If not specified, defaults to the same value (specified or default) as
     *     {@link #transparency(java.lang.Boolean)}.</li>
     *     <li>Only applies to CRSs with an invalid area, such as an orthographic projection.</li>
     * </ul>
     *
     * @param voidTransparent the voidTransparent to set
     */
    public GetMap voidTransparent(Boolean voidTransparent) {
        setVoidTransparent(voidTransparent);
        return this;
    }

    /**
     * @return the width
     * @see #width(java.lang.Integer)
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     * @see #width(java.lang.Integer)
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * <blockquote><q>
     *     Width of the viewport in pixel units to present the response (the map subset).
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_e7a9f80c-3a19-4c0c-a45b-196d083a92d1"> OGC API - Maps - Part 1: Core - 11.2.1. Parameters width and height</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, positive integer.</li>
     *     <li>Also defined by the Spatial Subsetting requirements class: takes on a subsetting role
     *     (around {@link #center(org.opengis.geometry.DirectPosition)}) rather than a scaling role
     *     when used without {@link #scaleDenominator(java.lang.Double)} or {@code center}.</li>
     *     <li>A 4xx error is returned together with {@link #bbox(org.opengis.geometry.Envelope)} or
     *     {@link #subset(java.util.List)} as well as {@link #scaleDenominator(java.lang.Double)}.</li>
     * </ul>
     *
     * @param width the width to set
     */
    public GetMap width(Integer width) {
        setWidth(width);
        return this;
    }

    /**
     * @return the height
     * @see #height(java.lang.Integer)
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     * @see #height(java.lang.Integer)
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * <blockquote><q>
     *     Height of the viewport in pixel units to present the response (the map subset).
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_e7a9f80c-3a19-4c0c-a45b-196d083a92d1"> OGC API - Maps - Part 1: Core - 11.2.1. Parameters width and height</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, positive integer.</li>
     *     <li>Also defined by the Spatial Subsetting requirements class: takes on a subsetting role
     *     (around {@link #center(org.opengis.geometry.DirectPosition)}) rather than a scaling role
     *     when used without {@link #scaleDenominator(java.lang.Double)} or {@code center}.</li>
     *     <li>A 4xx error is returned together with {@link #bbox(org.opengis.geometry.Envelope)} or
     *     {@link #subset(java.util.List)} as well as {@link #scaleDenominator(java.lang.Double)}.</li>
     * </ul>
     *
     * @param height the height to set
     */
    public GetMap height(Integer height) {
        setHeight(height);
        return this;
    }

    /**
     * @return the center
     * @see #center(org.opengis.geometry.DirectPosition)
     */
    public DirectPosition getCenter() {
        return center;
    }

    /**
     * @param center the center to set
     * @see #center(org.opengis.geometry.DirectPosition)
     */
    public void setCenter(DirectPosition center) {
        this.center = center;
    }

    /**
     * <blockquote><q>
     *     The center parameter defines the center point of the map in the center-crs coordinates from
     *     which the width and height parameters will define a subset, taking into consideration the
     *     scale and display resolution of the map.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_02a878e9-746a-4800-9531-2f930bb367ce"> OGC API - Maps - Part 1: Core - 13.3.3. Parameter center, width and height</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Coordinates are in {@code [OGC:CRS84]} unless a {@code center-crs} value is specified.</li>
     *     <li>A 4xx error is returned if used together with {@link #bbox(org.opengis.geometry.Envelope)}
     *     and/or {@link #subset(java.util.List)} on the same dimensions.</li>
     * </ul>
     *
     * @param center the center to set
     */
    public GetMap center(DirectPosition center) {
        setCenter(center);
        return this;
    }

    /**
     * @return the scaleDenominator
     * @see #scaleDenominator(java.lang.Double)
     */
    public Double getScaleDenominator() {
        return scaleDenominator;
    }

    /**
     * @param scaleDenominator the scaleDenominator to set
     * @see #scaleDenominator(java.lang.Double)
     */
    public void setScaleDenominator(Double scaleDenominator) {
        this.scaleDenominator = scaleDenominator;
    }

    /**
     * <blockquote><q>
     *     Given a selected unit of measure, the scale denominator specifies how many of these units in
     *     the real world correspond to a distance of one unit on the map as printed or displayed.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_e7a9f80c-3a19-4c0c-a45b-196d083a92d1"> OGC API - Maps - Part 1: Core - 11.2.2. Parameter scale-denominator</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Considers the display resolution ({@link #mmPerPixel(java.lang.Double)} or the 0.28
     *     mm/pixel default) for the local subset of the map being returned.</li>
     *     <li>The only way to specify scaling when requesting a spatial subset using
     *     {@link #center(org.opengis.geometry.DirectPosition)}.</li>
     *     <li>A 4xx error is returned if used together with {@link #width(java.lang.Integer)} and/or
     *     {@link #height(java.lang.Integer)} as well as {@link #bbox(org.opengis.geometry.Envelope)}.</li>
     * </ul>
     *
     * @param scaleDenominator the scaleDenominator to set
     */
    public GetMap scaleDenominator(Double scaleDenominator) {
        setScaleDenominator(scaleDenominator);
        return this;
    }

    /**
     * @return the mmPerPixel
     * @see #mmPerPixel(java.lang.Double)
     */
    public Double getMmPerPixel() {
        return mmPerPixel;
    }

    /**
     * @param mmPerPixel the mmPerPixel to set
     * @see #mmPerPixel(java.lang.Double)
     */
    public void setMmPerPixel(Double mmPerPixel) {
        this.mmPerPixel = mmPerPixel;
    }

    /**
     * <blockquote><q>
     *     size (in millimeters) of a pixel of the rendering device that presents the response.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_8dcce7cf-f163-4061-a697-779c126c1682"> OGC API - Maps - Part 1: Core - 12.2.1. Parameter mm-per-pixel</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, positive number.</li>
     *     <li>If not provided, the server assumes a pixel size of 0.28 millimeters (90.71 pixels per
     *     inch, as standardized in WMS 1.3.0).</li>
     *     <li>Taken into account for {@link #scaleDenominator(java.lang.Double)} and symbology rules.</li>
     * </ul>
     *
     * @param mmPerPixel the mmPerPixel to set
     */
    public GetMap mmPerPixel(Double mmPerPixel) {
        setMmPerPixel(mmPerPixel);
        return this;
    }

    /**
     * @return the orientation
     * @see #orientation(java.lang.Double)
     */
    public Double getOrientation() {
        return orientation;
    }

    /**
     * @param orientation the orientation to set
     * @see #orientation(java.lang.Double)
     */
    public void setOrientation(Double orientation) {
        this.orientation = orientation;
    }

    /**
     * <blockquote><q>
     *     If rotation of the map content is desired, an orientation parameter SHALL be supported that
     *     specifies the amount by which to rotate a map, expressed as counterclockwise degrees. This
     *     results in the viewing perspective being rotated by that same orientation in a clockwise direction.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_09606542-6d1a-4601-9a9d-18ca472dae80"> OGC API - Maps - Part 1: Core - 17.2.1. Parameter orientation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, default {@code 0}.</li>
     *     <li>Applied with the center of the selected spatial subset as pivot point, or the center of
     *     the map if none is specified.</li>
     * </ul>
     *
     * @param orientation the orientation to set
     */
    public GetMap orientation(Double orientation) {
        setOrientation(orientation);
        return this;
    }

    /**
     * @return the crsProjMethod
     * @see #crsProjMethod(java.lang.String)
     */
    public String getCrsProjMethod() {
        return crsProjMethod;
    }

    /**
     * @param crsProjMethod the crsProjMethod to set
     * @see #crsProjMethod(java.lang.String)
     */
    public void setCrsProjMethod(String crsProjMethod) {
        this.crsProjMethod = crsProjMethod;
    }

    /**
     * <blockquote><q>
     *     A crs-proj-method parameter supporting selection of a projection operation method SHALL be
     *     supported.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_d71deb2d-553e-4302-bd6e-a1eeea248581"> OGC API - Maps - Part 1: Core - 18.2.1. Parameter crs-proj-method</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>CURIE or URI, as listed by the {@code /projectionsAndDatums} resource.</li>
     * </ul>
     *
     * @param crsProjMethod the crsProjMethod to set
     */
    public GetMap crsProjMethod(String crsProjMethod) {
        setCrsProjMethod(crsProjMethod);
        return this;
    }

    /**
     * @return the crsProjCenter
     * @see #crsProjCenter(java.lang.String)
     */
    public String getCrsProjCenter() {
        return crsProjCenter;
    }

    /**
     * @param crsProjCenter the crsProjCenter to set
     * @see #crsProjCenter(java.lang.String)
     */
    public void setCrsProjCenter(String crsProjCenter) {
        this.crsProjCenter = crsProjCenter;
    }

    /**
     * <blockquote><q>
     *     A crs-proj-center parameter of the form Lat(centerLat),Lon(centerLon) SHALL be supported to
     *     facilitate the selection of the most relevant projection parameters to center a custom projection.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_d71deb2d-553e-4302-bd6e-a1eeea248581"> OGC API - Maps - Part 1: Core - 18.2.3. Parameter crs-proj-center</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>{@code Lat} and {@code Lon} are mapped to the most relevant operation method parameters
     *     for the selected {@link #crsProjMethod(java.lang.String)}.</li>
     * </ul>
     * <pre>
     * crs-proj-center=Lat(30),Lon(40)
     * </pre>
     *
     * @param crsProjCenter the crsProjCenter to set
     */
    public GetMap crsProjCenter(String crsProjCenter) {
        setCrsProjCenter(crsProjCenter);
        return this;
    }

    /**
     * @return the crsProjParams
     * @see #crsProjParams(java.lang.String)
     */
    public String getCrsProjParams() {
        return crsProjParams;
    }

    /**
     * @param crsProjParams the crsProjParams to set
     * @see #crsProjParams(java.lang.String)
     */
    public void setCrsProjParams(String crsProjParams) {
        this.crsProjParams = crsProjParams;
    }

    /**
     * <blockquote><q>
     *     A crs-proj-params parameter SHALL be supported that enables selection of one or more values
     *     for operation method parameters, with values in between parentheses () following the CURIE
     *     (or URI) of a projection parameter, and different parameters separated by value.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_d71deb2d-553e-4302-bd6e-a1eeea248581"> OGC API - Maps - Part 1: Core - 18.2.2. Parameter crs-proj-params</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, as listed for the selected {@link #crsProjMethod(java.lang.String)} by the
     *     {@code /projectionsAndDatums} resource.</li>
     * </ul>
     * <pre>
     * crs-proj-params=[epsg-parameter:8823](40),[epsg-parameter:8824](90)
     * </pre>
     *
     * @param crsProjParams the crsProjParams to set
     */
    public GetMap crsProjParams(String crsProjParams) {
        setCrsProjParams(crsProjParams);
        return this;
    }

    /**
     * @return the crsDatum
     * @see #crsDatum(java.lang.String)
     */
    public String getCrsDatum() {
        return crsDatum;
    }

    /**
     * @param crsDatum the crsDatum to set
     * @see #crsDatum(java.lang.String)
     */
    public void setCrsDatum(String crsDatum) {
        this.crsDatum = crsDatum;
    }

    /**
     * <blockquote><q>
     *     A crs-datum parameter as a URI allowing to select a datum for the output CRS SHALL be
     *     supported.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_d71deb2d-553e-4302-bd6e-a1eeea248581"> OGC API - Maps - Part 1: Core - 18.2.4. Parameter crs-datum</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, CURIE or URI, as listed by the {@code /projectionsAndDatums} resource.</li>
     *     <li>If not specified, the native (storage) CRS datum is assumed (WGS84 ensemble
     *     {@code [epsg-datum:6326]} if the native CRS is not declared).</li>
     * </ul>
     *
     * @param crsDatum the crsDatum to set
     */
    public GetMap crsDatum(String crsDatum) {
        setCrsDatum(crsDatum);
        return this;
    }

    /**
     * @return the format
     * @see #format(java.lang.String)
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     * @see #format(java.lang.String)
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * <blockquote><q>
     *     HTTP content negotiation addresses the same use cases as the FORMAT= parameter of WMS.
     *     However, for convenience, some implementations can implement an f= parameter for
     *     circumstances where content negotiation is not possible or available.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_9cc2269c-941a-4133-a5eb-e99252b2f62f"> OGC API - Maps - Part 1: Core - 7.2. Map resource (NOTE 2)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Not a formal requirement of the core (HTTP content negotiation is normative); the exact
     *     set of accepted values ({@code png}, {@code jpg}, {@code tiff}) is an implementation choice,
     *     not defined verbatim by the spec.</li>
     * </ul>
     *
     * @param format the format to set
     */
    public GetMap format(String format) {
        setFormat(format);
        return this;
    }

}
