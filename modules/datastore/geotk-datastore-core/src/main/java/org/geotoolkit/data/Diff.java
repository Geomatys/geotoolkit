/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class Diff {

    private final Map<String,SimpleFeature> modifiedFeatures;
    private final Map<String,SimpleFeature> addedFeatures;
    private final Object mutex;
    private SpatialIndex spatialIndex;

    /**
     * Unmodifiable view of modified features.
     * It is imperative that the user manually synchronize on the
     * map when iterating over any of its collection views:
     * <pre>
     *  Set s = diff.modified2.keySet();  // Needn't be in synchronized block
     *      ...
     *  synchronized(diff) {  // Synchronizing on diff, not diff.modified2 or s!
     *      Iterator i = s.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * Failure to follow this advice may result in non-deterministic behavior.
     *
     * <p>The returned map will be serializable if the specified map is
     * serializable.
     */
    public final Map<String,SimpleFeature> modified2;
    /**
     * Unmodifiable view of added features.
     * It is imperative that the user manually synchronize on the
     * map when iterating over any of its collection views:
     * <pre>
     *  Set s = diff.added.keySet();  // Needn't be in synchronized block
     *      ...
     *  synchronized(diff) {  // Synchronizing on m, not diff.added or s!
     *      Iterator i = s.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * Failure to follow this advice may result in non-deterministic behavior.
     *
     * <p>The returned map will be serializable if the specified map is
     * serializable.
     */
    public final Map<String,SimpleFeature> added;
    public int nextFID = 0;
    

    public Diff() {
        modifiedFeatures = new ConcurrentHashMap<String,SimpleFeature>();
        addedFeatures = new ConcurrentHashMap<String,SimpleFeature>();
        modified2 = Collections.unmodifiableMap(modifiedFeatures);
        added = Collections.unmodifiableMap(addedFeatures);
        spatialIndex = new Quadtree();
        mutex = this;
    }

    public Diff(final Diff other) {
        modifiedFeatures = Collections.synchronizedMap(new HashMap(other.modifiedFeatures));
        addedFeatures = Collections.synchronizedMap(new HashMap(other.addedFeatures));
        modified2 = Collections.unmodifiableMap(modifiedFeatures);
        added = Collections.unmodifiableMap(addedFeatures);
        spatialIndex = copySTRtreeFrom(other);
        nextFID = other.nextFID;
        mutex = this;
    }

    public boolean isEmpty() {
        synchronized (mutex) {
            return modifiedFeatures.isEmpty() && addedFeatures.isEmpty();
        }
    }

    public void add(final String fid, final SimpleFeature f) {
        synchronized (mutex) {
            addedFeatures.put(fid, f);
            addToSpatialIndex(f);
        }
    }

    private void addToSpatialIndex(final SimpleFeature f) {
        if (f.getDefaultGeometry() != null) {
            final BoundingBox bounds = f.getBounds();
            if (!bounds.isEmpty()) {
                spatialIndex.insert(JTSEnvelope2D.reference(bounds), f);
            }
        }
    }

    /*
     * Update a feature already in the diff
     */
    public void update(final String fid, final SimpleFeature f) {
        synchronized (mutex) {
            final SimpleFeature old;
            if (addedFeatures.containsKey(fid)) {
                old = addedFeatures.get(fid);
                addedFeatures.put(fid, f);
            } else {
                old = modifiedFeatures.get(fid);
                modifiedFeatures.put(fid, f);
            }
            if (old != null) {
                spatialIndex.remove(JTSEnvelope2D.reference(old.getBounds()), old);
            }
            addToSpatialIndex(f);
        }
    }

    public void remove(final String fid) {
        synchronized (mutex) {
            final SimpleFeature old;

            if (addedFeatures.containsKey(fid)) {
                old = (SimpleFeature) addedFeatures.get(fid);
                addedFeatures.remove(fid);
            } else {
                old = (SimpleFeature) modifiedFeatures.get(fid);
                modifiedFeatures.put(fid, TransactionStateDiff.NULL);
            }
            if (old != null) {
                spatialIndex.remove(JTSEnvelope2D.reference(old.getBounds()), old);
            }
        }
    }

    public void clear() {
        synchronized (mutex) {
            nextFID = 0;
            addedFeatures.clear();
            modifiedFeatures.clear();
            spatialIndex = new Quadtree();
        }
    }

    public List queryIndex(final Envelope env) {
        synchronized (mutex) {
            return spatialIndex.query(env);
        }
    }

    private Quadtree copySTRtreeFrom(final Diff diff) {
        final Quadtree tree = new Quadtree();

        synchronized (diff) {
            for(final Entry<String,SimpleFeature> entry : diff.added.entrySet()){
                final SimpleFeature f = entry.getValue();
                if (!diff.modifiedFeatures.containsKey(f.getID())) {
                    tree.insert(JTSEnvelope2D.reference(f.getBounds()), f);
                }
            }

            for(final Entry<String,SimpleFeature> entry : diff.modified2.entrySet()){
                final SimpleFeature f = (SimpleFeature) entry.getValue();
                tree.insert(JTSEnvelope2D.reference(f.getBounds()), f);
            }
        }

        return tree;
    }
}
