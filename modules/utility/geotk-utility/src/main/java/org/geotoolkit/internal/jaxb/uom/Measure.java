/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.uom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.measure.unit.Unit;


/**
 * A measurement value together with its unit of measure.
 * This is used for marshalling an element defined by ISO-19103.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 2.5
 * @module
 */
public final class Measure {
    /**
     * The beginning of the UOM value for GML.
     */
    private static final String UOM_PREFIX = "../uom/gmxUom.xsd#xpointer(//*[@gml:id='";

    /**
     * The end of the uom value for GML.
     */
    private static final String UOM_SUFFIX = "'])";

    /**
     * The value of the measure.
     */
    @XmlValue
    public Double value;

    /**
     * The unit of measure.
     */
    @XmlAttribute(required = true)
    public String uom;

    /**
     * Default empty constructor for JAXB.
     */
    public Measure() {
    }

    /**
     * Constructs a representation of the measure as defined in ISO-19103 standard,
     * with the UOM attribute like {@code '../uom/gmxUom.xsd#xpointer(//*[@gml:id='m'])'}.
     *
     * @param value The value of the measure.
     * @param uom The unit of measure to use.
     */
    public Measure(final Double value, final Unit<?> uom) {
        this(value, (uom != null) ? uom.toString() : "", true);
    }

    /**
     * Constructs a representation of the measure as defined in ISO-19103 standard.
     * According to the value of the parameter isGML, it is possible to just have the unit
     * name or a full GML string, like {@code '../uom/gmxUom.xsd#xpointer(//*[@gml:id='m'])'}.
     *
     * @param value The value of the measure.
     * @param uom   The unit of measure to use.
     * @param isGml If {@code true}, uom would store string like
     *        {@code '../uom/gmxUom.xsd#xpointer(//*[@gml:id='m'])'},
     *        otherwise just the unit name.
     */
    Measure(final Double value, final String uom, final boolean isGML) {
        this.value = value;
        this.uom = (isGML) ? UOM_PREFIX + uom + UOM_SUFFIX : uom;
    }
}
