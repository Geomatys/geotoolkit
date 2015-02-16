/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import org.apache.sis.geometry.GeneralEnvelope;

import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.geotoolkit.feature.type.*;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;

/**
 * Abstract implementation of Feature.
 *
 * @author jdeolive
 * @author jgarnett
 * @author Johann Sorel (Geomatys)
 *
 * @module pending
 */
public abstract class AbstractFeature<C extends Collection<Property>> extends AbstractComplexAttribute<C,FeatureId> implements Feature {

    private static final Name NOT_FOUND = new DefaultName("notfound");

    /**
     * Default geometry attribute name
     * We store only the name of the property, otherwise if the properties change
     * we might become inconsistant with the new default geometry.
     */
    protected Name defaultGeometryName;

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
     * Create a Feature with the following content.
     *
     * @param desc Nested type
     * @param id Feature ID
     */
    public AbstractFeature(final FeatureType type, final FeatureId id) {
        super(type, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getType() {
        return (FeatureType) super.getType();
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

        boolean copy = false;
        BoundingBox bounds = null;

        for (Iterator itr = getType().getDescriptors().iterator(); itr.hasNext();) {
            final PropertyDescriptor propertyDesc = (PropertyDescriptor) itr.next();
            if (propertyDesc instanceof GeometryDescriptor) {
                final GeometryAttribute ga = (GeometryAttribute) getProperty(propertyDesc.getName());
                if (ga == null) {
                    continue;
                }
                final Envelope env = ga.getBounds();
                if(env==null || (env instanceof JTSEnvelope2D && ((JTSEnvelope2D)env).isNull()) ){
                    continue;
                }

                final GeneralEnvelope genv = new GeneralEnvelope(env);
                if(genv.isAllNaN()){
                    continue;
                }

                final BoundingBox bbox = DefaultBoundingBox.castOrCopy(env);

                if(bounds == null){
                    //avoid copying geometry bounds if there is only one
                    bounds = bbox;
                }else{
                    if(!copy){
                        //ensure we do not modify the geometry attribut bound
                        copy = true;
                        bounds = new DefaultBoundingBox(bounds);
                    }
                    bounds.include(bbox);
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

        if (defaultGeometryName == null){
            //look for it up from the type
            final GeometryDescriptor baseDesc = getType().getGeometryDescriptor();
            if (baseDesc == null) {
                defaultGeometryName = NOT_FOUND;
            }else{
                defaultGeometryName = baseDesc.getName();
            }
        }

        if(defaultGeometryName == NOT_FOUND){
            return null;
        }else{
            return (GeometryAttribute) getProperty(defaultGeometryName);
        }
    }

    //TODO: REVISIT
    //this implementation seems really bad to me or I am missing something:
    //1- getValue() shouldn't contain the passed in attribute, but the schema should contain its descriptor
    //2- this.defaultGeometry = defaultGeometry means getValue() will  not contain the argument
    @Override
    public void setDefaultGeometryProperty(final GeometryAttribute defaultGeometry) {
        if (!getType().getDescriptors().contains(defaultGeometry.getDescriptor())) {
            throw new IllegalArgumentException("specified attribute is not one of: " + getType());
        }

        this.defaultGeometryName = defaultGeometry.getType().getName();
    }

    @Override
    public void setProperty(org.opengis.feature.Property property) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getPropertyValue(String name) throws IllegalArgumentException {
        final Property prop = getProperty(name);        
        return prop==null ? null : prop.getValue();
    }

    @Override
    public void setPropertyValue(String name, Object value) throws IllegalArgumentException {
        Property prop = getProperty(name);
        if(prop==null){
            final PropertyDescriptor desc = getType().getDescriptor(name);
            if(desc==null){
                throw new IllegalArgumentException("No property for name : "+name);
            }
            prop = FeatureUtilities.defaultProperty(desc);
            getProperties().add(prop);
        }
        prop.setValue(value);
    }
}
