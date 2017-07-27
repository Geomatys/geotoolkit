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
package org.geotoolkit.processing.vector.intersect;

import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.data.memory.WrapFeatureCollection;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.data.FeatureStreams;


/**
 * FeatureCollection for Intersect process
 *
 * @author Quentin Boileau
 */
public class IntersectFeatureCollection extends WrapFeatureCollection {

    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));
    private final FeatureType newFeatureType;
    private final Geometry interGeom;

    /**
     * Connect to the original FeatureConnection with an intersection filter
     * @param originalFC FeatureCollection
     * @param interGeom
     */
    public IntersectFeatureCollection(final FeatureCollection originalFC, final Geometry interGeom) {
        super(originalFC);
        this.interGeom = interGeom;
        this.newFeatureType = super.getType();
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
     * Return the new FeatureIterator that which apply intersection filter
     * @param hints
     * @return the FeatureIterator
     * @throws FeatureStoreRuntimeException
     */
    @Override
    public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
        return FeatureStreams.filter(getOriginalFeatureCollection().iterator(null), createFilter());
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected Feature modify(final Feature original) {
        return null;
    }

    /**
     * Create an intersect filter between the intersection Geometry
     * and feature geometries
     * @return the intersect filter
     */
    private Filter createFilter() {
        final List<Filter> filterList = new ArrayList<Filter>();
        for (final PropertyType property : newFeatureType.getProperties(true)) {
            if (AttributeConvention.isGeometryAttribute(property)) {
                final Filter filter = FF.intersects(FF.property(property.getName()), FF.literal(interGeom));
                filterList.add(filter);
            }
        }
        return FF.or(filterList);
    }
}
