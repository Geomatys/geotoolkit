/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.sos.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.gml.xml.AbstractFeature;
import org.geotoolkit.gml.xml.BoundingShape;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.opengis.observation.Observation;

/**
 * Utility methods on Observation XML binding.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OMXMLUtils {

    /**
     * Extract the available feature of interest identifiers in the specified
     * feature property.
     *
     * @param foiProp An xml feature property.
     * @return A feature of interest identifier list.
     */
    public static String getFOIId(final FeatureProperty foiProp) {
        if (foiProp.getHref() != null) {
            return foiProp.getHref();
        } else if (foiProp.getAbstractFeature() != null) {
            final AbstractFeature feat = (AbstractFeature) foiProp.getAbstractFeature();
            return feat.getId();
        }
        return null;
    }

    /**
     * Extract the available phenomenons identifiers in the specified phenomenon
     * property.
     *
     * @param phenProp An xml phenomenon property.
     *
     * @return A phenomenon identifier list.
     */
    public static List<String> getPhenomenonsFields(final PhenomenonProperty phenProp) {
        final List<String> results = new ArrayList<>();
        if (phenProp.getHref() != null) {
            results.add(phenProp.getHref());
        } else if (phenProp.getPhenomenon() instanceof org.opengis.observation.CompositePhenomenon comp) {
            for (org.opengis.observation.Phenomenon phen : comp.getComponent()) {
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

    /**
     * Return an envelope containing all the observation member of the collection.
     *
     * @param version SOS version.
     * @param observations A list of complex observations.
     * @param srsName srs name of the result envelope.
     *
     * @return an envelope.
     */
    public static Envelope getCollectionBound(final String version, final List<? extends Observation> observations, final String srsName) {
        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;

        for (Observation observation: observations) {
            final AbstractFeature feature = (AbstractFeature) observation.getFeatureOfInterest();
            if (feature != null) {
                if (feature.getBoundedBy() != null) {
                    final BoundingShape bound = feature.getBoundedBy();
                    if (bound.getEnvelope() != null) {
                        if (bound.getEnvelope().getLowerCorner() != null
                            && bound.getEnvelope().getLowerCorner().getCoordinate() != null
                            && bound.getEnvelope().getLowerCorner().getCoordinate().length == 2 ) {
                            final double[] lower = bound.getEnvelope().getLowerCorner().getCoordinate();
                            if (lower[0] < minx) {
                                minx = lower[0];
                            }
                            if (lower[1] < miny) {
                                miny = lower[1];
                            }
                        }
                        if (bound.getEnvelope().getUpperCorner() != null
                            && bound.getEnvelope().getUpperCorner().getCoordinate() != null
                            && bound.getEnvelope().getUpperCorner().getCoordinate().length == 2 ) {
                            final double[] upper = bound.getEnvelope().getUpperCorner().getCoordinate();
                            if (upper[0] > maxx) {
                                maxx = upper[0];
                            }
                            if (upper[1] > maxy) {
                                maxy = upper[1];
                            }
                        }
                    }
                }
            }
        }

        if (minx == Double.MAX_VALUE) {
            minx = -180.0;
        }
        if (miny == Double.MAX_VALUE) {
            miny = -90.0;
        }
        if (maxx == (-Double.MAX_VALUE)) {
            maxx = 180.0;
        }
        if (maxy == (-Double.MAX_VALUE)) {
            maxy = 90.0;
        }

        final Envelope env = SOSXmlFactory.buildEnvelope(version, null, minx, miny, maxx, maxy, srsName);
        env.setSrsDimension(2);
        env.setAxisLabels(Arrays.asList("X Y"));
        return env;
    }
}
