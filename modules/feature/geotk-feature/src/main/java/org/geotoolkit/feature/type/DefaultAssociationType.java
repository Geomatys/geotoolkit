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
package org.geotoolkit.feature.type;

import org.opengis.util.GenericName;
import java.util.List;
import java.util.Objects;

import org.opengis.feature.FeatureAssociation;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.util.InternationalString;

import static org.apache.sis.util.ArgumentChecks.*;

/**
 * Default implementation of a association type
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 *
 * @deprecated To be replaced by {@link org.apache.sis.feature.DefaultAssociationRole}.
 */
@Deprecated
public class DefaultAssociationType extends DefaultPropertyType<AssociationType> implements AssociationType {

    protected final AttributeType relatedType;

    private AssociationDescriptor descriptor;

    public DefaultAssociationType(final GenericName name, final AttributeType referenceType, final boolean isAbstract,
            final List<Filter> restrictions, final AssociationType superType, final InternationalString description){
        super(name, referenceType.getBinding(), isAbstract, restrictions, superType, description);
        this.relatedType = referenceType;

        ensureNonNull("related type", relatedType);
    }

    final synchronized void setDescriptor(final AssociationDescriptor d) {
        if (descriptor == null) {
            descriptor = d;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType getRelatedType() {
        return relatedType;
    }

    @Override
    public FeatureType getValueType() {
        return (FeatureType) getRelatedType();
    }

    @Override
    public int getMinimumOccurs() {
        return (descriptor != null) ? descriptor.getMinOccurs() : 0;
    }

    @Override
    public int getMaximumOccurs() {
        return (descriptor != null) ? descriptor.getMaxOccurs() : Integer.MAX_VALUE;
    }

    @Override
    public FeatureAssociation newInstance() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ relatedType.hashCode();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof AssociationType)) {
            return false;
        }

        AssociationType asso = (AssociationType) other;

        return super.equals(asso) && Objects.equals(relatedType, asso.getRelatedType());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return new StringBuilder(super.toString()).append("; relatedType=[").append(relatedType).append("]").toString();
    }
}
