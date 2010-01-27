/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.feature.simple;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.feature.DefaultGeometryAttribute;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureValidationUtilities;
import org.geotoolkit.feature.SimpleIllegalAttributeException;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.BoundingBox;

/**
 * Abstract simple feature class, will delegate most Feature methods
 * to the simpleFeature interface methods.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractSimpleFeature implements SimpleFeature{

    protected abstract boolean isValidating();

    protected abstract Map<String,Integer> getIndex();

    protected abstract Object[] getValues();

    protected abstract Map<Object, Object>[] getAttributUserData();

    //simpel feature-----------------------------------------------------------

    @Override
    public String getID() {
        return getIdentifier().getID();
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return getType();
    }

    @Override
    public void setAttributes(List<Object> values) {
        final Object[] array = getValues();

        for (int i = 0; i < array.length; i++) {
            array[i] = values.get(i);
        }
    }

    @Override
    public void setAttributes(Object[] values) {
        final Object[] array = getValues();

        for (int i = 0; i < array.length; i++) {
            array[i] = values[i];
        }
    }

    @Override
    public void setAttribute(String name, Object value) {
        final Integer idx = getIndex().get(name);
        if (idx == null) {
            throw new SimpleIllegalAttributeException("Unknown attribute " + name);
        }
        setAttribute(idx, value);
    }

    @Override
    public void setAttribute(Name name, Object value) {
        setAttribute(DefaultName.toExtendedForm(name), value);
    }

    @Override
    public void setAttribute(int index, Object value) throws IndexOutOfBoundsException {
        final SimpleFeatureType type = getFeatureType();
        // first do conversion
        final Object converted = Converters.convert(value, type.getDescriptor(index).getType().getBinding());
        // if necessary, validation too
        if (isValidating()) {
            FeatureValidationUtilities.validate(type.getDescriptor(index), converted);
        }
        // finally set the value into the feature
        getValues()[index] = converted;
    }

    @Override
    public List<Object> getAttributes() {
        return UnmodifiableArrayList.wrap(getValues());
    }

    @Override
    public int getAttributeCount() {
        return getValues().length;
    }

    @Override
    public Object getAttribute(Name name) {
        return getAttribute(DefaultName.toExtendedForm(name));
    }

    @Override
    public Object getAttribute(String name) {
        Integer idx = getIndex().get(name);
        if (idx != null) {
            return getAttribute(idx);
        } else {
            return null;
        }
    }
    
    @Override
    public Object getAttribute(int index) throws IndexOutOfBoundsException {
        return getValues()[index];
    }

    @Override
    public Object getDefaultGeometry() {
        final Map<String,Integer> index = getIndex();

        // should be specified in the index as the default key (null)
        final Integer indexGeom = index.get(null);

        Object defaultGeometry = indexGeom != null ? getValues()[indexGeom] : null;

        // not found? do we have a default geometry at all?
        if (defaultGeometry == null) {
            final GeometryDescriptor geometryDescriptor = getFeatureType().getGeometryDescriptor();
            if (geometryDescriptor != null) {
                final Integer defaultGeomIndex = index.get(DefaultName.toExtendedForm(geometryDescriptor.getName()));
                defaultGeometry = getAttribute(defaultGeomIndex.intValue());
            }
        }
//        // not found? Ok, let's do a lookup then...
//        if ( defaultGeometry == null ) {
//            for ( Object o : values ) {
//                if ( o instanceof Geometry ) {
//                    defaultGeometry = o;
//                    break;
//                }
//            }
//        }

        return defaultGeometry;
    }

    @Override
    public void setDefaultGeometry(Object geometry) {
        Integer geometryIndex = getIndex().get(null);
        if (geometryIndex != null) {
            setAttribute(geometryIndex, geometry);
        }
    }

    //feature methods ----------------------------------------------------------

    @Override
    public BoundingBox getBounds() {
        //TODO: cache this value
        final JTSEnvelope2D bounds = new JTSEnvelope2D(getFeatureType().getCoordinateReferenceSystem());
        for (Object o : getValues()) {
            if (o instanceof Geometry) {
                final Geometry g = (Geometry) o;
                //TODO: check userData for crs... and ensure its of the same
                // crs as the feature type
                if (bounds.isNull()) {
                    bounds.init(g.getEnvelopeInternal());
                } else {
                    bounds.expandToInclude(g.getEnvelopeInternal());
                }
            }
        }

        return bounds;
    }

    @Override
    public GeometryAttribute getDefaultGeometryProperty() {
        final GeometryDescriptor geometryDescriptor = getFeatureType().getGeometryDescriptor();
        GeometryAttribute geometryAttribute = null;
        if (geometryDescriptor != null) {
            Object defaultGeometry = getDefaultGeometry();
            geometryAttribute = new DefaultGeometryAttribute(defaultGeometry, geometryDescriptor, null);
        }
        return geometryAttribute;
    }

    @Override
    public void setDefaultGeometryProperty(GeometryAttribute geometryAttribute) {
        if (geometryAttribute != null) {
            setDefaultGeometry(geometryAttribute.getValue());
        } else {
            setDefaultGeometry(null);
        }
    }

    @Override
    public void setValue(Collection<Property> values) {
        final Object[] array = getValues();
        int i = 0;
        for (Property p : values) {
            array[i] = p.getValue();
            i++;
        }
    }

    @Override
    public Collection<? extends Property> getValue() {
        return getProperties();
    }

    @Override
    public Collection<Property> getProperties(Name name) {
        return getProperties(DefaultName.toExtendedForm(name));
    }

    @Override
    public Property getProperty(Name name) {
        return getProperty(DefaultName.toExtendedForm(name));
    }

    @Override
    public Collection<Property> getProperties(String name) {
        final Integer idx = getIndex().get(name);
        if (idx != null) {
            // cast temporarily to a plain collection to avoid type problems with generics
            Collection c = Collections.singleton(new Attribute(idx));
            return c;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<Property> getProperties() {
        return new AttributeList();
    }

    @Override
    public Property getProperty(String name) {
        final Integer idx = getIndex().get(name);
        if (idx == null) {
            return null;
        } else {
            final int index = idx;
            AttributeDescriptor descriptor = getFeatureType().getDescriptor(index);
            if (descriptor instanceof GeometryDescriptor) {
                return new GeometryAttribut(index);
            } else {
                return new Attribute(index);
            }
        }
    }

    @Override
    public void validate() throws IllegalAttributeException {
        final Object[] values = getValues();
        final SimpleFeatureType type = getFeatureType();

        for (int i = 0; i < values.length; i++) {
            AttributeDescriptor descriptor = type.getDescriptor(i);
            FeatureValidationUtilities.validate(descriptor, values[i]);
        }
    }

    @Override
    public AttributeDescriptor getDescriptor() {
        return null;
    }

    @Override
    public void setValue(Object newValue) {
        setValue((Collection<Property>) newValue);
    }

    @Override
    public Name getName() {
        return null;
    }

    @Override
    public boolean isNillable() {
        return true;
    }

    @Override
    public String toString() {

        final StringWriter writer = new StringWriter();
        writer.append(this.getClass().getName());
        writer.append('\n');

        final FeatureType featureType = getFeatureType();
        if (featureType != null) {
            writer.append("featureType:").append(featureType.getName().toString()).append('\n');
        }

        //make a nice table to display
        final TableWriter tablewriter = new TableWriter(writer);
        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        tablewriter.write("@id\t"+getID()+"\n");

        for(Property prop : getProperties()){
            tablewriter.write(DefaultName.toJCRExtendedForm(prop.getName()));
            tablewriter.write("\t");
            tablewriter.write(prop.getValue().toString());
            tablewriter.write("\n");
        }
        
        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        
        try {
            tablewriter.flush();
            writer.flush();
        } catch (IOException ex) {
            //will never happen is this case
            ex.printStackTrace();
        }

        return writer.toString();
    }


    ////////////////////////////////////////////////////////////////////////////
    // MAPPING CLASSES SIMPLEFEATURE TO FEATURE ////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////


    /**
     * Live collection backed directly on the value array
     */
    class AttributeList extends AbstractList<Property> {

        @Override
        public Attribute get(int index) {
            return new Attribute(index);
        }

        @Override
        public Attribute set(int index, Property element) {
            AbstractSimpleFeature.this.setAttribute(index, element.getValue());
            return null;
        }

        @Override
        public int size() {
            return AbstractSimpleFeature.this.getAttributeCount();
        }
    }

    /**
     * Attribute that delegates directly to the value array
     */
    private class Attribute implements org.opengis.feature.Attribute {

        private final int index;

        Attribute(int index) {
            this.index = index;
        }

        @Override
        public Identifier getIdentifier() {
            return null;
        }

        @Override
        public AttributeDescriptor getDescriptor() {
            return getFeatureType().getDescriptor(index);
        }

        @Override
        public AttributeType getType() {
            return getFeatureType().getType(index);
        }

        @Override
        public Name getName() {
            return getDescriptor().getName();
        }

        @Override
        public Map<Object, Object> getUserData() {
            Map<Object, Object>[] attributeUserData = getAttributUserData();
            // lazily create the attribute user data
            if (attributeUserData[index] == null) {
                attributeUserData[index] = new HashMap<Object, Object>();
            }
            return attributeUserData[index];
        }

        @Override
        public Object getValue() {
            return AbstractSimpleFeature.this.getAttribute(index);
        }

        @Override
        public boolean isNillable() {
            return getDescriptor().isNillable();
        }

        @Override
        public void setValue(Object newValue) {
            AbstractSimpleFeature.this.setAttribute(index, newValue);
        }

        @Override
        public void validate() {
            FeatureValidationUtilities.validate(getDescriptor(), getValues()[index]);
        }
    }

    private class GeometryAttribut extends Attribute implements GeometryAttribute{

        /**
         * bounds, derived
         */
        protected BoundingBox bounds;

        GeometryAttribut(int index){
            super(index);
        }

        @Override
        public GeometryType getType() {
            return getDescriptor().getType();
        }

        @Override
        public GeometryDescriptor getDescriptor() {
            return (GeometryDescriptor) super.getDescriptor();
        }

        @Override
        public BoundingBox getBounds() {
            if (bounds == null) {
                final JTSEnvelope2D bbox = new JTSEnvelope2D(getType().getCoordinateReferenceSystem());
                final Geometry geom = (Geometry) getValue();
                if (geom != null) {
                    bbox.expandToInclude(geom.getEnvelopeInternal());
                } else {
                    bbox.setToNull();
                }
                bounds = bbox;
            }
            return bounds;
        }

        @Override
        public void setBounds(BoundingBox bbox) {
            bounds = bbox;
        }

    }




}
