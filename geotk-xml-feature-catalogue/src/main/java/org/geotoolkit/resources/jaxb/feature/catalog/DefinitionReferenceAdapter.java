/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import org.geotoolkit.feature.catalog.DefinitionReferenceImpl;
import org.opengis.feature.catalog.DefinitionReference;

/**
 * JAXB adapter in order to map implementing class with the Types DefinitionReference. See
 * package documentation for more information about JAXB and DefinitionReference.
 *
 * @module
 * @since 3.03
 * @author Guilhem Legal
 */
public class DefinitionReferenceAdapter extends XmlAdapter<DefinitionReferenceAdapter, DefinitionReference> {

    private DefinitionReference feature;

    /**
     * Empty constructor for JAXB only.
     */
    private DefinitionReferenceAdapter() {
    }

    /**
     * Wraps an DefinitionReference value with a {@code SV_DefinitionReference} tags at marshalling-time.
     *
     * @param feature The DefinitionReference value to marshall.
     */
    protected DefinitionReferenceAdapter(final DefinitionReference feature) {
        this.feature = feature;
    }

    /**
     * Returns the DefinitionReference value covered by a {@code SV_DefinitionReference} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the DefinitionReference value.
     */
    protected DefinitionReferenceAdapter wrap(final DefinitionReference value) {
        return new DefinitionReferenceAdapter(value);
    }

    /**
     * Returns the {@link DefinitionReferenceImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_DefinitionReference")
    public DefinitionReferenceImpl getDefinitionReference() {
        if (feature == null)
            return null;
        return (feature instanceof DefinitionReferenceImpl) ? (DefinitionReferenceImpl)feature : new DefinitionReferenceImpl(feature);
    }

    /**
     * Sets the value for the {@link DefinitionReferenceImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setDefinitionReference(final DefinitionReferenceImpl DefinitionReference) {
        this.feature = DefinitionReference;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public DefinitionReference unmarshal(final DefinitionReferenceAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.feature;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the DefinitionReference.
     * @return The adapter for this DefinitionReference.
     */
    @Override
    public DefinitionReferenceAdapter marshal(final DefinitionReference value) throws Exception {
        if (value == null) {
            return null;
        }
        return new DefinitionReferenceAdapter(value);
    }




}
