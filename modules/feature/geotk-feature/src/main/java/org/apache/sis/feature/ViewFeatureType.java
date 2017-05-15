/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.apache.sis.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.sis.storage.FeatureNaming;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureInstantiationException;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

/**
 * FeatureType implementation which define a filtered view of a reference
 * feature type.
 *
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ViewFeatureType implements DecoratedFeatureType {
    private final FeatureType base;
    private final Set<GenericName> fullNames = new HashSet<>();
    private final FeatureNaming<PropertyType> names = new FeatureNaming<>();
    private boolean isSimple;

    /**
     * Filter feature type properties.
     *
     * @param base reference feature type
     * @param propertyNames properties to include in the feature type view
     */
    public ViewFeatureType(FeatureType base, String ... propertyNames) {
        this(base, new HashSet<>(Arrays.asList(propertyNames)));
    }
    /**
     * Filter feature type properties.
     *
     * @param base reference feature type
     * @param propertyNames properties to include in the feature type view
     */
    public ViewFeatureType(FeatureType base, Set<String> propertyNames) {
        ArgumentChecks.ensureNonNull("type", base);
        this.base = base;

        //NOTE : copied and modified from DefaultFeatureType.computeTransientFields
        isSimple = true;
        for (String pname : propertyNames) {
            PropertyType property = base.getProperty(pname);

            final int minimumOccurs, maximumOccurs;
            if (property instanceof AttributeType<?>) {
                minimumOccurs = ((AttributeType<?>) property).getMinimumOccurs();
                maximumOccurs = ((AttributeType<?>) property).getMaximumOccurs();
                isSimple &= (minimumOccurs == maximumOccurs);
            } else if (property instanceof FeatureAssociationRole) {
                minimumOccurs = ((FeatureAssociationRole) property).getMinimumOccurs();
                maximumOccurs = ((FeatureAssociationRole) property).getMaximumOccurs();
                isSimple = false;
            } else {
                minimumOccurs = 0;
                maximumOccurs = 0;
            }
            if (maximumOccurs != 0) {
                isSimple &= (maximumOccurs == 1);
            }

            //replace operations by ViewOperation
            if(property instanceof Operation){
                property = new DecoratedOperation((Operation) property);
            }

            final GenericName fullName = property.getName();
            fullNames.add(fullName);
            GenericName name = fullName;
            try {
                names.add(null, name, property);
            } catch (IllegalNameException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    @Override
    public FeatureType getDecoratedType() {
        return base;
    }

    /**
     * Redirect to wrapped feature type.
     *
     * @return name
     */
    @Override
    public GenericName getName() {
        return base.getName();
    }

    /**
     * Redirect to wrapped feature type.
     *
     * @return abstract
     */
    @Override
    public boolean isAbstract() {
        return base.isAbstract();
    }

    /**
     *
     * @return true if type is simple
     */
    @Override
    public boolean isSimple() {
        return isSimple;
    }

    /**
     * Redirect to wrapped feature type.
     *
     * @return definition
     */
    @Override
    public InternationalString getDefinition() {
        return base.getDefinition();
    }

    /**
     * Redirect to wrapped feature type.
     *
     * @return designation
     */
    @Override
    public InternationalString getDesignation() {
        return base.getDesignation();
    }

    /**
     * Redirect to wrapped feature type.
     *
     * @return description
     */
    @Override
    public InternationalString getDescription() {
        return base.getDescription();
    }

    @Override
    public PropertyType getProperty(String name) throws PropertyNotFoundException {
        try {
            return names.get(null, name);
        } catch (IllegalNameException e) {
            throw new PropertyNotFoundException("Property " + name + " not found or ambiguous.");
        }
    }

    @Override
    public Collection<? extends PropertyType> getProperties(boolean includeSuperTypes) {
        final Collection<PropertyType> properties = new ArrayList<>();
        final Collection<? extends PropertyType> basePropertiers = base.getProperties(includeSuperTypes);
        for (PropertyType pt : basePropertiers) {
            if(fullNames.contains(pt.getName())){
                properties.add(getProperty(pt.getName().toString()));
            }
        }
        return properties;
    }

    @Override
    public Set<? extends FeatureType> getSuperTypes() {
        return Collections.EMPTY_SET;
    }

    /**
     * NOTE : copied and modified from DefaultFeatureType.isAssignableFrom
     *
     * Returns {@code true} if this type is same or a super-type of the given type.
     * The check is based mainly on the feature type {@linkplain #getName() name}, which should be unique.
     *
     * <div class="note"><b>Analogy:</b>
     * if we compare {@code FeatureType} to {@link Class} in the Java language, then this method is equivalent
     * to {@link Class#isAssignableFrom(Class)}.</div>
     *
     * @param  type  the type to be checked.
     * @return {@code true} if instances of the given type can be assigned to association of this type.
     */
    @Override
    public boolean isAssignableFrom(final FeatureType type) {
        if (type == this) {
            return true; // Optimization for a common case.
        }
        ArgumentChecks.ensureNonNull("type", type);
        return maybeAssignableFrom(this, type);
    }

    /**
     * NOTE : copied and modified from DefaultFeatureType.maybeAssignableFrom
     *
     * Returns {@code true} if the given base type may be the same or a super-type of the given type, using only
     * the name as a criterion. This is a faster check than {@link #isAssignableFrom(FeatureType)}.
     *
     * <p>Performance note: callers should verify that {@code base != type} before to invoke this method.</p>
     */
    static boolean maybeAssignableFrom(final FeatureType base, final FeatureType type) {
        // Slower path for non-SIS implementations.
        if (Objects.equals(base.getName(), type.getName())) {
            return true;
        }
        for (final FeatureType superType : type.getSuperTypes()) {
            if (base == superType || maybeAssignableFrom(base, superType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Feature newInstance() throws FeatureInstantiationException, UnsupportedOperationException {
        return newInstance(base.newInstance());
    }

    public Feature newInstance(Feature base) throws FeatureInstantiationException, UnsupportedOperationException {
        return new ViewFeature(base);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if(obj instanceof FeatureType) {
            final FeatureType that = (FeatureType) obj;
            return isAbstract() == that.isAbstract() &&
                   getName() == that.getName() &&
                   getDefinition() == that.getDefinition() &&
                   getDescription() == that.getDescription() &&
                   getDesignation() == that.getDesignation() &&
                   getSuperTypes().equals(that.getSuperTypes()) &&
                   getProperties(false).equals(that.getProperties(false));
        }
        return false;
    }



    @Override
    public String toString() {
        return FeatureFormat.sharedFormat(this);
    }

    private class ViewFeature extends AbstractFeature implements DecoratedFeature {

        private final Feature base;

        private ViewFeature(Feature base) {
            super(ViewFeatureType.this);
            this.base = base;
        }

        @Override
        public DecoratedFeatureType getType() {
            return ViewFeatureType.this;
        }

        @Override
        public Feature getDecoratedFeature() {
            return base;
        }

        @Override
        public Object getPropertyValue(String name) throws PropertyNotFoundException {
            getProperty(name);
            return base.getPropertyValue(name);
        }

        @Override
        public void setPropertyValue(String name, Object value) throws IllegalArgumentException {
            getProperty(name);
            base.setPropertyValue(name, value);
        }

    }

}
