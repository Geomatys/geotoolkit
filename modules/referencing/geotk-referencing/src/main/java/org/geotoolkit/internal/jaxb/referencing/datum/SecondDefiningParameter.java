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
import javax.measure.unit.Unit;

import org.opengis.referencing.datum.Ellipsoid;

import org.geotoolkit.internal.jaxb.uom.Measure;
import org.geotoolkit.xml.Namespaces;


/**
 * Stores the second defining parameter of an {@linkplain Ellipsoid ellipsoid}. If
 * {@link Ellipsoid#isIvfDefinitive} is {@code true}, the {@link #inverseFlattening}
 * value should be initialized. Otherwise it is the {@link #semiMinorAxis} parameter
 * that should have be defined.
 * <p>
 * The goal of this class is to allow JAXB to handle a second defining parameter,
 * according to the kind of ellipsoid we are facing to. Exactly one field in this
 * class is non-null; all other fields shall be null.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.06
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
    public SecondDefiningParameter secondDefiningParameter;

    /**
     * The polar radius. {@code null} if the {@link #inverseFlattening} value is defined.
     *
     * @see Ellipsoid#getSemiMinorAxis
     */
    public Double semiMinorAxis;

    /**
     * The inverse of the flattening value. {@code null} if the {@link #semiMinorAxis} value
     * is defined.
     *
     * @see Ellipsoid#getInverseFlattening
     */
    public Double inverseFlattening;

    /**
     * Unit of the ellipsoid axes.
     */
    public Unit<?> unit;

    /**
     * JAXB mandatory empty constructor.
     */
    public SecondDefiningParameter() {
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
        } else {
            if (ellipsoid.isIvfDefinitive()) {
                inverseFlattening = ellipsoid.getInverseFlattening();
            } else {
                semiMinorAxis = ellipsoid.getSemiMinorAxis();
            }
            unit = ellipsoid.getAxisUnit();
        }
    }

    /**
     * Returns the semi-minor axis value as a measurement.
     *
     * @return The measure of the semi-minor axis.
     */
    @XmlElement(namespace = Namespaces.GML)
    public Measure getSemiMinorAxis() {
        return (semiMinorAxis != null) ? new Measure(semiMinorAxis, unit) : null;
    }

    /**
     * Sets the semi-minor axis value. This is invoked by JAXB for unmarshalling.
     *
     * @param semiMinorAxis The semi-minor axis value.
     */
    public void setSemiMinorAxis(final Measure semiMinorAxis) {
        this.semiMinorAxis = semiMinorAxis.value;
    }

    /**
     * Returns the inverse of the flattening value as a measurement.
     * Note: The unit of this measurement is dimensionless.
     *
     * @return The inverse of the flattening value as a measurement.
     */
    @XmlElement(namespace = Namespaces.GML)
    public Measure getInverseFlattening() {
        return (inverseFlattening != null) ? new Measure(inverseFlattening, null) : null;
    }

    /**
     * Sets the inverse of the flattening value. This is invoked by JAXB for unmarshalling.
     *
     * @param inverseFlattening The inverse flattening value.
     */
    public void setInverseFlattening(final Measure inverseFlattening) {
        this.inverseFlattening = inverseFlattening.value;
    }
}
