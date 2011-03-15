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

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.util.Version;


/**
 * Representation of a {@code WMS GetCapabilities} request, with its parameters.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 */
@Immutable
public final class GetCapabilities {
    /**
     * The output format for this request.
     */
    private final String format;

    private final String language;

    private final Version version;

    public GetCapabilities(final Version version) {
        this(version, null, null);
    }

    public GetCapabilities(final Version version, final String format, final String language) {
        this.version  = version;
        this.format   = format;
        this.language = language;
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
    public final String getService() {
        return "WMS";
    }

    /**
     * {@inheritDoc}
     */
    public final Version getVersion() {
        return version;
    }

}
