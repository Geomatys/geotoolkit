/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.ows.xml;

import java.util.List;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OWSXmlFactory {
    
    
    public static AcceptVersions buildAcceptVersion(final String currentVersion, final List<String> acceptVersion) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.AcceptVersionsType(acceptVersion);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.AcceptVersionsType(acceptVersion);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AcceptFormats buildAcceptFormat(final String currentVersion, final List<String> acceptformats) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.AcceptFormatsType(acceptformats);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.AcceptFormatsType(acceptformats);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static Sections buildSections(final String currentVersion, final List<String> sections) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.SectionsType(sections);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.SectionsType(sections);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
}
