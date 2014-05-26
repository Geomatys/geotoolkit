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
package org.geotoolkit.feature.type;

import java.util.*;

import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.util.InternationalString;

import org.apache.sis.util.Classes;
import org.apache.sis.feature.AbstractIdentifiedType;
import org.apache.sis.internal.util.UnmodifiableArrayList;

import static org.apache.sis.util.ArgumentChecks.*;

/**
 * Default implementation of a property type
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 *
 * @deprecated Replaced by Apache SIS {@link AbstractIdentifiedType}.
 */
@Deprecated
public class DefaultPropertyType<T extends PropertyType> extends AbstractIdentifiedType implements PropertyType {

    private static final List<Filter> NO_RESTRICTIONS = Collections.emptyList();

    protected final Name name;
    protected final Class<?> binding;
    protected final boolean isAbstract;
    protected final T superType;
    protected final List<Filter> restrictions;
    protected final Map<Object, Object> userData;

    public DefaultPropertyType(final Name name, final Class<?> binding, final boolean isAbstract,
            final List<Filter> restrictions, final T superType, final InternationalString description)
    {
        super(properties(name.getLocalPart(), description));
        ensureNonNull("name", name);

        if (binding == null) {
            if (superType != null && superType.getBinding() != null) {
                // FIXME: This should be optional as the superType may have the required information?
                throw new NullPointerException("Binding to a Java class, did you mean to bind to " + superType.getBinding());
            }
            throw new NullPointerException("Binding to a Java class is required");
        }

        this.name = name;
        this.binding = binding;
        this.isAbstract = isAbstract;

        if (restrictions == null || restrictions.isEmpty()) {
            this.restrictions = NO_RESTRICTIONS;
        } else {
            this.restrictions = UnmodifiableArrayList.wrap(restrictions.toArray(new Filter[restrictions.size()]));
        }

        this.superType = superType;
        this.userData = new HashMap<Object, Object>();
    }

    private static Map<String,Object> properties(final String name, final InternationalString description) {
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(NAME_KEY, name);
        properties.put(DESCRIPTION_KEY, description);
        return properties;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Name getName() {
        return name;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<?> getBinding() {
        return binding;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Filter> getRestrictions() {
        return restrictions;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getSuper() {
        return superType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return getName().hashCode() ^ getBinding().hashCode() ^ (getDescription() != null ? getDescription().hashCode() : 17);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PropertyType)) {
            return false;
        }

        final PropertyType prop = (PropertyType) other;

        if (!Objects.equals(name, prop.getName())) {
            return false;
        }

        if (!Objects.equals(binding, prop.getBinding())) {
            return false;
        }

        if (isAbstract != prop.isAbstract()) {
            return false;
        }

        if (!equals(getRestrictions(), prop.getRestrictions())) {
            return false;
        }

        if (!Objects.equals(superType, prop.getSuper())) {
            return false;
        }

        if (!Objects.equals(super.getDescription(), prop.getDescription())) {
            return false;
        }

        return true;
    }

    /**
     * Convenience method for testing two lists for equality. One or both objects may be null,
     * and considers null and emtpy list as equal
     */
    private boolean equals(final List object1, final List object2) {
        if ((object1 == object2) || (object1 != null && object1.equals(object2))) {
            return true;
        }
        if (object1 == null && object2.isEmpty()) {
            return true;
        }
        if (object2 == null && object1.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public Map<Object, Object> getUserData() {
        return userData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append(" ");
        sb.append(getName());
        if (isAbstract()) {
            sb.append(" abstract");
        }
        if (superType != null) {
            sb.append(" extends ");
            sb.append(superType.getName().getLocalPart());
        }
        if (binding != null) {
            sb.append("<");
            sb.append(Classes.getShortName(binding));
            sb.append(">");
        }
        if (super.getDescription() != null) {
            sb.append("\n\tdescription=");
            sb.append(super.getDescription());
        }
        if (restrictions != null && !restrictions.isEmpty()) {
            sb.append("\nrestrictions=");
            boolean first = true;
            for (Filter filter : restrictions) {
                if (first) {
                    first = false;
                } else {
                    sb.append(" AND ");
                }
                sb.append(filter);
            }
        }
        return sb.toString();
    }
}
