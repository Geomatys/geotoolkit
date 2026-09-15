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
package org.geotoolkit.ogcwxs.request.join;

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * TJS {@code GetData} request parameters.
 *
 * <blockquote><q>
 * The GetData operation provides a means for a client to retrieve attribute data. The
 * response includes descriptive information about each attribute included in the response,
 * as well as the attribute values for all selected records.
 * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;12.1</a></small></i>
 * </blockquote>
 * <br>
 * <ul>
 * <li>The service metadata may advertise a {@code GeolinkidsLimit} constraint on the
 * {@code OperationsMetadata/Operation[@name='GetData']} element, capping how many
 * geolink identifiers a client may request at once.</li>
 * <li>The service metadata may advertise an {@code AttributeLimit} constraint on the same
 * element, capping how many attributes may be requested in one GetData request.</li>
 * </ul>
 * <pre>
 * &lt;ows:Constraint name=&quot;GeolinkidsLimit&quot;&gt;
 *      &lt;ows:AllowedValues&gt;
 *           &lt;ows:Range&gt;
 *               &lt;ows:MaximumValue&gt;1000&lt;/ows:MaximumValue&gt;
 *           &lt;/ows:Range&gt;
 *      &lt;/ows:AllowedValues&gt;
 *      &lt;ows:Meaning&gt;The maximum number of GeolinkIds that can be specified as
 * part of the GeolinkIds element in a GetData request.&lt;/ows:Meaning&gt;
 * &lt;/ows:Constraint&gt;
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 * @see #language(String)
 * @see #frameworkURI(String)
 * @see #datasetURI(String)
 * @see #attributes(String)
 * @see #linkageKeys(String)
 * @see #filterColumn(String)
 * @see #filterValue(String)
 * @see #xsl(String)
 * @see #aid(Boolean)
 */
public final class GetData extends RequestParameters {

    private String language;
    private String frameworkURI;
    private String datasetURI;
    private String attributes;
    private String linkageKeys;
    private String filterColumn;
    private String filterValue;
    private String xsl;
    private Boolean aid;

    /**
     * <blockquote><q>
     * Language identifier for human readable text. Value is a two or five character IETF
     * RFC 4646 language identifier.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 53</a></small></i>
     * </blockquote>
     *
     * @return the language
     * @see #language(String)
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     * @see #language(String)
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @param language the language to set
     * @see #getLanguage()
     * @see #setLanguage(String)
     */
    public GetData language(String language) {
        setLanguage(language);
        return this;
    }

    /**
     * <blockquote><q>
     * URI of a spatial framework to which the attribute table can be joined.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 53</a></small></i>
     * </blockquote>
     *
     * @return the frameworkURI
     * @see #frameworkURI(String)
     */
    public String getFrameworkURI() {
        return frameworkURI;
    }

    /**
     * @param frameworkURI the frameworkURI to set
     * @see #frameworkURI(String)
     */
    public void setFrameworkURI(String frameworkURI) {
        this.frameworkURI = frameworkURI;
    }

    /**
     * @param frameworkURI the frameworkURI to set
     * @see #getFrameworkURI()
     * @see #setFrameworkURI(String)
     */
    public GetData frameworkURI(String frameworkURI) {
        setFrameworkURI(frameworkURI);
        return this;
    }

    /**
     * <blockquote><q>
     * URI of an attribute table, which can be joined to the spatial framework identified
     * by the FrameworkURI.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 53</a></small></i>
     * </blockquote>
     *
     * @return the datasetURI
     * @see #datasetURI(String)
     */
    public String getDatasetURI() {
        return datasetURI;
    }

    /**
     * @param datasetURI the datasetURI to set
     * @see #datasetURI(String)
     */
    public void setDatasetURI(String datasetURI) {
        this.datasetURI = datasetURI;
    }

    /**
     * @param datasetURI the datasetURI to set
     * @see #getDatasetURI()
     * @see #setDatasetURI(String)
     */
    public GetData datasetURI(String datasetURI) {
        setDatasetURI(datasetURI);
        return this;
    }

    /**
     * <blockquote><q>
     * The names of the attributes requested from the server.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 53</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>The service metadata may advertise an {@code AttributeLimit} constraint capping
     * how many attributes may be requested in one GetData request (see class javadoc).</li>
     * <li>If absent, the response includes all attributes available for the requested
     * dataset (i.e. all dataset columns).</li>
     * </ul>
     *
     * @return the attributes
     * @see #attributes(String)
     */
    public String getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     * @see #attributes(String)
     */
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    /**
     * @param attributes the attributes to set
     * @see #getAttributes()
     * @see #setAttributes(String)
     */
    public GetData attributes(String attributes) {
        setAttributes(attributes);
        return this;
    }

