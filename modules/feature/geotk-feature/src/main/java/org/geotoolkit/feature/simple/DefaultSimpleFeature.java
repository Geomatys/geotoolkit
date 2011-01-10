/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geotoolkit.feature.DefaultAttribute;
import org.geotoolkit.feature.DefaultGeometryAttribute;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.filter.identity.DefaultFeatureId;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.identity.FeatureId;

/**
 * An implementation of {@link SimpleFeature} geared towards speed and backed by an Object[].
 *
 * @author Justin
 * @author Andrea Aime
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class DefaultSimpleFeature extends AbstractSimpleFeature {

    private static List<Property> toProperties(final SimpleFeatureType sft, final Object[] values){
        final int n = sft.getAttributeCount();
        final List<Property> properties = new ArrayList<Property>(n);
        for(int i=0; i<n; i++){
            final AttributeDescriptor desc = sft.getDescriptor(i);
            if(desc instanceof GeometryDescriptor){
                properties.add(new DefaultGeometryAttribute(values[i], (GeometryDescriptor) desc, null));
            }else{
                properties.add(new DefaultAttribute(values[i], desc, null));
            }
        }
        return properties;
    }

    private String strID;

    /**
     * The attribute name -> position index
     */
    private final Map<Object, Integer> index;
    /**
     * Wheter this feature is self validating or not
     */
    private final boolean validating;

    public DefaultSimpleFeature(final SimpleFeatureType featureType, final FeatureId id, final Object[] values, final boolean validating){
        this(featureType, id, toProperties(featureType,values), validating);
    }

    public DefaultSimpleFeature(final SimpleFeatureType type, final FeatureId id, final List<Property> properties, final boolean validating){
        this(new DefaultAttributeDescriptor( type, type.getName(), 1, 1, true, null),id,properties,validating);
    }

    public DefaultSimpleFeature(final AttributeDescriptor desc, final FeatureId id, final Object[] values, final boolean validating){
        this(desc, id, toProperties((SimpleFeatureType) desc.getType(),values), validating);
    }

    public DefaultSimpleFeature(final AttributeDescriptor desc, final FeatureId id, final List<Property> properties, final boolean validating){
        super(desc,id);

        // in the most common case reuse the map cached in the feature type
        if (desc.getType() instanceof DefaultSimpleFeatureType) {
            index = ((DefaultSimpleFeatureType) desc.getType()).index;
        } else {
            // if we're not lucky, rebuild the index completely...
            // TODO: create a separate cache for this case?
            this.index = DefaultSimpleFeatureType.buildIndex((SimpleFeatureType) desc.getType());
        }

        this.value = properties;
        this.validating = validating;

        // if we're self validating, do validation right now
        if (validating) {
            validate();
        }
    }


    @Override
    protected boolean isValidating() {
        return validating;
    }

    @Override
    protected Map<Object, Integer> getIndex() {
        return index;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureId getIdentifier() {
        if(id == null){
            id = new DefaultFeatureId(strID);
        }
        return id;
    }

    public void setIdentifier(final FeatureId fid){
        this.id = fid;
        this.strID = fid.getID();
    }

    public void setId(final String id){
        this.strID = id;
        this.id = null;
    }

    @Override
    public String getID() {
        if(strID != null){
            return strID;
        }else{
            return id.getID();
        }
    }

    /**
     * returns a unique code for this feature
     *
     * @return A unique int
     */
    @Override
    public int hashCode() {
        return getIdentifier().hashCode() * getType().hashCode();
    }

    /**
     * override of equals.  Returns if the passed in object is equal to this.
     *
     * @param obj the Object to test for equality.
     *
     * @return <code>true</code> if the object is equal, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DefaultSimpleFeature)) {
            return false;
        }

        final DefaultSimpleFeature feat = (DefaultSimpleFeature) obj;

        // this check shouldn't exist, by contract,
        //all features should have an ID.
        if (getIdentifier() == null) {
            if (feat.getIdentifier() != null) {
                return false;
            }
        }

        if (!getIdentifier().equals(feat.getIdentifier())) {
            return false;
        }

        if (!feat.getFeatureType().equals(getFeatureType())) {
            return false;
        }

        final List<Property> properties = getProperties();
        for (int i=0, n=properties.size(); i<n; i++) {
            Object otherAtt = feat.getAttribute(i);

            if (getProperties().get(i).getValue() == null) {
                if (otherAtt != null) {
                    return false;
                }
            } else {
                if (!properties.get(i).getValue().equals(otherAtt)) {
                    if (properties.get(i).getValue() instanceof Geometry && otherAtt instanceof Geometry) {
                        // we need to special case Geometry
                        // as JTS is broken Geometry.equals( Object )
                        // and Geometry.equals( Geometry ) are different
                        // (We should fold this knowledge into AttributeType...)
                        if (!((Geometry) properties.get(i).getValue()).equalsExact(
                                (Geometry) otherAtt)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
