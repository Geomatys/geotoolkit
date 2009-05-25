/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import org.opengis.parameter.ParameterValue;


/**
 * An {@code <Parameter>} element in
 * {@linkplain GeographicMetadataFormat geographic metadata format}.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 *
 * @see ParameterValue
 */
public class Parameter extends MetadataAccessor {
    /**
     * Creates a parser for an axis. This constructor should not be invoked
     * directly; use {@link ImageReferencing#getParameter} instead.
     *
     * @param metadata  The metadata which contains this parameter.
     * @param index The index for this instance.
     */
    protected Parameter(final ImageReferencing metadata, final int index) {
        super(metadata.projection);
        selectChild(index);
    }

    /**
     * Creates a parser for a parameter. This constructor should not be invoked
     * directly; use {@link ImageReferencing#getParameter} instead.
     *
     * @param parent The set of all parameters.
     * @param index  The index for this instance.
     */
    Parameter(final ChildList<Parameter> parent, final int index) {
        super(parent);
        selectChild(index);
    }

    /**
     * Returns the name for this parameter, or {@code null} if none.
     *
     * @return The parameter name.
     */
    public String getName() {
        return getAttributeAsString("name");
    }

    /**
     * Sets the name for this parameter.
     *
     * @param name The parameter name, or {@code null} if none.
     */
    public void setName(final String name) {
        setAttributeAsString("name", name);
    }

    /**
     * Returns the value for this parameter, or {@code null} if none.
     *
     * @return The parameter value.
     */
    public double getValue() {
        return getAttributeAsDouble("value");
    }

    /**
     * Sets the value for this parameter.
     *
     * @param value The parameter value, or {@code null} if none.
     */
    public void setValue(final double value) {
        setAttributeAsDouble("value", value);
    }
}
