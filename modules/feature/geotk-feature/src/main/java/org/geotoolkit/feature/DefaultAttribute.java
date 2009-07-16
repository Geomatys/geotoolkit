/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009 Geomatys
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
 * Default Attribut implementation.
 *
 * @author Johann Sorel (Geomatys)
 * @author Rob Hranac, VFNY
 * @author Chris Holmes, TOPP
 * @author Ian Schneider
 * @author Jody Garnett
 * @author Gabriel Roldan
 */
public class DefaultAttribute<V extends Object, D extends AttributeDescriptor, I extends Identifier>
        extends DefaultProperty<V,D> implements Attribute {
    
    /**
     * id of the attribute.
     */
    protected final I id;

    /**
     * Static constructor for the most commun use of Attribute classes type.
     * @param content
     * @param type
     * @param id
     * @return
     */
    public static DefaultAttribute<Object,AttributeDescriptor,Identifier> create(Object content, AttributeType type, Identifier id){
        return new DefaultAttribute<Object,AttributeDescriptor,Identifier>(
                content,
                new DefaultAttributeDescriptor(type, type.getName(), 1, 1, true, null),
                id);
    }

    /**
     * Protected constructor, used by subclass which initialize the content after some
     * processing.
     * 
     * @param descriptor
     * @param id
     */
    protected DefaultAttribute(final D descriptor, final I id) {
        super(descriptor);
        this.id = id;
    }

    public DefaultAttribute(final V content, final D descriptor, final I id) {
        super(content, descriptor);
        this.id = id;
        Types.validate(this, getValue());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public I getIdentifier() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType getType() {
        return descriptor.getType();
    }

    /**
     * Override of setValue to convert the newValue to specified type if need be.
     */
    @Override
    public void setValue(Object newValue) throws IllegalArgumentException, IllegalStateException {
        newValue = checkType(newValue);
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

    /**
     * {@inheritDoc }
     */
    @Override
    public void validate() {
        Types.validate(this, this.getValue());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(":");
        sb.append(descriptor.getName().getLocalPart());
        if (!descriptor.getName().getLocalPart().equals(descriptor.getType().getName().getLocalPart()) ||
                id != null) {
            sb.append("<");
            sb.append(descriptor.getType().getName().getLocalPart());
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
    protected Object checkType(Object value) throws IllegalArgumentException {
        if (value != null) {
            final Class<?> target = getType().getBinding();
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
