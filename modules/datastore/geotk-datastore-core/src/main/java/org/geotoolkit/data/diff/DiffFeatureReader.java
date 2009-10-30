/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.diff;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.data.DataSourceException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.TransactionStateDiff;

/**
 * A  FeatureReader that considers differences.
 * <p>
 * Used to implement In-Process Transaction support. This implementation will need to peek ahead in
 * order to check for deletetions.
 * </p>
 * 
 * @author Jody Garnett, Refractions Research
 * @module pending
 */
public class DiffFeatureReader<T extends FeatureType, F extends Feature> implements FeatureReader<T, F> {

    private final FeatureReader<T, F> reader;
    private final Diff diff;
    private final Filter filter;
    private final Set encounteredFids = new HashSet();
    private final boolean indexedGeometryFilter;
    private final boolean fidFilter;
    
    private Iterator<? extends Feature> addedIterator;
    private Iterator<? extends Feature> modifiedIterator;
    private Iterator<? extends Identifier> fids;
    private Iterator<? extends Feature> spatialIndexIterator;
    private F next = null;

    /**
     * This constructor grabs a "copy" of the current diff.
     * <p>
     * This reader is not "live" to changes over the course of the Transaction. (Iterators are not
     * always stable of the course of modifications)
     * </p>
     * 
     * @param reader
     * @param diff Differences of Feature by FID
     */
    public DiffFeatureReader(final FeatureReader<T, F> reader, final Diff diff) {
        this(reader, diff, Filter.INCLUDE);
    }

    /**
     * This constructor grabs a "copy" of the current diff.
     * <p>
     * This reader is not "live" to changes over the course of the Transaction. (Iterators are not
     * always stable of the course of modifications)
     * </p>
     * 
     * @param reader
     * @param diff Differences of Feature by FID
     */
    public DiffFeatureReader(final FeatureReader<T, F> reader, final Diff diff, final Filter filter) {
        this.reader = reader;
        this.diff = diff;
        this.filter = filter;

        if (filter instanceof Id) {
            fidFilter = true;
            indexedGeometryFilter = false;
        }else if(isSubsetOfBboxFilter(filter)) {
            fidFilter = false;
            indexedGeometryFilter = true;
        }else{
            fidFilter = false;
            indexedGeometryFilter = false;
        }

        synchronized (diff) {
            if (indexedGeometryFilter) {
                spatialIndexIterator = getIndexedFeatures().iterator();
            }
            addedIterator = diff.added.values().iterator();
            modifiedIterator = diff.modified2.values().iterator();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getFeatureType() {
        return reader.getFeatureType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws IOException, IllegalAttributeException, NoSuchElementException {
        if (hasNext()) {
            final F live = next;
            next = null;
            return live;
        }

        throw new NoSuchElementException("No more Feature exists");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws IOException {
        if (next != null) {
            // We found it already
            return true;
        }
        
        if (filter == Filter.EXCLUDE) {
            return false;
        }

        F peek;
        while(reader.hasNext()) {

            try {
                peek = reader.next();
            } catch (NoSuchElementException e) {
                throw new DataSourceException("Could not aquire the next Feature", e);
            } catch (IllegalAttributeException e) {
                throw new DataSourceException("Could not aquire the next Feature", e);
            }

            final String fid = peek.getIdentifier().getID();
            encounteredFids.add(fid);

            final F changed = (F) diff.modified2.get(fid);
            if(changed != null){
                //the feature is modified in the diff, use it
                if (changed == TransactionStateDiff.NULL || !filter.evaluate(changed)) {
                    //the feature has be removed in the diff or doesnt match the filter anymore
                    continue;
                } else {
                    next = changed;
                    return true;
                }
            } else {
                next = peek;
                return true;
            }
        }

        queryDiff();
        return next != null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }

        if (diff != null) {
            addedIterator = null;
        }
    }

    protected void queryDiff() {
        if (fidFilter) {
            queryFidFilter();
        } else if (indexedGeometryFilter) {
            querySpatialIndex();
        } else {
            queryAdded();
            queryModified();
        }
    }

    protected void querySpatialIndex() {
        while (spatialIndexIterator.hasNext() && next == null) {
            final F f = (F) spatialIndexIterator.next();
            if (encounteredFids.contains(f.getIdentifier().getID()) || !filter.evaluate(f)) {
                continue;
            }
            next = f;
        }
    }

    protected void queryAdded() {
        while (addedIterator.hasNext() && next == null) {
            next = (F) addedIterator.next();
            if (encounteredFids.contains(next.getIdentifier().getID()) || !filter.evaluate(next)) {
                next = null;
            }
        }
    }

    protected void queryModified() {
        while (modifiedIterator.hasNext() && next == null) {
            next = (F) modifiedIterator.next();
            if (next == TransactionStateDiff.NULL || encounteredFids.contains(next.getIdentifier().getID()) || !filter.evaluate(next)) {
                next = null;
            }
        }
    }

    protected void queryFidFilter() {
        final Id fidFilter = (Id) filter;
        if (fids == null) {
            fids = fidFilter.getIdentifiers().iterator();
        }
        while (fids.hasNext() && next == null) {
            final String fid = fids.next().toString();
            if (!encounteredFids.contains(fid)) {
                next = (F) diff.modified2.get(fid);
                if (next == null) {
                    next = (F) diff.added.get(fid);
                }
            }
        }
    }

    protected List getIndexedFeatures() {
        // TODO: check geom is default geom.
        final Envelope env = extractBboxForSpatialIndexQuery((BinarySpatialOperator) filter);
        return diff.queryIndex(env);
    }

    protected boolean isDefaultGeometry(PropertyName ae) {
        return reader.getFeatureType().getGeometryDescriptor().getLocalName().equals(ae.getPropertyName());
    }

    private static boolean isSubsetOfBboxFilter(final Filter filter) {
        return filter instanceof Contains || filter instanceof Crosses ||
                filter instanceof Overlaps || filter instanceof Touches ||
                filter instanceof Within || filter instanceof BBOX;
    }

    private static Envelope extractBboxForSpatialIndexQuery(final BinarySpatialOperator f) {
        final BinarySpatialOperator geomFilter = (BinarySpatialOperator) f;
        final Expression leftGeometry = geomFilter.getExpression1();
        final Expression rightGeometry = geomFilter.getExpression2();

        final Geometry g;
        if (leftGeometry instanceof Literal) {
            g = (Geometry) ((Literal) leftGeometry).getValue();
        } else {
            g = (Geometry) ((Literal) rightGeometry).getValue();
        }
        return g.getEnvelopeInternal();
    }
}
