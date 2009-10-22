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
import org.geotoolkit.feature.catalog.FeatureTypeImpl;
import org.opengis.feature.catalog.FeatureType;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI FeatureType. See
 * package documentation for more information about JAXB and FeatureType.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class FeatureTypeAdapter extends XmlAdapter<FeatureTypeAdapter, FeatureType> {
    
    private FeatureType feature;
    
    @XmlIDREF
    @XmlAttribute(namespace="http://www.w3.org/1999/xlink")
    private FeatureTypeImpl href;
    
    /**
     * Empty constructor for JAXB only.
     */
    private FeatureTypeAdapter() {
    }

    /**
     * Wraps an FeatureType value with a {@code SV_FeatureType} tags at marshalling-time.
     *
     * @param feature The FeatureType value to marshall.
     */
    public FeatureTypeAdapter(final FeatureType feature) {
        if (feature instanceof FeatureTypeImpl && ((FeatureTypeImpl)feature).isReference()) {
            this.href    = (FeatureTypeImpl)feature;
            this.feature = null;
        } else {
            this.href    = null;
            this.feature = feature;
        }
    }

    /**
     * Returns the FeatureType value covered by a {@code SV_FeatureType} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the FeatureType value.
     */
    protected FeatureTypeAdapter wrap(final FeatureType value) {
        return new FeatureTypeAdapter(value);
    }

    /**
     * Returns the {@link FeatureTypeImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_FeatureType")
    public FeatureTypeImpl getFeatureType() {
        if (feature == null)
            return null;
        return (feature instanceof FeatureTypeImpl) ?
            (FeatureTypeImpl)feature : new FeatureTypeImpl(feature);
    }

    /**
     * Sets the value for the {@link FeatureTypeImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setFeatureType(final FeatureTypeImpl FeatureType) {
        this.feature = FeatureType;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public FeatureType unmarshal(FeatureTypeAdapter value) throws Exception {
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
     * @param value The bound type value, here the FeatureType.
     * @return The adapter for this FeatureType.
     */
    @Override
    public FeatureTypeAdapter marshal(FeatureType value) throws Exception {
        return new FeatureTypeAdapter(value);
    }

    
    

}
