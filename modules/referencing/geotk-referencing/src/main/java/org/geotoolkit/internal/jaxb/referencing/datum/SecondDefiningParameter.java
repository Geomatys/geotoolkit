/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.internal.jaxb.referencing.datum;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.xml.Namespaces;
import org.opengis.referencing.datum.Ellipsoid;


/**
 * Stores the second defining parameter of an {@linkplain Ellipsoid ellipsoid}. If 
 * {@link Ellipsoid#isIvfDefinitive} is {@code true}, the {@link #inverseFlattening}
 * value should be initialized. Otherwise it is the {@link #semiMinorAxis} parameter
 * that should have be defined.
 * <p>
 * The goal of this class is to allow JAXB to handle a second defining parameter,
 * according to the kind of ellipsoid we are facing to. Exactly one field in this
 * call is non-null; all other fields shall be null.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 * @module
 */
@XmlRootElement(name = "SecondDefiningParameter", namespace = Namespaces.GML)
public final class SecondDefiningParameter {
    /**
     * Nested parameter, for JAXB purpose.
     */
    @XmlElement(name = "SecondDefiningParameter", namespace = Namespaces.GML)
    private SecondDefiningParameter secondDefiningParameter;

    /**
     * The polar radius. {@code null} if the {@link #inverseFlattening} value is defined.
     *
     * @see Ellipsoid#getSemiMinorAxis
     */
    @XmlElement(namespace = Namespaces.GML)
    private Double semiMinorAxis;

    /**
     * The inverse of the flattening value. {@code null} if the {@link #semiMinorAxis} value
     * is defined.
     *
     * @see Ellipsoid#getInverseFlattening
     */
    @XmlElement(namespace = Namespaces.GML)
    private Double inverseFlattening;

    /**
     * JAXB mandatory empty constructor.
     */
    private SecondDefiningParameter() {
    }

    /**
     * Stores the semi-minor axis or the inverse of the flattening value.
     *
     * @param ellipsoid The ellipsoid from which to get the semi-minor of inverse flattening value.
     * @param nested {@code true} if the element should be nested in an other XML type.
     */
    public SecondDefiningParameter(final Ellipsoid ellipsoid, final boolean nested) {
        if (nested) {
            secondDefiningParameter = new SecondDefiningParameter(ellipsoid, false);
        } else if (ellipsoid.isIvfDefinitive()) {
            inverseFlattening = ellipsoid.getInverseFlattening();
        } else {
            semiMinorAxis = ellipsoid.getSemiMinorAxis();
        }
    }
}
