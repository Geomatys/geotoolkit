/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.observation.xml;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.gml.xml.AbstractFeature;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.geotoolkit.swe.xml.v101.PhenomenonType;
import org.opengis.observation.CompositePhenomenon;
import org.opengis.observation.Phenomenon;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class XmlObservationUtils {

    public static final String RESPONSE_FORMAT_V100 = "text/xml; subtype=\"om/1.0.0\"";
    public static final String RESPONSE_FORMAT_V200 = "http://www.opengis.net/om/2.0";

    public static List<String> getPhenomenonsFields(final PhenomenonProperty phenProp) {
        final List<String> results = new ArrayList<>();
        if (phenProp.getHref() != null) {
            results.add(phenProp.getHref());
        } else if (phenProp.getPhenomenon() instanceof CompositePhenomenon) {
            final CompositePhenomenon comp = (CompositePhenomenon) phenProp.getPhenomenon();
            for (Phenomenon phen : comp.getComponent()) {
                if (phen instanceof org.geotoolkit.swe.xml.Phenomenon) {
                    final org.geotoolkit.swe.xml.Phenomenon p = (org.geotoolkit.swe.xml.Phenomenon) phen;
                    results.add((p.getName() != null) ? p.getName().getCode() : "");
                }
            }
        } else if (phenProp.getPhenomenon() instanceof org.geotoolkit.swe.xml.Phenomenon) {
            final org.geotoolkit.swe.xml.Phenomenon p = (org.geotoolkit.swe.xml.Phenomenon) phenProp.getPhenomenon();
            results.add((p.getName() != null) ? p.getName().getCode() : "");
        }
        return results;
    }

    public static Phenomenon getPhenomenons(final PhenomenonProperty phenProp) {
        if (phenProp.getHref() != null) {
            return new PhenomenonType(phenProp.getHref(), phenProp.getHref());
        } else if (phenProp.getPhenomenon() != null) {
            return phenProp.getPhenomenon();

        }
        return null;
    }

    public static String getFOIName(final FeatureProperty foiProp) {
        if (foiProp.getHref() != null) {
            return foiProp.getHref();
        } else if (foiProp.getAbstractFeature() != null) {
            final AbstractFeature feat = (AbstractFeature) foiProp.getAbstractFeature();
            return feat.getId();
        }
        return null;
    }

}
