/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2014, Geomatys
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
package org.geotoolkit.temporal.reference.xmlAdapter;

import javax.xml.bind.annotation.XmlElement;
import org.apache.sis.internal.jaxb.gco.PropertyType;
import org.apache.sis.xml.Namespaces;
import org.geotoolkit.temporal.object.DefaultTemporalEdge;
import org.opengis.temporal.TemporalEdge;

/**
 * JAXB adapter for {@link DefaultTemporalEdge} values mapped to {@link TemporalEdge}.
 *
 * @author Remi Marechal (Geomatys).
 */
public class TemporalEdgeAdapter extends PropertyType<TemporalEdgeAdapter, TemporalEdge> {

    /**
     * Empty constructor for JAXB only.
     */
    public TemporalEdgeAdapter() {
    }

    /**
     * Constructor for the {@link #wrap} method only.
     */
    private TemporalEdgeAdapter(final TemporalEdge edge) {
        super(edge);
    } 

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     * This method is indirectly invoked by the private constructor
     * below, so it shall not depend on the state of this object.
     *
     * @return {@code TimeCS.class}
     */
    @Override
    protected Class<TemporalEdge> getBoundType() {
        return TemporalEdge.class;
    }

    /**
     * Invoked by {@link PropertyType} at marshalling time for wrapping the given value
     * in a {@code <gml:TemporalEdge>} XML element.
     *
     * @param  instant The element to marshall.
     * @return A {@code PropertyType} wrapping the given the element.
     */
    @Override
    protected TemporalEdgeAdapter wrap(TemporalEdge bt) {
        return new TemporalEdgeAdapter(bt);
    }
    
    /**
     * Invoked by JAXB at marshalling time for getting the actual element to write
     * inside the {@code <gml:TemporalEdge>} XML element.
     * This is the value or a copy of the value given in argument to the {@code wrap} method.
     *
     * @return The element to be marshalled.
     */
    @XmlElement(name = "TimeEdge", namespace = Namespaces.GML)
    public DefaultTemporalEdge getElement() {
        return DefaultTemporalEdge.castOrCopy(metadata);
    }

    /**
     * Invoked by JAXB at unmarshalling time for storing the result temporarily.
     *
     * @param edge The unmarshalled element.
     */
    public void setElement(final DefaultTemporalEdge edge) {
        metadata = edge;
    }
}
