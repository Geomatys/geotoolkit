/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.feature.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.atom.xml.Link;

/**
 *
 * @author Rohan FERRE (Geomatys)
 */

public class FeatureSetCollection {

    private FeatureSet fs;

    private List<Link> links;

    private int nbMatched;

    private int nbReturned;

    private boolean hasNumberMatched;

    private final String gmlVersion = "3.2.1";

    private Map<String, String> schemaLocations;

    public FeatureSetCollection() {
        links = new ArrayList<>();
    }

    public FeatureSetCollection(final FeatureSet fs, final List<Link> links, final int nbMatched, final int nbReturned) {
        this.fs = fs;
        this.links = links;
        this.nbMatched = nbMatched;
        this.nbReturned = nbReturned;
        this.hasNumberMatched = true;
    }

    public FeatureSetCollection(final FeatureSet fs, final List<Link> links) {
        this.fs = fs;
        this.links = links;
        this.hasNumberMatched = false;
    }

    /**
     *
     * @return the featureSet
     */
    public FeatureSet getFeatureSet() {
        return fs;
    }

    /**
     *
     * @return the array list of links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     *
     * @return the nbMatched
     */
    public int getNbMatched() {
        return nbMatched;
    }

    /**
     *
     * @return the nbReturned
     */
    public int getNbReturned() {
        return nbReturned;
    }

    /**
     *
     * @return hasNumberMatched
     */
    public boolean hasNumberMatched() {
        return hasNumberMatched;
    }

    /**
     * @return the schemaLocations
     */
    public Map<String, String> getSchemaLocations() {
        return schemaLocations;
    }

    /**
     * @return the gmlVersion
     */
    public String getGmlVersion() {
        return gmlVersion;
    }
}
