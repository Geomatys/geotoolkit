/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import com.vividsolutions.jts.geom.Geometry;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.identity.FeatureIdImpl;

import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AttributlessFeature implements SimpleFeature{

    private final Geometry geom;
    private final SimpleFeatureType sft;

    public AttributlessFeature(Geometry geom, CoordinateReferenceSystem crs){

        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();

        typeBuilder.setCRS(crs);
        //set the name
        typeBuilder.setName("attributless");

        //add the attribute types
        typeBuilder.add("the_geom", Geometry.class );

        //set the default geometry
        typeBuilder.setDefaultGeometry("the_geom");

        this.sft = typeBuilder.buildFeatureType();

        this.geom = geom;
    }

    @Override
    public String getID() {
        return "dummyId";
    }

    @Override
    public SimpleFeatureType getType() {
        return sft;
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return sft;
    }

    @Override
    public List<Object> getAttributes() {
        return Collections.singletonList((Object)geom);
    }

    @Override
    public void setAttributes(List<Object> values) {
        throw new UnsupportedOperationException("AttributlessFeature is immutable.");
    }

    @Override
    public void setAttributes(Object[] values) {
        throw new UnsupportedOperationException("AttributlessFeature is immutable.");
    }

    @Override
    public Object getAttribute(String name) {
        if(name.equals("the_geom")){
            return geom;
        }else{
            return null;
        }
    }

    @Override
    public void setAttribute(String name, Object value) {
        throw new UnsupportedOperationException("AttributlessFeature is immutable.");
    }

    @Override
    public Object getAttribute(Name name) {
        if(name.getLocalPart().equals("the_geom")){
            return geom;
        }else{
            return null;
        }
    }

    @Override
    public void setAttribute(Name name, Object value) {
        throw new UnsupportedOperationException("AttributlessFeature is immutable.");
    }

    @Override
    public Object getAttribute(int index) throws IndexOutOfBoundsException {
        if(index == 0){
            return geom;
        }else{
            return null;
        }
    }

    @Override
    public void setAttribute(int index, Object value) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("AttributlessFeature is immutable.");
    }

    @Override
    public int getAttributeCount() {
        return 1;
    }

    @Override
    public Object getDefaultGeometry() {
        return geom;
    }

    @Override
    public void setDefaultGeometry(Object geometry) {
        throw new UnsupportedOperationException("AttributlessFeature is immutable.");
    }

    @Override
    public FeatureId getIdentifier() {
        return new FeatureIdImpl("1");
    }

    @Override
    public BoundingBox getBounds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GeometryAttribute getDefaultGeometryProperty() {
        return new GeometryAttributeImpl(geom, sft.getGeometryDescriptor(), getIdentifier());
    }

    @Override
    public void setDefaultGeometryProperty(GeometryAttribute geometryAttribute) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(Collection<Property> values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<? extends Property> getValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Property> getProperties(Name name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Property getProperty(Name name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Property> getProperties(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Property> getProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Property getProperty(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void validate() throws IllegalAttributeException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AttributeDescriptor getDescriptor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(Object newValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Name getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isNillable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Object, Object> getUserData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
