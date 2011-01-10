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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.feature.catalog.ListedValueImpl;
import org.opengis.feature.catalog.ListedValue;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI ListedValue. See
 * package documentation for more information about JAXB and ListedValue.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class ListedValueAdapter extends XmlAdapter<ListedValueAdapter, ListedValue> {
    
    private ListedValue feature;
    
    /**
     * Empty constructor for JAXB only.
     */
    private ListedValueAdapter() {
    }

    /**
     * Wraps an ListedValue value with a {@code SV_ListedValue} tags at marshalling-time.
     *
     * @param feature The ListedValue value to marshall.
     */
    protected ListedValueAdapter(final ListedValue feature) {
        this.feature = feature;
    }

    /**
     * Returns the ListedValue value covered by a {@code SV_ListedValue} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the ListedValue value.
     */
    protected ListedValueAdapter wrap(final ListedValue value) {
        return new ListedValueAdapter(value);
    }

    /**
     * Returns the {@link ListedValueImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_ListedValue")
    public ListedValueImpl getListedValue() {
        if (feature == null) 
            return null;
        return (feature instanceof ListedValueImpl) ?
            (ListedValueImpl)feature : new ListedValueImpl(feature);
    }

    /**
     * Sets the value for the {@link ListedValueImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setListedValue(final ListedValueImpl ListedValue) {
        this.feature = ListedValue;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public ListedValue unmarshal(final ListedValueAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.feature;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the ListedValue.
     * @return The adapter for this ListedValue.
     */
    @Override
    public ListedValueAdapter marshal(final ListedValue value) throws Exception {
        return new ListedValueAdapter(value);
    }

    
    

}
