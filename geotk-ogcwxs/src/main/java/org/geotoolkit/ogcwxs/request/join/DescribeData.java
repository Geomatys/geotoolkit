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
 * TJS {@code DescribeData} request parameters.
 *
 * <blockquote><q>
 * The DescribeData operation returns an XML document that describes all of the attributes
 * for any dataset identified in the request.
 * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;11.1</a></small></i>
 * </blockquote>
 * <pre>
 * http://foo.bar/foo?
 *     Service=TJS&amp;
 *     Version=1.0&amp;
 *     FrameworkURI=http://foo.bar2/Provinces/2001&amp;
 *     DatasetURI=http://foo.bar/2001Census/2001&amp;
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 * @see #language(String)
 * @see #frameworkURI(String)
 * @see #datasetURI(String)
 * @see #attributes(String)
 */
public final class DescribeData extends RequestParameters {

    private String language;
    private String frameworkURI;
    private String datasetURI;
    private String attributes;

    /**
     * <blockquote><q>
     * Language identifier for human readable text. Value is a two or five character IETF
     * RFC 4646 language identifier.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 49</a></small></i>
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
    public DescribeData language(String language) {
        setLanguage(language);
        return this;
    }

    /**
     * <blockquote><q>
     * URI of a spatial framework to which the attribute table can be joined.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 49</a></small></i>
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
    public DescribeData frameworkURI(String frameworkURI) {
        setFrameworkURI(frameworkURI);
        return this;
    }

    /**
     * <blockquote><q>
     * URI of an attribute table, which can be joined to the spatial framework identified
     * by the FrameworkURI.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 49</a></small></i>
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
    public DescribeData datasetURI(String datasetURI) {
        setDatasetURI(datasetURI);
        return this;
    }

    /**
     * <blockquote><q>
     * The names of the attributes for which descriptions are requested from the server.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 49</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Value is one or more {@code Columnset/Attributes/Column} names as found in the
     * DescribeData response, in comma-delimited format.</li>
     * <li>If absent, the response includes descriptions of all attributes available for
     * the specified dataset.</li>
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
    public DescribeData attributes(String attributes) {
        setAttributes(attributes);
        return this;
    }
}
