/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.xml;

import org.geotoolkit.lang.Static;


/**
 * List some namespaces URLs used by JAXB when (un)marshalling.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@Static
public final class Namespaces {
    /**
     * Do not allow instantiation of this class.
     */
    private Namespaces() {
    }

    /**
     * The {@value} URL.
     */
    public static final String GCO = "http://www.isotc211.org/2005/gco";

    /**
     * The {@value} URL.
     */
    public static final String GMD = "http://www.isotc211.org/2005/gmd";

    /**
     * The {@value} URL.
     */
    public static final String GMX = "http://www.isotc211.org/2005/gmx";

    /**
     * The {@value} URL.
     */
    public static final String GML = "http://www.opengis.net/gml";

    /**
     * The {@value} URL.
     */
    public static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";

    /**
     * The {@value} URL.
     */
    public static final String XLINK = "http://www.w3.org/1999/xlink";

    /**
     * The {@value} URL.
     */
    public static final String FRA = "http://www.cnig.gouv.fr/2005/fra";
}
