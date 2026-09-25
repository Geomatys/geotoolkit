/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.processing.vector.regroup;

import java.util.Collection;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.memory.WrapFeatureSet;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.coordinate.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * FeatureCollection for Regroup process
 * @author Quentin Boileau
 * @module
 */
public class RegroupFeatureCollection extends WrapFeatureSet {

    private final FeatureType newFeatureType;
    private final String regroupAttribute;
    private final String geometryName;

    /**
     * Connect to the original FeatureConnection
     * @param originalFC FeatureCollection
     * @param regroupAttribute
     * @param geometryName String
     */
    public RegroupFeatureCollection(final FeatureSet originalFC, final String regroupAttribute,
            final String geometryName) throws DataStoreException {
        super(originalFC);
        this.regroupAttribute = regroupAttribute;
        this.geometryName = geometryName;
        this.newFeatureType = RegroupProcess.regroupFeatureType(originalFC.getType(), geometryName, regroupAttribute);
    }

    /**
     * Return the new FeatureType
     * @return FeatureType
     */
    @Override
    public FeatureType getType() {
        return newFeatureType;
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected Feature modify(final Feature original) {
        throw new UnsupportedOperationException("Function didn't used");
    }

    /**
     * Run the process algorithm to generate a Feature from an attribute Value
     * @param attributeValue Object
     * @return a Feature
     */
    private Feature modify2(final Object attributeValue) {
        try {
            if (attributeValue != null) {
                final FeatureSet fiteredFC = super.getOriginalFeatureCollection().subset(filter(attributeValue));
                return RegroupProcess.regroupFeature(regroupAttribute, attributeValue, newFeatureType, geometryName, fiteredFC);
            } else {
                //In this case the request is Regroup.regroupFeature(null, null, newFeatureType, geometryName, originalFC);
                return RegroupProcess.regroupFeature(regroupAttribute, attributeValue, newFeatureType, geometryName,
                        super.getOriginalFeatureCollection());
            }

        } catch (FactoryException | MismatchedDimensionException | TransformException | DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final Collection<Object> groupValues = getAttributeValues();
        return groupValues.stream().map(this::modify2);
    }

    /**
     * Call the getAttributesValues()
     * @return a Collection of Objects
     */
    private Collection<Object> getAttributeValues() {
        return RegroupProcess.getAttributeValues(regroupAttribute, super.getOriginalFeatureCollection());
    }

    private FeatureQuery filter(final Object attributeValue)
            throws FactoryException, MismatchedDimensionException, TransformException {
        final FilterFactory ff = FilterUtilities.FF;
        final Filter filter = ff.equal(ff.property(regroupAttribute), ff.literal(attributeValue));
        final FeatureQuery query = new FeatureQuery();
        query.setSelection(filter);
        return query;
    }

}