    /**
     * <blockquote><q>
     * The DatasetKey identifiers requested by the user.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 53</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Value is one or more identifiers in comma-delimited format, where ranges are
     * indicated with a minimum and maximum value separated by a dash ("-").</li>
     * <li>The same identifier cannot be requested multiple times.</li>
     * <li>If absent, the response includes all linkage keys available for the requested
     * dataset, apart from those excluded by the filterColumn/filterValue parameters.</li>
     * </ul>
     * <pre>
     * LinkageKeys=10-13,24,35,46-48
     * </pre>
     *
     * @return the linkageKeys
     * @see #linkageKeys(String)
     */
    public String getLinkageKeys() {
        return linkageKeys;
    }

    /**
     * @param linkageKeys the linkageKeys to set
     * @see #linkageKeys(String)
     */
    public void setLinkageKeys(String linkageKeys) {
        this.linkageKeys = linkageKeys;
    }

    /**
     * @param linkageKeys the linkageKeys to set
     * @see #getLinkageKeys()
     * @see #setLinkageKeys(String)
     */
    public GetData linkageKeys(String linkageKeys) {
        setLinkageKeys(linkageKeys);
        return this;
    }

    /**
     * <blockquote><q>
     * The name of a Nominal or Ordinal field in the dataset upon which to filter the
     * contents of the GetData response.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 53</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>If either filterColumn or {@link #getFilterValue()} is absent, the response
     * includes all linkage keys not excluded by the linkageKeys parameter.</li>
     * </ul>
     *
     * @return the filterColumn
     * @see #filterColumn(String)
     */
    public String getFilterColumn() {
        return filterColumn;
    }

    /**
     * @param filterColumn the filterColumn to set
     * @see #filterColumn(String)
     */
    public void setFilterColumn(String filterColumn) {
        this.filterColumn = filterColumn;
    }

    /**
     * @param filterColumn the filterColumn to set
     * @see #getFilterColumn()
     * @see #setFilterColumn(String)
     */
    public GetData filterColumn(String filterColumn) {
        setFilterColumn(filterColumn);
        return this;
    }

    /**
     * <blockquote><q>
     * The Nominal or Ordinal value which the contents of the GetData response shall match.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 53</a></small></i>
     * </blockquote>
     *
     * @return the filterValue
     * @see #filterValue(String)
     */
    public String getFilterValue() {
        return filterValue;
    }

    /**
     * @param filterValue the filterValue to set
     * @see #filterValue(String)
     */
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    /**
     * @param filterValue the filterValue to set
     * @see #getFilterValue()
     * @see #setFilterValue(String)
     */
    public GetData filterValue(String filterValue) {
        setFilterValue(filterValue);
        return this;
    }

    /**
     * <blockquote><q>
     * The URL of a valid XSLT document that shall be referenced in the response.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 53</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>The XSL file must be accessible via the same server and port number as the TJS
     * GetData operation, since cross-domain XSLT references are not supported by most web
     * browsers.</li>
     * </ul>
     *
     * @return the xsl
     * @see #xsl(String)
     */
    public String getXsl() {
        return xsl;
    }

    /**
     * @param xsl the xsl to set
     * @see #xsl(String)
     */
    public void setXsl(String xsl) {
        this.xsl = xsl;
    }

    /**
     * @param xsl the xsl to set
     * @see #getXsl()
     * @see #setXsl(String)
     */
    public GetData xsl(String xsl) {
        setXsl(xsl);
        return this;
    }

    /**
     * <blockquote><q>
     * Attribute Identifier. Value is "true" or "false". When "true", the "aid" attribute
     * shall be included for each "K" and "V" element.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 53</a></small></i>
     * </blockquote>
     *
     * @return the aid
     * @see #aid(Boolean)
     */
    public Boolean getAid() {
        return aid;
    }

    /**
     * @param aid the aid to set
     * @see #aid(Boolean)
     */
    public void setAid(Boolean aid) {
        this.aid = aid;
    }

    /**
     * @param aid the aid to set
     * @see #getAid()
     * @see #setAid(Boolean)
     */
    public GetData aid(Boolean aid) {
        setAid(aid);
        return this;
    }
}
