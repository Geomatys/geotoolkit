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
package org.geotoolkit.ogcapi.request.styles;

import org.geotoolkit.ogcapi.request.PreferHandling;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     When a style is replaced using a PUT method at {baseResource}/styles/{styleId}, all
 *     representations of that style SHALL be replaced based on the new content, including any
 *     automatically derived representation in other style encodings.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/clause_8_manage_styles.adoc"> OGC API - Styles - Replace a style</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReplaceStyle extends RequestParameters {

    private String collectionId;
    private String styleId;
    private byte[] content;
    private String contentType;
    private Boolean dryRun;
    private PreferHandling handling;

    /**
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     local identifier of a collection
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/collectionId.yaml"> OGC API - Features - Part 1: Core - collectionId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Set to replace a style of a single collection ({@code /collections/{collectionId}/styles/{styleId}});
     *     leave {@code null} to replace a style of the whole dataset ({@code /styles/{styleId}}).</li>
     * </ul>
     */
    public ReplaceStyle collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #styleId(java.lang.String)
     */
    public String getStyleId() {
        return styleId;
    }

    /**
     * @see #styleId(java.lang.String)
     */
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    /**
     * <blockquote><q>
     *     The parameter styleId SHALL be the id property of a style obtained by previously
     *     having queried the Styles resource (i.e., responses to GET requests to {baseResource}/styles).
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/clause_8_manage_styles.adoc"> OGC API - Styles - Style resource endpoints</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public ReplaceStyle styleId(String styleId) {
        setStyleId(styleId);
        return this;
    }

    /**
     * @see #content(byte[])
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @see #content(byte[])
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * <blockquote><q>
     *     Every POST or PUT operation of the server that accepts a stylesheet document as
     *     content SHALL support the media type application/vnd.mapbox.style+json.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/clause_10_style_encodings.adoc"> OGC API - Styles - Mapbox Styles encoding</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The raw stylesheet document to replace the style with, encoded as declared by {@code contentType}.</li>
     * </ul>
     */
    public ReplaceStyle content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * @see #contentType(java.lang.String)
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @see #contentType(java.lang.String)
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * <blockquote><q>
     *     Every POST or PUT operation of the server that accepts a stylesheet document as
     *     content SHALL support the media type application/vnd.ogc.sld+xml.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/clause_10_style_encodings.adoc"> OGC API - Styles - SLD/SE encoding</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; sent as the {@code Content-Type} header.</li>
     *     <li>E.g. {@code application/vnd.mapbox.style+json}, {@code application/vnd.ogc.sld+xml;version=1.0}.</li>
     * </ul>
     */
    public ReplaceStyle contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

    /**
     * @see #dryRun(java.lang.Boolean)
     */
    public Boolean getDryRun() {
        return dryRun;
    }

    /**
     * @see #dryRun(java.lang.Boolean)
     */
    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * If {@code true}, the server validates and otherwise processes the request except that the
     * style is not actually replaced, returning a {@code 204} response on success.
     * <br>
     * <ul>
     *     <li>Optional; default {@code false}.</li>
     *     <li>Not defined by OGC API - Styles (draft), which has no dry-run
     *     requirement at the time of writing; this is a server-specific extension (no verbatim
     *     spec text available for this field).</li>
     * </ul>
     */
    public ReplaceStyle dryRun(Boolean dryRun) {
        setDryRun(dryRun);
        return this;
    }

    /**
     * @see #handling(org.geotoolkit.ogcapi.request.PreferHandling)
     */
    public PreferHandling getHandling() {
        return handling;
    }

    /**
     * @see #handling(org.geotoolkit.ogcapi.request.PreferHandling)
     */
    public void setHandling(PreferHandling handling) {
        this.handling = handling;
    }

    /**
     * The {@code Prefer} header preference (RFC 7240 section 4.4) for stylesheet validation.
     * <br>
     * <ul>
     *     <li>Optional; leave {@code null} to omit the {@code Prefer} header entirely.</li>
     *     <li>Not defined by OGC API - Styles (draft), which does not mention the
     *     {@code Prefer} header at the time of writing; this is a server-specific extension
     *     (no verbatim spec text available for this field).</li>
     * </ul>
     */
    public ReplaceStyle handling(PreferHandling handling) {
        setHandling(handling);
        return this;
    }

}
