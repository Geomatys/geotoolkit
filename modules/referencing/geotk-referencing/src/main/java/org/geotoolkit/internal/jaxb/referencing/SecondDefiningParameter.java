/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.measure.unit.Unit;

import org.opengis.referencing.datum.Ellipsoid;

import org.apache.sis.measure.Units;
import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.gco.Measure;


/**
 * Stores the second defining parameter of an {@linkplain Ellipsoid ellipsoid}.
 * The goal of this class is to allow JAXB to handle a second defining parameter,
 * according to the kind of ellipsoid we are facing to.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
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
     * The measure, which is either the polar radius or the inverse of the flattening value.
     * We distinguish those two cases by the unit: if the measure is the inverse flattening,
     * then the unit must be {@link Unit#ONE}.
     *
     * @see Ellipsoid#getSemiMinorAxis
     * @see Ellipsoid#getInverseFlattening
     */
    public Measure measure;

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
                measure = new Measure(ellipsoid.getInverseFlattening(), Unit.ONE);
            } else {
                measure = new Measure(ellipsoid.getSemiMinorAxis(), ellipsoid.getAxisUnit());
                Units.ensureLinear(measure.unit);
            }
        }
    }

    /**
     * Returns {@code true} if the measure is the inverse of the flattening value.
     *
     * @return {@code true} if the measure is the inverse of the flattening value.
     */
    public boolean isIvfDefinitive() {
        return (measure != null) && Unit.ONE.equals(measure.unit);
    }

    /**
     * Returns the semi-minor axis value as a measurement.
     *
     * @return The measure of the semi-minor axis.
     */
    @XmlElement(namespace = Namespaces.GML)
    public Measure getSemiMinorAxis() {
        return isIvfDefinitive() ? null : measure;
    }

    /**
     * Sets the semi-minor axis value. This is invoked by JAXB for unmarshalling.
     *
     * @param measure The semi-minor axis value.
     */
    public void setSemiMinorAxis(final Measure measure) {
        this.measure = measure;
        Units.ensureLinear(measure.unit);
    }

    /**
     * Returns the inverse of the flattening value as a measurement.
     * Note: The unit of this measurement is dimensionless.
     *
     * @return The inverse of the flattening value as a measurement.
     */
    @XmlElement(namespace = Namespaces.GML)
    public Measure getInverseFlattening() {
        return isIvfDefinitive() ? measure : null;
    }

    /**
     * Sets the inverse of the flattening value. This is invoked by JAXB for unmarshalling.
     * <p>
     * Note that some GML wrongly assign the "m" unit to this measure, which is wrong. This
     * method overwrite the unit with a dimensionless one. This is required anyway in order
     * to distinguish between the two cases.
     *
     * @param measure The inverse flattening value.
     */
    public void setInverseFlattening(final Measure measure) {
        this.measure = measure;
        measure.unit = Unit.ONE;
    }
}
