/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.jaxb.geometry;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.sis.xml.Namespaces;

import org.opengis.geometry.Geometry;


/**
 * JAXB adapter for {@link Geometry}, in order to integrate the value in an element
 * complying with OGC/ISO standard. The geometry values are covered by a {@code gml:**}
 * element
 * <p>
 * The default implementation does almost nothing. The geometry objects will <strong>not</strong>
 * create the expected {@link JAXBElement} type. This class is only a hook to be extended by more
 * specialized subclasses in GML modules.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
public class GM_Object extends XmlAdapter<GM_Object, Geometry> {
    /**
     * The Geometry value covered by a {@code gml:**} element.
     */
    @XmlElementRef(name = "AbstractGeometry", namespace = Namespaces.GML, type = JAXBElement.class)
    protected JAXBElement<? extends Geometry> geometry;

    /**
     * Empty constructor for JAXB and subclasses only.
     */
    public GM_Object() {
    }

    /**
     * Converts an adapter read from an XML stream to the GeoAPI interface which will
     * contains this value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for a geometry value.
     * @return An instance of the GeoAPI interface which represents the geometry value.
     */
    @Override
    public final Geometry unmarshal(final GM_Object value) {
        return (value != null) ? value.geometry.getValue() : null;
    }

    /**
     * Converts a GeoAPI interface to the appropriate adapter for the way it will be
     * marshalled into an XML file or stream. JAXB calls automatically this method at
     * marshalling time.
     *
     * @param value The geometry value, here the interface.
     * @return The adapter for the given value.
     */
    @Override
    public final GM_Object marshal(final Geometry value) {
        if (value == null) {
            return null;
        }
        return wrap(value);
    }

    /**
     * Returns the geometry value to be covered by a {@code gml:**} element.
     * The default implementation returns {@code null} if all cases. Subclasses
     * must override this method in order to provide useful marshalling.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the geometry value.
     */
    protected GM_Object wrap(Geometry value) {
        return null;
    }
}
