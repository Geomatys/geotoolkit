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
package org.geotoolkit.feature.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.atom.xml.Link;

/**
 * Object wrapping a collection of FeatureSet adding several additional informations.
 * These informations are used in a WFS/Feature API context, in order to produce XML/JSON output.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FeatureSetWrapper {

    /**
     * The list of FeatureSet wrapped.
     */
    private final List<FeatureSet> featureSets;

    /**
     * A list of HTTP link (used in Feature API).
     */
    private final List<Link> links;

    /**
     * Filter on a property. used in WFS 2.0 GetPropertyValue request.
     */
    private final String valueReference;

    /**
     * Number of feature that match the original request (But not included in the FeatureSets).
     */
    private final Integer nbMatched;

    /**
     * Number of feature included in the FeatureSets.
     * if not specified, it can be computed later.
     */
    private final Integer nbReturned;

    /**
     * List of XSD to include in a XML schemaLocation header.
     */
    private final Map<String, String> schemaLocations;

    /**
     * Acting GML version (example: 3.2.1).
     */
    private final String gmlVersion;

    /**
     * WFS/feature API version.
     * current accepted values:
     * - 1.0.0
     * - 2.0.0
     * - feat-1.0.0
     */
    private final String wfsVersion;

    /**
     * Flag indicating that we must write a single feature as root of the document.
     */
    private final boolean writeSingleFeature;

    /**
     * Full constructor.
     *
     * @param featureSets The list of FeatureSet to wrap.
     * @param valueReference Filter on a property.
     * @param links A list of HTTP link.
     * @param schemaLocations List of XSD to include in a XML schemaLocation header.
     * @param gmlVersion Acting GML version.
     * @param wfsVersion WFS/feature API version.
     * @param nbMatched Number of feature that match the original request.
     * @param nbReturned Number of feature included in the FeatureSets.
     * @param writeSingleFeature Flag indicating that we must write a single feature as root of the document.
     */
    public FeatureSetWrapper(final List<FeatureSet> featureSets, final String valueReference, final List<Link> links, final Map<String, String> schemaLocations, final String gmlVersion,
            final String wfsVersion, final Integer nbMatched, final Integer nbReturned, boolean writeSingleFeature) {
        this.featureSets = featureSets;
        this.gmlVersion = gmlVersion;
        this.wfsVersion = wfsVersion;
        this.schemaLocations = schemaLocations;
        this.nbMatched = nbMatched;
        this.nbReturned = nbReturned;
        this.writeSingleFeature = writeSingleFeature;
        this.valueReference = null;
        this.links = links;
    }

    /**
     * Feature API mode constructor.
     * GML version is set to 3.2.1
     * WFS version is set to feat-1.0.0
     *
     * @param featureSet A single FeatureSet to wrap.
     * @param links A list of HTTP link.
     * @param schemaLocations List of XSD to include in a XML schemaLocation header.
     * @param nbMatched Number of feature that match the original request.
     * @param nbReturned Number of feature included in the FeatureSets.
     */
    public FeatureSetWrapper(FeatureSet featureSet, final List<Link> links, final Map<String, String> schemaLocations, final Integer nbMatched,
            final Integer nbReturned) {
        this.featureSets = Arrays.asList(featureSet);
        this.gmlVersion = "3.2.1";
        this.wfsVersion = "feat-1.0.0";
        this.schemaLocations = schemaLocations;
        this.nbMatched = nbMatched;
        this.nbReturned = nbReturned;
        this.writeSingleFeature = false;
        this.valueReference = null;
        this.links = links;
    }

    /**
     * WFS getFeature mode
     *
     * @param featureSets The list of FeatureSet to wrap.
     * @param schemaLocations List of XSD to include in a XML schemaLocation header.
     * @param gmlVersion Acting GML version.
     * @param wfsVersion WFS/feature API version.
     * @param nbMatched Number of feature that match the original request.
     * @param nbReturned Number of feature included in the FeatureSets.
     * @param writeSingleFeature Flag indicating that we must write a single feature as root of the document.
     */
    public FeatureSetWrapper(final List<FeatureSet> featureSets, final Map<String, String> schemaLocations, final String gmlVersion,
            final String wfsVersion, final Integer nbMatched, final Integer nbReturned, boolean writeSingleFeature) {
        this.featureSets = featureSets;
        this.gmlVersion = gmlVersion;
        this.wfsVersion = wfsVersion;
        this.schemaLocations = schemaLocations;
        this.nbMatched = nbMatched;
        this.nbReturned = nbReturned;
        this.writeSingleFeature = writeSingleFeature;
        this.valueReference = null;
        this.links = new ArrayList<>();
    }

    /**
     * WFS value reference mode.
     * WFS version is set to 2.0.0
     *
     * @param featureSet A single FeatureSet to wrap.
     * @param valueReference Filter on a property.
     * @param nbMatched Number of feature that match the original request.
     * @param nbReturned Number of feature included in the FeatureSets.
     * @param gmlVersion Acting GML version.
     */
    public FeatureSetWrapper(final FeatureSet featureSet, final String valueReference, final Integer nbMatched, final Integer nbReturned, final String gmlVersion) {
        this.featureSets = Arrays.asList(featureSet);
        this.gmlVersion = gmlVersion;
        this.wfsVersion = "2.0.0";
        this.schemaLocations = new HashMap<>();
        this.nbMatched = nbMatched;
        this.nbReturned = nbReturned;
        this.writeSingleFeature = true;
        this.valueReference = valueReference;
        this.links = new ArrayList<>();
    }

    /**
     * @return The list of FeatureSet wrapped.
     */
    public List<FeatureSet> getFeatureSet() {
        return featureSets;
    }

    /**
     * @return A list of XSD to include in a XML schemaLocation header.
     */
    public Map<String, String> getSchemaLocations() {
        return schemaLocations;
    }

    /**
     * @return The acting GML version.
     */
    public String getGmlVersion() {
        return gmlVersion;
    }

    /**
     * @return The WFS/feature API version.
     */
    public String getWfsVersion() {
        return wfsVersion;
    }

    /**
     * @return The number of feature that match the original request.
     */
    public Integer getNbMatched() {
        return nbMatched;
    }

    /**
     * @return the number of feature included in the FeatureSets.
     */
    public Integer getNbReturned() {
        return nbReturned;
    }

    /**
     * @return A flag indicating that we must write a single feature as root of the document.
     */
    public boolean isWriteSingleFeature() {
        return writeSingleFeature;
    }

    /**
     * @return the filter on a property.
     */
    public String getValueReference() {
        return valueReference;
    }

    /**
     * @return A list of HTTP link.
     */
    public List<Link> getLinks() {
        return links;
    }
}
