/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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

import javax.imageio.metadata.IIOMetadata;
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
 *
 * @deprecated Replaced by the standard metadata objects defined by ISO 19115-2. The
 *   {@link SpatialMetadata} class can convert automatically those metadata objects
 *   to {@code IIOMetadata}.
 */
@Deprecated
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
        return getAttribute("name");
    }

    /**
     * Sets the name for this parameter.
     *
     * @param name The parameter name, or {@code null} if none.
     */
    public void setName(final String name) {
        setAttribute("name", name);
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
        setAttribute("value", value);
    }

    /**
     * A list of {@linkplain Parameter parameters}.
     */
    @Deprecated
    static final class List extends ChildList<Parameter> {
        /** Creates a parser for parameters. */
        public List(final IIOMetadata metadata) {
            super(metadata, "rectifiedGridDomain/crs/projection", "parameter");
        }

        /** Creates a new parameter. */
        @Override
        protected Parameter newChild(final int index) {
            return new Parameter(this, index);
        }
    }
}
