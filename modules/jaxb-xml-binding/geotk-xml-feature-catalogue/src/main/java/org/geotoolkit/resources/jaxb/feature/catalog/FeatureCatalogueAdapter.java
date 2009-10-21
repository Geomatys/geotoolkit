/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2008, Geotools Project Managment Committee (PMC)
 *    (C) 2008, Geomatys
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
package org.geotoolkit.resources.jaxb.feature.catalog;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.feature.catalog.FeatureCatalogueImpl;
import org.opengis.feature.catalog.FeatureCatalogue;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI FeatureCatalogue. See
 * package documentation for more information about JAXB and FeatureCatalogue.
 *
 * @module pending
 * @since 2.5
 * @source $URL: http://svn.geotools.org/trunk/modules/library/metadata/src/main/java/org/geotools/resources/jaxb/metadata/FeatureCatalogueAdapter.java $
 * @author Guilhem Legal
 */
public class FeatureCatalogueAdapter extends XmlAdapter<FeatureCatalogueAdapter, FeatureCatalogue>  {
    
    
    private FeatureCatalogue feature;
    
    @XmlIDREF
    @XmlAttribute(namespace="http://www.w3.org/1999/xlink")
    private FeatureCatalogueImpl href;
    
    /**
     * Empty constructor for JAXB only.
     */
    private FeatureCatalogueAdapter() {
    }

    /**
     * Wraps an FeatureCatalogue value with a {@code SV_FeatureCatalogue} tags at marshalling-time.
     *
     * @param feature The FeatureCatalogue value to marshall.
     */
    public FeatureCatalogueAdapter(final FeatureCatalogue feature) {
        if (feature instanceof FeatureCatalogueImpl && ((FeatureCatalogueImpl)feature).isReference()) {
            this.href = (FeatureCatalogueImpl)feature;
        } else {
            this.feature = feature;
        }
    }

    /**
     * Returns the FeatureCatalogue value covered by a {@code SV_FeatureCatalogue} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the FeatureCatalogue value.
     */
    protected FeatureCatalogueAdapter wrap(final FeatureCatalogue value) {
        return new FeatureCatalogueAdapter(value);
    }

    /**
     * Returns the {@link FeatureCatalogueImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_FeatureCatalogue", required = false)
    public FeatureCatalogueImpl getFeatureCatalogue() {
        if (feature == null)
            return null;
        return (feature instanceof FeatureCatalogueImpl) ?
            (FeatureCatalogueImpl)feature : new FeatureCatalogueImpl(feature);
    }

    /**
     * Sets the value for the {@link FeatureCatalogueImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setFeatureCatalogue(final FeatureCatalogueImpl FeatureCatalogue) {
        this.feature = FeatureCatalogue;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public FeatureCatalogue unmarshal(FeatureCatalogueAdapter value) throws Exception {
        if (value == null) {
            return null;
        } else if (value.href != null) {
            return value.href;
        } else {
            return value.feature;
        }
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the FeatureCatalogue.
     * @return The adapter for this FeatureCatalogue.
     */
    @Override
    public FeatureCatalogueAdapter marshal(FeatureCatalogue value) throws Exception {
        if (value == null) {
            return null;
        } 
        return new FeatureCatalogueAdapter(value);
    }

    
    @Override
    public String toString() {
        return "[FeatureCatalogueAdapter] feature ? " + (feature != null) + " href ?" + (href != null); 
    }
}
