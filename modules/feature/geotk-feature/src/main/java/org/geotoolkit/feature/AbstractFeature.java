/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.util.Collection;
import java.util.Iterator;

import org.geotoolkit.geometry.DefaultBoundingBox;

import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;

/**
 * Temptative implementation of Feature.
 * <p>
 * NOTE this is work in progress and at this time not really being used throughout the library.
 * </p>
 * @author jdeolive
 * @author jgarnett
 * @author Johann Sorel (Geomatys)
 *
 * TODO : Make this class thread safe or not ?
 * @module pending
 */
public abstract class AbstractFeature<C extends Collection<Property>> extends AbstractComplexAttribute<C,FeatureId> implements Feature {

    /**
     * Default geometry attribute
     */
    protected GeometryAttribute defaultGeometry;
    protected BoundingBox bounds;

    /**
     * Create a Feature with the following content.
     *
     * @param desc Nested descriptor
     * @param id Feature ID
     */
    public AbstractFeature(final AttributeDescriptor desc, final FeatureId id) {
        super(desc, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getType() {
        return (FeatureType) descriptor.getType();
    }

    public void setId(final FeatureId id) {
        this.id = id;
    }

    /**
     * Get the total bounds of this feature which is calculated by doing a union
     * of the bounds of each geometry this feature is associated with.
     *
     * @return An Envelope containing the total bounds of this Feature.
     *
     * @todo REVISIT: what to return if there are no geometries in the feature?
     *       For now we'll return a null envelope, make this part of interface?
     *       (IanS - by OGC standards, all Feature must have geom)
     */
    @Override
    public BoundingBox getBounds() {

        if(bounds == null){
            for (Iterator itr = getValue().iterator(); itr.hasNext();) {
                Property property = (Property) itr.next();
                if (property instanceof GeometryAttribute) {
                    final GeometryAttribute ga = (GeometryAttribute) property;
                    if(bounds == null){
                        final BoundingBox bbox = ga.getBounds();
                        if(!bbox.isEmpty()){
                            bounds = new DefaultBoundingBox(ga.getBounds());
                        }
                    }else{
                        bounds.include(ga.getBounds());
                    }
                }
            }
        }

        return bounds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeometryAttribute getDefaultGeometryProperty() {
        if (defaultGeometry != null) {
            return defaultGeometry;
        }

        synchronized (this) {
            if (defaultGeometry == null) {
                //look it up from the type
                if (getType().getGeometryDescriptor() == null) {
                    return null;
                }

                final GeometryType geometryType = getType().getGeometryDescriptor().getType();

                if (geometryType != null) {
                    for (Iterator itr = getValue().iterator(); itr.hasNext();) {
                        Property property = (Property) itr.next();
                        if (property instanceof GeometryAttribute) {
                            if (property.getType().equals(geometryType)) {
                                defaultGeometry = (GeometryAttribute) property;
                                break;
                            }
                        }
                    }
                }

            }
        }

        return defaultGeometry;
    }

    //TODO: REVISIT
    //this implementation seems really bad to me or I am missing something:
    //1- getValue() shouldn't contain the passed in attribute, but the schema should contain its descriptor
    //2- this.defaultGeometry = defaultGeometry means getValue() will  not contain the argument
    @Override
    public void setDefaultGeometryProperty(final GeometryAttribute defaultGeometry) {
        if (!getValue().contains(defaultGeometry)) {
            throw new IllegalArgumentException("specified attribute is not one of: " + getValue());
        }

        synchronized (this) {
            this.defaultGeometry = defaultGeometry;
        }
    }
}
