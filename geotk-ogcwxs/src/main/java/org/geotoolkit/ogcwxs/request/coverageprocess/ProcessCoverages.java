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
package org.geotoolkit.ogcwxs.request.coverageprocess;

import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.wcs.xml.WCSVersion;

/**
 * To evaluate a WCPS abstract syntax query against the coverages offered by the server.
 *
 * <blockquote><q>
 * The additional request type ProcessCoverages allows to submit a WCPS request string and
 * obtain a processing result consisting of a – possibly empty – sequence of data items
 * (such as scalars, features, coverages).
 * </q><i><small><a href="https://docs.ogc.org/is/08-059r4/08-059r4.pdf">OGC 08-059r4, &sect;6.5.1</a></small></i>
 * </blockquote>
 * <p>
 * {@code ProcessCoverages} is a WCS operation defined by the WCS Processing Extension
 * (OGC 08-059r4): the {@code VERSION} parameter therefore identifies the WCS service
 * version, not the WCPS query language version. The query itself, carried in {@code query},
 * is versionless abstract syntax as defined by the WCPS standard (OGC 08-068).
 * </p>
 *
 * @author Quentin Bialota (Geomatys)
 * @see #version(WCSVersion)
 * @see #query(String)
 */
public final class ProcessCoverages extends RequestParameters {

    private WCSVersion version;
    private String query;

    /**
     * <blockquote><q>
     * The request protocol version in the service parameter is that of the WCS Core.
     * </q><i><small><a href="https://docs.ogc.org/is/08-059r4/08-059r4.pdf">OGC 08-059r4, &sect;6.5.2</a></small></i>
     * </blockquote>
     *
     * @return the version
     * @see #version(WCSVersion)
     */
    public WCSVersion getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #version(WCSVersion)
     */
    public void setVersion(WCSVersion version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     * @see #setVersion(WCSVersion)
     */
    public ProcessCoverages version(WCSVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * <blockquote><q>
     * WCPS query string to be executed on the server
     * </q><i><small><a href="https://docs.ogc.org/is/08-059r4/08-059r4.pdf">OGC 08-059r4, Table 3</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Must be expressed in WCPS Abstract Syntax as defined by the WCPS Language
     * Interface Standard (OGC 08-068r2).</li>
     * </ul>
     * <pre>
     * for $c in ( Modis1, Modis2, Modis3 )
     * return
     *     encode( abs( $c.red – $c.green ), "application/x-netcdf" )
     * </pre>
     *
     * @return the query
     * @see #query(String)
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     * @see #query(String)
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @param query the query to set
     * @see #getQuery()
     * @see #setQuery(String)
     */
    public ProcessCoverages query(String query) {
        setQuery(query);
        return this;
    }

}
