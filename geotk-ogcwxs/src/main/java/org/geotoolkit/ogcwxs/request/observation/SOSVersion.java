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
package org.geotoolkit.ogcwxs.request.observation;

/**
 * Supported SOS protocol versions.
 *
 * <p>
 * {@code org.geotoolkit.sos.xml.SOSVersion} (in {@code geotk-xml-sos}) only defines {@code v100} :
 * {@link org.geotoolkit.sos.xml.SOSXmlFactory} itself dispatches on raw version strings rather than
 * that enum, and does support {@code "2.0.0"}. This local enum exists only to give callers of
 * {@link org.geotoolkit.ogcwxs.client.observation.SOSApi} a typed way to pick a version.
 * </p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public enum SOSVersion {
    v100("1.0.0"),
    v200("2.0.0");

    private final String code;

    SOSVersion(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
