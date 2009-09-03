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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.feature.catalog.PropertyTypeImpl;
import org.opengis.feature.catalog.PropertyType;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI PropertyType. See
 * package documentation for more infoFeatureTypermation about JAXB and PropertyType.
 *
 * @since 2.5
 * @source $URL: http://svn.geotools.org/trunk/modules/library/metadata/src/main/java/org/geotools/resources/jaxb/metadata/PropertyTypeAdapter.java $
 * @author Guilhem Legal
 */
public class PropertyTypeAdapter extends XmlAdapter<PropertyTypeAdapter, PropertyType> {
    
    private PropertyType feature;
    
    @XmlIDREF
    @XmlAttribute(namespace="http://www.w3.org/1999/xlink")
    private PropertyTypeImpl href;
    
    /**
     * Empty constructor for JAXB only.
     */
    private PropertyTypeAdapter() {
    }

    /**
     * Wraps an PropertyType value with a {@code SV_PropertyType} tags at marshalling-time.
     *
     * @param feature The PropertyType value to marshall.
     */
    protected PropertyTypeAdapter(final PropertyType feature) {
        if (feature instanceof PropertyTypeImpl && ((PropertyTypeImpl)feature).isReference()) {
            this.href    = (PropertyTypeImpl) feature;
        } else {
            this.feature = feature;
        }
    }

    /**
     * Returns the PropertyType value covered by a {@code SV_PropertyType} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the PropertyType value.
     */
    protected PropertyTypeAdapter wrap(final PropertyType value) {
        return new PropertyTypeAdapter(value);
    }

    /**
     * Returns the {@link PropertyTypeImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElementRef
    public PropertyTypeImpl getPropertyType() {
        if (feature == null) {
            return null;
        }
        return (feature instanceof PropertyTypeImpl) ?
            (PropertyTypeImpl)feature : new PropertyTypeImpl(feature);
    }

    /**
     * Sets the value for the {@link PropertyTypeImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setPropertyType(final PropertyTypeImpl PropertyType) {
        this.feature = PropertyType;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public PropertyType unmarshal(PropertyTypeAdapter value) throws Exception {
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
     * @param value The bound type value, here the PropertyType.
     * @return The adapter for this PropertyType.
     */
    @Override
    public PropertyTypeAdapter marshal(PropertyType value) throws Exception {
        return new PropertyTypeAdapter(value);
    }

    
    

}

