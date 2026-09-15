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
 * TJS {@code GetCapabilities} request parameters.
 *
 * <blockquote><q>
 * Like most OGC services, TJS includes a mandatory GetCapabilities operation. This
 * operation allows clients to retrieve service metadata.
 * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;8.1</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 * @see #updateSequence(String)
 * @see #language(String)
 */
public final class GetCapabilities extends RequestParameters {

    private String updateSequence;
    private String language;

    /**
     * Local identifier used to negotiate the freshness of the service metadata document,
     * as inherited from [OGC 06-121r3] Subclauses 7.2 and 7.3. (optional)
     *
     * @return the updateSequence
     * @see #updateSequence(String)
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @param updateSequence the updateSequence to set
     * @see #updateSequence(String)
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * @param updateSequence the updateSequence to set
     * @see #getUpdateSequence()
     * @see #setUpdateSequence(String)
     */
    public GetCapabilities updateSequence(String updateSequence) {
        setUpdateSequence(updateSequence);
        return this;
    }

    /**
     * <blockquote><q>
     * Language identifier for human readable text. Value is a two or five character IETF
     * RFC 4646 language identifier.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 35</a></small></i>
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
    public GetCapabilities language(String language) {
        setLanguage(language);
        return this;
    }
}
