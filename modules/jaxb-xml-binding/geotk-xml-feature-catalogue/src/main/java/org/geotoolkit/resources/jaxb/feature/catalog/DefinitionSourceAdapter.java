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
import org.geotoolkit.feature.catalog.DefinitionSourceImpl;
import org.opengis.feature.catalog.DefinitionSource;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI DefinitionSource. See
 * package documentation for more infoFeatureTypermation about JAXB and DefinitionSource.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class DefinitionSourceAdapter extends XmlAdapter<DefinitionSourceAdapter, DefinitionSource> {
    
    private DefinitionSource feature;
    
    @XmlIDREF
    @XmlAttribute(namespace="http://www.w3.org/1999/xlink")
    private DefinitionSourceImpl href;
    
    /**
     * Empty constructor for JAXB only.
     */
    private DefinitionSourceAdapter() {
    }

    /**
     * Wraps an DefinitionSource value with a {@code SV_DefinitionSource} tags at marshalling-time.
     *
     * @param feature The DefinitionSource value to marshall.
     */
    protected DefinitionSourceAdapter(final DefinitionSource feature) {
        if (feature instanceof DefinitionSourceImpl && ((DefinitionSourceImpl)feature).isReference()) {
            this.href    = (DefinitionSourceImpl) feature;
        } else {
            this.feature = feature;
        }
    }

    /**
     * Returns the DefinitionSource value covered by a {@code SV_DefinitionSource} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the DefinitionSource value.
     */
    protected DefinitionSourceAdapter wrap(final DefinitionSource value) {
        return new DefinitionSourceAdapter(value);
    }

    /**
     * Returns the {@link DefinitionSourceImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_DefinitionSource")
    public DefinitionSourceImpl getDefinitionSource() {
        if (feature == null)
            return null;
        return (feature instanceof DefinitionSourceImpl) ?
            (DefinitionSourceImpl)feature : new DefinitionSourceImpl(feature);
    }

    /**
     * Sets the value for the {@link DefinitionSourceImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setDefinitionSource(final DefinitionSourceImpl DefinitionSource) {
        this.feature = DefinitionSource;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public DefinitionSource unmarshal(DefinitionSourceAdapter value) throws Exception {
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
     * @param value The bound type value, here the DefinitionSource.
     * @return The adapter for this DefinitionSource.
     */
    @Override
    public DefinitionSourceAdapter marshal(DefinitionSource value) throws Exception {
        return new DefinitionSourceAdapter(value);
    }

    
    

}

