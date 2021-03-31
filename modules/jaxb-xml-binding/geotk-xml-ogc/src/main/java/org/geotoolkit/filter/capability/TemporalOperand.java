/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.filter.capability;

import java.util.List;
import java.util.ArrayList;
import org.opengis.util.CodeList;


/**
 * Enumeration of the different {@code TemporalOperand} types.
 *
 * <pre>
 * &lt;complexType name="TemporalOperandsType"&lt;
 *   &lt;complexContent&lt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&lt;
 *       &lt;sequence&lt;
 *         &lt;element name="TemporalOperand" maxOccurs="unbounded"&lt;
 *           &lt;complexType&lt;
 *             &lt;complexContent&lt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&lt;
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" /&lt;
 *               &lt;/restriction&lt;
 *             &lt;/complexContent&lt;
 *           &lt;/complexType&lt;
 *         &lt;/element&lt;
 *       &lt;/sequence&lt;
 *     &lt;/restriction&lt;
 *   &lt;/complexContent&lt;
 * &lt;/complexType&lt;
 * </pre>
 *
 * @version <A HREF="http://portal.opengeospatial.org/files/?artifact_id=39968">Implementation specification 2.0</A>
 * @author Johann Sorel (Geomatys)
 * @since GeoAPI 3.1
 */
public final class TemporalOperand extends CodeList<TemporalOperand> {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 8576126878446037604L;

    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<TemporalOperand> VALUES = new ArrayList<TemporalOperand>(19);

    /**
     * Creates an operand in the {@code "http://www.opengis.net/fes/2.0"} namespace.
     *
     * @param  name  the name of the new element. This name must not be in use by an other element of this type.
     */
    private TemporalOperand(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code TemporalOperand}s.
     *
     * @return the list of codes declared in the current JVM.
     */
    public static TemporalOperand[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new TemporalOperand[VALUES.size()]);
        }
    }

    /**
     * Returns the list of codes of the same kind than this code list element.
     * Invoking this method is equivalent to invoking {@link #values()}, except that
     * this method can be invoked on an instance of the parent {@code CodeList} class.
     *
     * @return all code {@linkplain #values() values} for this code list.
     */
    @Override
    public TemporalOperand[] family() {
        return values();
    }

    /**
     * Returns the date type that matches the given string, or returns a
     * new one if none match it. More specifically, this methods returns the first instance for
     * which <code>{@linkplain #name() name()}.{@linkplain String#equals equals}(code)</code>
     * returns {@code true}. If no existing instance is found, then a new one is created for
     * the given name.
     *
     * @param  code  the name of the code to fetch or to create.
     * @return a code matching the given name.
     */
    public static TemporalOperand valueOf(final String code) {
        return valueOf(TemporalOperand.class, code);
    }
}
