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
import org.geotoolkit.temporal.reference.DefaultOrdinalEra;
import org.opengis.temporal.OrdinalEra;

/**
 * JAXB adapter for {@link DefaultOrdinalEra} values mapped to {@link OrdinalEra}.
 *
 * @author Remi Marechal (Geomatys).
 * @author Guilhem Legal (Geomatys).
 * @version 4.0
 * @since   4.0
 */
public class OrdinalEraAdapter extends PropertyType<OrdinalEraAdapter, OrdinalEra> {

    /**
     * Empty constructor for JAXB only.
     */
    public OrdinalEraAdapter() {
    }
    
    /**
     * Constructor for the {@link #wrap} method only.
     */
    private OrdinalEraAdapter(final OrdinalEra ordiRefSystem) {
        super(ordiRefSystem);
    }
    
    /**
     * Invoked by JAXB at marshalling time for getting the actual element to write
     * inside the {@code <gml:OrdinalEra>} XML element.
     * This is the value or a copy of the value given in argument to the {@code wrap} method.
     *
     * @return The element to be marshalled.
     */
    @XmlElement(name = "TimeOrdinalEra", namespace = Namespaces.GML)
    public DefaultOrdinalEra getElement() {
        return DefaultOrdinalEra.castOrCopy(metadata);
    }
    
    /**
     * Invoked by JAXB at marshalling time for getting the actual element to write
     * inside the {@code <gml:OrdinalEra>} XML element.
     * This is the value or a copy of the value given in argument to the {@code wrap} method.
     *
     * @return The element to be marshalled.
     */
    @Override
    protected Class<OrdinalEra> getBoundType() {
        return OrdinalEra.class;
    }

    /**
     * Invoked by {@link PropertyType} at marshalling time for wrapping the given value
     * in a {@code <gml:OrdinalEra>} XML element.
     *
     * @param  instant The element to marshall.
     * @return A {@code PropertyType} wrapping the given the element.
     */
    @Override
    protected OrdinalEraAdapter wrap(OrdinalEra ordiEra) {
        return new OrdinalEraAdapter(ordiEra);
    }

    /**
     * Invoked by JAXB at unmarshalling time for storing the result temporarily.
     *
     * @param ordinalEra The unmarshalled element.
     */
    public void setElement(final DefaultOrdinalEra ordinalEra) {
        metadata = ordinalEra;
    }
}
