/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.feature.catalog.BoundFeatureAttributeImpl;
import org.geotoolkit.feature.catalog.FeatureAssociationImpl;
import org.opengis.feature.catalog.BoundFeatureAttribute;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI BoundFeature. See
 * package documentation for more information about JAXB and BoundFeature.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class BoundFeatureAttributeAdapter extends XmlAdapter<BoundFeatureAttributeAdapter, BoundFeatureAttribute> {
    
    private BoundFeatureAttribute feature;
    
    @XmlIDREF
    @XmlAttribute(namespace="http://www.w3.org/1999/xlink")
    private BoundFeatureAttributeImpl href;
    
    /**
     * Empty constructor for JAXB only.
     */
    private BoundFeatureAttributeAdapter() {
    }

    /**
     * Wraps an BoundFeature value with a {@code SV_BoundFeature} tags at marshalling-time.
     *
     * @param feature The BoundFeature value to marshall.
     */
    protected BoundFeatureAttributeAdapter(final BoundFeatureAttribute feature) {
        if (feature instanceof BoundFeatureAttributeImpl && ((BoundFeatureAttributeImpl)feature).isReference()) {
            this.href    = (BoundFeatureAttributeImpl) feature;
        } else {
            this.feature = feature;
        }
    }

    /**
     * Returns the BoundFeature value covered by a {@code SV_BoundFeature} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the BoundFeature value.
     */
    protected BoundFeatureAttributeAdapter wrap(final BoundFeatureAttribute value) {
        return new BoundFeatureAttributeAdapter(value);
    }

    /**
     * Returns the {@link BoundFeatureImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_BoundFeature")
    public BoundFeatureAttributeImpl getBoundFeature() {
        if (feature == null) 
            return null;
        return (feature instanceof BoundFeatureAttributeImpl) ?
            (BoundFeatureAttributeImpl)feature : new BoundFeatureAttributeImpl(feature);
    }

    /**
     * Sets the value for the {@link BoundFeatureImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setBoundFeature(final BoundFeatureAttributeImpl BoundFeature) {
        this.feature = BoundFeature;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public BoundFeatureAttribute unmarshal(final BoundFeatureAttributeAdapter value) throws Exception {
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
     * @param value The bound type value, here the BoundFeature.
     * @return The adapter for this BoundFeature.
     */
    @Override
    public BoundFeatureAttributeAdapter marshal(final BoundFeatureAttribute value) throws Exception {
        return new BoundFeatureAttributeAdapter(value);
    }

    
    

}
