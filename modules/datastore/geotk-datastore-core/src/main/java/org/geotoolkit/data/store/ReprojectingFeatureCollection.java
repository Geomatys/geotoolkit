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
package org.geotoolkit.data.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.collection.DelegateFeatureReader;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.collection.FeatureIterator;
import org.geotoolkit.feature.collection.DecoratingFeatureCollection;
import org.geotoolkit.feature.collection.DelegateFeatureIterator;
import org.geotoolkit.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * FeatureCollection<SimpleFeatureType, SimpleFeature> decorator that reprojects the default geometry.
 * 
 * @author Justin
 * @module pending
 */
public class ReprojectingFeatureCollection extends DecoratingFeatureCollection<SimpleFeatureType, SimpleFeature> {

    /**
     * The transform to the target coordinate reference system
     */
    private final MathTransform transform;
    /**
     * The schema of reprojected features
     */
    private final SimpleFeatureType schema;
    /**
     * The target coordinate reference system
     */
    private final CoordinateReferenceSystem target;
    /**
     * Transformer used to transform geometries;
     */
    private GeometryCoordinateSequenceTransformer transformer;

    public ReprojectingFeatureCollection(FeatureCollection<SimpleFeatureType, SimpleFeature> delegate,
            CoordinateReferenceSystem target) {
        this(delegate, delegate.getSchema().getGeometryDescriptor().getCoordinateReferenceSystem(), target);
    }

    public ReprojectingFeatureCollection(
            FeatureCollection<SimpleFeatureType, SimpleFeature> delegate,
            CoordinateReferenceSystem source, CoordinateReferenceSystem target) {
        super(delegate);
        this.target = target;
        this.schema = reType(delegate.getSchema(), target);

        if (source == null) {
            throw new NullPointerException("source crs");
        }
        if (target == null) {
            throw new NullPointerException("destination crs");
        }

        this.transform = transform(source, target);
        transformer = new GeometryCoordinateSequenceTransformer();
    }

    public void setTransformer(GeometryCoordinateSequenceTransformer transformer) {
        this.transformer = transformer;
    }

    private MathTransform transform(CoordinateReferenceSystem source,
            CoordinateReferenceSystem target) {
        try {
            return CRS.findMathTransform(source, target);
        } catch (FactoryException e) {
            throw new IllegalArgumentException("Could not create math transform");
        }
    }

    private SimpleFeatureType reType(SimpleFeatureType type,
            CoordinateReferenceSystem target) {
        try {
            return FeatureTypeUtilities.transform(type, target);
        } catch (SchemaException e) {
            throw new IllegalArgumentException("Could not transform source schema", e);
        }
    }

    public FeatureReader<SimpleFeatureType, SimpleFeature> reader() throws IOException {
        return new DelegateFeatureReader<SimpleFeatureType, SimpleFeature>(getSchema(), features());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator<SimpleFeature> features() {
        return new DelegateFeatureIterator<SimpleFeature>(this, iterator());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close(FeatureIterator<SimpleFeature> close) {
        close.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator<SimpleFeature> iterator() {
        try {
            return new ReprojectingIterator(delegate.iterator(), transform, schema, transformer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close(Iterator close) {
        Iterator iterator = ((ReprojectingIterator) close).getDelegate();
        delegate.close(iterator);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getSchema() {
        return this.schema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> subCollection(Filter filter) {
        Filter unFilter = unFilter(filter);
        return new ReprojectingFeatureCollection(delegate.subCollection(unFilter), target);
        // TODO: return new delegate.subCollection( filter ).reproject( target
        // );
    }

    /**
     * Takes any literal geometry in the provided filter and backprojects it
     * 
     * @param FilterFactory
     * @param MathTransform
     */
    private Filter unFilter(Filter filter) {
        // need: filterFactory
        // need: inverse of our transform
        // FilterVisitor fv = new ReprojectingFilterVisitor(ff, transform);
        // filter.accept(fv, null);
        // TODO: create FilterVisitor that backproject literal geometry
        return filter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> sort(SortBy order) {
        // return new ReprojectingFeatureList( delegate.sort( order ), target );
        throw new UnsupportedOperationException("Not yet");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object[] toArray(Object[] a) {
        List list = new ArrayList();
        Iterator i = iterator();
        try {
            while (i.hasNext()) {
                list.add(i.next());
            }

            return list.toArray(a);
        } finally {
            close(i);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean add(SimpleFeature o) {
        // must back project any geometry attributes
        throw new UnsupportedOperationException("Not yet");
        // return delegate.add( o );
    }

    /**
     * This method computes reprojected bounds the hard way, but computing them
     * feature by feature. This method could be faster if computed the
     * reprojected bounds by reprojecting the original feature bounds a Shape
     * object, thus getting the true shape of the reprojected envelope, and then
     * computing the minimum and maximum coordinates of that new shape. The
     * result would not a true representation of the new bounds.
     */
    @Override
    public JTSEnvelope2D getBounds() {
        FeatureIterator<SimpleFeature> r = features();
        try {
            Envelope newBBox = new Envelope();
            Envelope internal;
            SimpleFeature feature;

            while (r.hasNext()) {
                feature = r.next();
                final Geometry geom = ((Geometry) feature.getDefaultGeometry());
                if (geom != null) {
                    internal = geom.getEnvelopeInternal();
                    newBBox.expandToInclude(internal);
                }
            }
            return JTSEnvelope2D.reference(newBBox);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception occurred while computing reprojected bounds", e);
        } finally {
            r.close();
        }
    }
}
