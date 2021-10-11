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
package org.geotoolkit.feature.xml.jaxb;

import java.util.Collection;
import java.util.Set;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureInstantiationException;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class XSDFeatureType implements FeatureType {

    final FeatureTypeBuilder builder = new FeatureTypeBuilder();

    private boolean lock = false;


    public boolean isLock(){
        return lock;
    }

    public void lock(){
        lock = true;
    }

    @Override
    public GenericName getName() {
        return builder.getName();
    }

    @Override
    public boolean isAbstract() {
        return builder.build().isAbstract();
    }

    @Override
    public boolean isSimple() {
        boolean isSimple = true;
        for (final PropertyTypeBuilder property : builder.properties()) {
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
                continue; // For feature operations, maximumOccurs is implicitly 0.
            }
            if (maximumOccurs != 0) {
                isSimple &= (maximumOccurs == 1);
            }

            if(!isSimple) break;
        }

        return isSimple;
    }

    @Override
    public PropertyType getProperty(String name) throws PropertyNotFoundException {
        return builder.build().getProperty(name);
    }

    @Override
    public Collection<? extends PropertyType> getProperties(boolean includeSuperTypes) {
        return builder.build().getProperties(includeSuperTypes);
    }

    @Override
    public Set<? extends FeatureType> getSuperTypes() {
        return builder.build().getSuperTypes();
    }

    @Override
    public boolean isAssignableFrom(FeatureType type) {
        return builder.build().isSimple();
    }

    @Override
    public InternationalString getDefinition() {
        return builder.build().getDefinition();
    }

    @Override
    public InternationalString getDesignation() {
        return builder.build().getDesignation();
    }

    @Override
    public InternationalString getDescription() {
        return builder.build().getDescription();
    }

    @Override
    public Feature newInstance() throws FeatureInstantiationException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
