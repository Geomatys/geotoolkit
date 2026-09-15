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
 * TJS {@code DescribeDatasets} request parameters.
 *
 * <blockquote><q>
 * The DescribeDatasets operation returns an XML document that identifies all of the
 * matching attribute datasets for which data is available from the service instance.
 * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;10.1</a></small></i>
 * </blockquote>
 * <pre>
 * http://foo.bar/foo?
 *     Service=TJS&amp;
 *     Version=1.0&amp;
 *     Request=DescribeDatasets&amp;
 *     FrameworkURI=http://foo.bar2/Provinces/2001&amp;
 *     Language=en-CA
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 * @see #language(String)
 * @see #frameworkURI(String)
 * @see #datasetURI(String)
 */
public final class DescribeDatasets extends RequestParameters {

    private String language;
    private String frameworkURI;
    private String datasetURI;

    /**
     * <blockquote><q>
     * Language identifier for human readable text. Value is a two or five character IETF
     * RFC 4646 language identifier.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 45</a></small></i>
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
    public DescribeDatasets language(String language) {
        setLanguage(language);
        return this;
    }

    /**
     * <blockquote><q>
     * URI of a spatial framework to which the attribute data can be joined.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 45</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>If absent, the response includes descriptions for all frameworks for which data
     * access is supported by the server.</li>
     * </ul>
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
    public DescribeDatasets frameworkURI(String frameworkURI) {
        setFrameworkURI(frameworkURI);
        return this;
    }

    /**
     * <blockquote><q>
     * URI of an attribute table, which can be joined to the spatial framework identified
     * by the FrameworkURI.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 45</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Normally absent from an initial request, since dataset URIs are unknown until a
     * first DescribeDatasets response is obtained.</li>
     * <li>If absent, the response includes descriptions for all datasets available for the
     * specified framework(s).</li>
     * </ul>
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
    public DescribeDatasets datasetURI(String datasetURI) {
        setDatasetURI(datasetURI);
        return this;
    }
}
