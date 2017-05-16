/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wms.xml;

import org.geotoolkit.ows.xml.RequestBase;
import org.apache.sis.util.Version;


/**
 * Representation of a {@code WMS GetCapabilities} request, with its parameters.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 */
public final class GetCapabilities implements RequestBase {
    /**
     * The output format for this request.
     */
    private final String format;

    private final String language;

    private Version version;

    private String service;

    private final String updateSequence;

    public GetCapabilities(final Version version) {
        this(version, null, null, null);
    }

    public GetCapabilities(final Version version, final String format, final String language) {
        this(version, format, language, null);
    }

    public GetCapabilities(final Version version, final String format, final String language, final String updateSequence) {
        this.version        = version;
        this.format         = format;
        this.language       = language;
        this.updateSequence = updateSequence;
    }


    public String getExceptionFormat() {
        return "application/vnd.ogc.se_xml";
    }

    public String getFormat() {
        return format;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getService() {
        if (service == null) {
            return "WMS";
        }
        return service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Version getVersion() {
        return version;
    }

    @Override
    public void setService(final String value) {
        this.service = value;
    }

    @Override
    public void setVersion(final String version) {
        if (version != null) {
            this.version = new Version(version);
        } else {
            this.version = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

}
