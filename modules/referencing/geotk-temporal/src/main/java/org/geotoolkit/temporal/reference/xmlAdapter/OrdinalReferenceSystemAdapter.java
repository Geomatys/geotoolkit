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
import org.geotoolkit.temporal.reference.DefaultOrdinalReferenceSystem;
import org.opengis.temporal.OrdinalReferenceSystem;

/**
 * JAXB adapter for {@link DefaultOrdinalReferenceSystem} values mapped to {@link OrdinalReferenceSystem}.
 *
 * @author Remi Marechal (Geomatys).
 */
public class OrdinalReferenceSystemAdapter extends  PropertyType<OrdinalReferenceSystemAdapter, OrdinalReferenceSystem> {

    /**
     * Empty constructor for JAXB only.
     */
    public OrdinalReferenceSystemAdapter() {
    }

    /**
     * Constructor for the {@link #wrap} method only.
     */
    private OrdinalReferenceSystemAdapter(final OrdinalReferenceSystem ordiRefSystem) {
        super(ordiRefSystem);
    }

    /**
     * Invoked by JAXB at marshalling time for getting the actual element to write
     * inside the {@code <gml:Calendar>} XML element.
     * This is the value or a copy of the value given in argument to the {@code wrap} method.
     *
     * @return The element to be marshalled.
     */
    @XmlElement(name = "TimeOrdinalReferenceSystem", namespace = Namespaces.GML)
    public DefaultOrdinalReferenceSystem getElement() {
        return DefaultOrdinalReferenceSystem.castOrCopy(metadata);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     * This method is indirectly invoked by the private constructor
     * below, so it shall not depend on the state of this object.
     *
     * @return {@code OrdinalReferenceSystem.class}
     */
    @Override
    protected Class<OrdinalReferenceSystem> getBoundType() {
        return OrdinalReferenceSystem.class;
    }

    /**
     * Invoked by {@link PropertyType} at marshalling time for wrapping the given value
     * in a {@code <gml:OrdinalReferenceSystem>} XML element.
     *
     * @param  instant The element to marshall.
     * @return A {@code PropertyType} wrapping the given the element.
     */
    @Override
    protected OrdinalReferenceSystemAdapter wrap(OrdinalReferenceSystem ors) {
        return new OrdinalReferenceSystemAdapter(ors);
    }

    /**
     * Invoked by JAXB at unmarshalling time for storing the result temporarily.
     *
     * @param ordinalRefSystem The unmarshalled element.
     */
    public void setElement(final DefaultOrdinalReferenceSystem ordinalRefSystem) {
        metadata = ordinalRefSystem;
    }
}
