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
import java.io.IOException;
import java.io.StringWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Map.Entry;
import org.geotoolkit.io.TableWriter;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
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

    private final FeatureId id;

    private final SimpleFeatureType featureType;
    /**
     * The actual values held by this feature
     */
    private final Object[] values;
    /**
     * The attribute name -> position index
     */
    private final Map<String, Integer> index;
    /**
     * Wheter this feature is self validating or not
     */
    private final boolean validating;
    /**
     * The set of user data attached to the feature (lazily created)
     */
    private Map<Object, Object> userData;
    /**
     * The set of user data attached to each attribute (lazily created)
     */
    private Map<Object, Object>[] attributeUserData;

    /**
     * Builds a new feature based on the provided values and feature type
     * @param values
     * @param featureType
     * @param id
     */
    public DefaultSimpleFeature(final List<Object> values, final SimpleFeatureType featureType, final FeatureId id) {
        this(values.toArray(), featureType, id, false);
    }

    /**
     * Fast construction of a new feature. The object takes owneship of the provided value array,
     * do not modify after calling the constructor
     * @param values
     * @param featureType
     * @param id
     * @param validating
     */
    public DefaultSimpleFeature(final Object[] values, final SimpleFeatureType featureType, final FeatureId id,
            final boolean validating){
        this.id = id;
        this.featureType = featureType;
        this.values = values;
        this.validating = validating;

        // in the most common case reuse the map cached in the feature type
        if (featureType instanceof DefaultSimpleFeatureType) {
            index = ((DefaultSimpleFeatureType) featureType).index;
        } else {
            // if we're not lucky, rebuild the index completely...
            // TODO: create a separate cache for this case?
            this.index = DefaultSimpleFeatureType.buildIndex(featureType);
        }

        // if we're self validating, do validation right now
        if (validating) {
            validate();
        }
    }

    /**
     * Used by builder to copy more efficiently all values
     * @return Object[] of all values
     */
    @Override
    protected Object[] getValues() {
        return values;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureId getIdentifier() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getType() {
        return featureType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<Object, Object> getUserData() {
        if (userData == null) {
            userData = new HashMap<Object, Object>();
        }
        return userData;
    }

    /**
     * returns a unique code for this feature
     *
     * @return A unique int
     */
    @Override
    public int hashCode() {
        return id.hashCode() * featureType.hashCode();
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DefaultSimpleFeature)) {
            return false;
        }

        DefaultSimpleFeature feat = (DefaultSimpleFeature) obj;

        // this check shouldn't exist, by contract,
        //all features should have an ID.
        if (id == null) {
            if (feat.getIdentifier() != null) {
                return false;
            }
        }

        if (!id.equals(feat.getIdentifier())) {
            return false;
        }

        if (!feat.getFeatureType().equals(featureType)) {
            return false;
        }

        for (int i = 0, ii = values.length; i < ii; i++) {
            Object otherAtt = feat.getAttribute(i);

            if (values[i] == null) {
                if (otherAtt != null) {
                    return false;
                }
            } else {
                if (!values[i].equals(otherAtt)) {
                    if (values[i] instanceof Geometry && otherAtt instanceof Geometry) {
                        // we need to special case Geometry
                        // as JTS is broken Geometry.equals( Object )
                        // and Geometry.equals( Geometry ) are different
                        // (We should fold this knowledge into AttributeType...)
                        if (!((Geometry) values[i]).equals(
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

    @Override
    protected boolean isValidating() {
        return validating;
    }

    @Override
    protected Map<String, Integer> getIndex() {
        return index;
    }

    @Override
    protected Map<Object, Object>[] getAttributUserData() {
        if(attributeUserData == null){
            attributeUserData = new HashMap[values.length];
        }
        return attributeUserData;
    }

}
