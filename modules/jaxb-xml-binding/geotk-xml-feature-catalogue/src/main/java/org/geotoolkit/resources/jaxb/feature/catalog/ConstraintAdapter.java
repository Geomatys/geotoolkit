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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.feature.catalog.ConstraintImpl;
import org.opengis.feature.catalog.Constraint;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI Constraint. See
 * package documentation for more infoFeatureTypermation about JAXB and Constraint.
 *
 * @module pending
 * @since 2.5
 * @source $URL: http://svn.geotools.org/trunk/modules/library/metadata/src/main/java/org/geotools/resources/jaxb/metadata/ConstraintAdapter.java $
 * @author Guilhem Legal
 */
public class ConstraintAdapter extends XmlAdapter<ConstraintAdapter, Constraint> {
    
    private Constraint feature;
    
    /**
     * Empty constructor for JAXB only.
     */
    private ConstraintAdapter() {
    }

    /**
     * Wraps an Constraint value with a {@code SV_Constraint} tags at marshalling-time.
     *
     * @param feature The Constraint value to marshall.
     */
    protected ConstraintAdapter(final Constraint feature) {
        this.feature = feature;
    }

    /**
     * Returns the Constraint value covered by a {@code SV_Constraint} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the Constraint value.
     */
    protected ConstraintAdapter wrap(final Constraint value) {
        return new ConstraintAdapter(value);
    }

    /**
     * Returns the {@link ConstraintImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_Constraint")
    public ConstraintImpl getConstraint() {
        if (feature == null) 
            return null;
        return (feature instanceof ConstraintImpl) ?
            (ConstraintImpl)feature : new ConstraintImpl(feature);
    }

    /**
     * Sets the value for the {@link ConstraintImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setConstraint(final ConstraintImpl Constraint) {
        this.feature = Constraint;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public Constraint unmarshal(ConstraintAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.feature;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the Constraint.
     * @return The adapter for this Constraint.
     */
    @Override
    public ConstraintAdapter marshal(Constraint value) throws Exception {
        return new ConstraintAdapter(value);
    }

    
    

}

