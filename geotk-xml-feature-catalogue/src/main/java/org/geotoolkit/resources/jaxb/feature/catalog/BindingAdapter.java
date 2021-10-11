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
import org.geotoolkit.feature.catalog.BindingImpl;
import org.opengis.feature.catalog.Binding;

/**
 * JAXB adapter in order to map implementing class with the Types Binding. See
 * package documentation for more information about JAXB and Binding.
 *
 * @module
 * @since 3.03
 * @author Guilhem Legal
 */
public class BindingAdapter extends XmlAdapter<BindingAdapter, Binding> {

    private Binding association;

    /**
     * Empty constructor for JAXB only.
     */
    private BindingAdapter() {
    }

    /**
     * Wraps an Binding value with a {@code SV_Binding} tags at marshalling-time.
     *
     * @param association The Binding value to marshall.
     */
    protected BindingAdapter(final Binding association) {
        this.association = association;
    }

    /**
     * Returns the Binding value covered by a {@code SV_Binding} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the Binding value.
     */
    protected BindingAdapter wrap(final Binding value) {
        return new BindingAdapter(value);
    }

    /**
     * Returns the {@link BindingImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_Binding")
    public BindingImpl getBinding() {
        if (association == null)
            return null;
        return (association instanceof BindingImpl) ?
            (BindingImpl)association : new BindingImpl(association);
    }

    /**
     * Sets the value for the {@link BindingImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setBinding(final BindingImpl Binding) {
        this.association = Binding;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public Binding unmarshal(final BindingAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.association;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the Binding.
     * @return The adapter for this Binding.
     */
    @Override
    public BindingAdapter marshal(final Binding value) throws Exception {
        return new BindingAdapter(value);
    }




}
