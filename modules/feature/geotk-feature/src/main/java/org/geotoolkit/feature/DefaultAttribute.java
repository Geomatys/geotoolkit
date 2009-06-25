/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.feature;

import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.Types;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.Converters;
import org.opengis.feature.Attribute;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.filter.identity.Identifier;


/**
 * Simple, mutable class to store attributes.
 *
 * @author Rob Hranac, VFNY
 * @author Chris Holmes, TOPP
 * @author Ian Schneider
 * @author Jody Garnett
 * @author Gabriel Roldan
 * @version $Id$
 */
public class DefaultAttribute extends DefaultProperty implements Attribute {
    /**
     * id of the attribute.
     */
    protected Identifier id = null;

    public DefaultAttribute(final Object content, final AttributeDescriptor descriptor, final Identifier id) {
        super(content, descriptor);
        this.id = id;
        Types.validate(this, getValue());
    }

    public DefaultAttribute(final Object content, final AttributeType type, final Identifier id) {
        this(content, new DefaultAttributeDescriptor(type, type.getName(), 1, 1, true, null), id);
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    @Override
    public AttributeDescriptor getDescriptor() {
        return (AttributeDescriptor) super.getDescriptor();
    }

    @Override
    public AttributeType getType() {
        return (AttributeType) super.getType();
    }

    /**
     * Override of setValue to convert the newValue to specified type if need
     * be.
     */
    @Override
    public void setValue(Object newValue) throws IllegalArgumentException, IllegalStateException {

        newValue = parse(newValue);

        //TODO: remove this validation
        Types.validate(getType(), this, newValue);
        super.setValue(newValue);
    }

    /**
     * Override of hashCode.
     *
     * @return hashCode for this object.
     */
    @Override
    public int hashCode() {
        return super.hashCode() + (37 * (id == null ? 0 : id.hashCode()));
    }

    /**
     * Override of equals.
     *
     * @param other
     *            the object to be tested for equality.
     *
     * @return whether other is equal to this attribute Type.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DefaultAttribute)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        DefaultAttribute att = (DefaultAttribute) obj;

        return Utilities.equals(id, att.id);
    }

    @Override
    public void validate() {
        Types.validate(this, this.getValue());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(getClass().getSimpleName()).append(":");
        sb.append(getDescriptor().getName().getLocalPart());
        if (!getDescriptor().getName().getLocalPart().equals(getDescriptor().getType().getName().getLocalPart()) ||
                id != null) {
            sb.append("<");
            sb.append(getDescriptor().getType().getName().getLocalPart());
            if (id != null) {
                sb.append(" id=");
                sb.append(id);
            }
            sb.append(">");
        }
        sb.append("=");
        sb.append(value);
        return sb.toString();
    }

    /**
     * Allows this Attribute to convert an argument to its prefered storage
     * type. If no parsing is possible, returns the original value. If a parse
     * is attempted, yet fails (i.e. a poor decimal format) throw the Exception.
     * This is mostly for use internally in Features, but implementors should
     * simply follow the rules to be safe.
     *
     * @param value
     *            the object to attempt parsing of.
     *
     * @return <code>value</code> converted to the preferred storage of this
     *         <code>AttributeType</code>. If no parsing was possible then
     *         the same object is returned.
     *
     * @throws IllegalArgumentException
     *             if parsing is attempted and is unsuccessful.
     */
    protected Object parse(Object value) throws IllegalArgumentException {
        if (value != null) {
            final Class target = getType().getBinding();
            if (!target.isAssignableFrom(value.getClass())) {
                // attempt to convert
                final Object converted = Converters.convert(value, target);
                if (converted != null) {
                    value = converted;
                }
            }
        }

        return value;
    }
}
