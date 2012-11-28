/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.wcs.xml;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WCSXmlFactory {

    public static DescribeCoverage createDescribeCoverage(final String version, final String coverage) {
        if ("1.1.1".equals(version)) {
            return new org.geotoolkit.wcs.xml.v111.DescribeCoverageType(coverage);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.wcs.xml.v100.DescribeCoverageType(coverage);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
}
