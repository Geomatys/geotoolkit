/*
 * (C) 2022, Geomatys
 */
package org.geotoolkit.storage.multires;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.storage.tiling.TileMatrix;
import org.opengis.util.GenericName;

/**
 * SortedMap of TileMatrix sorted by first axe resolution.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ScaleSortedMap<T extends TileMatrix> extends TreeMap<GenericName, T>{

    public ScaleSortedMap() {
        super(new ScaleComparator());
        comparator();
    }

    public void insertByScale(T tileMatrix) {
        final GenericName id = tileMatrix.getIdentifier();
        ArgumentChecks.ensureNonNull("identifier", id);
        final ScaleComparator comparator = (ScaleComparator) comparator();
        if (comparator.matricesByScale.containsKey(id)) {
            throw new IllegalArgumentException("Key " + id + "already exist");
        }
        final double resolution = tileMatrix.getResolution()[0];
        comparator.matricesByScale.put(id, resolution);
        super.put(id, tileMatrix);
    }

    public void removeByScale(T tileMatrix) {
        final GenericName id = tileMatrix.getIdentifier();
        ArgumentChecks.ensureNonNull("identifier", id);
        final ScaleComparator comparator = (ScaleComparator) comparator();
        if (comparator.matricesByScale.remove(id) != null) {
            super.remove(id);
        }
    }

    @Override
    public T put(GenericName key, T value) {
        throw new IllegalArgumentException("Should not be used");
    }

    @Override
    public void putAll(Map<? extends GenericName, ? extends T> map) {
        throw new IllegalArgumentException("Should not be used");
    }

    @Override
    public T putIfAbsent(GenericName key, T value) {
        throw new IllegalArgumentException("Should not be used");
    }

    @Override
    public T remove(Object key) {
        throw new IllegalArgumentException("Should not be used");
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new IllegalArgumentException("Should not be used");
    }

    /**
     * Compare TileMatrix by scale.
     * Entries are sorted from coarser resolution (highest scale denominator) to most detailed resolution (lowest scale denominator).
     */
    private static class ScaleComparator implements Comparator<GenericName> {
        private final Map<GenericName,Double> matricesByScale = new HashMap<>();

        @Override
        public int compare(GenericName o1, GenericName o2) {
            Double d1 = matricesByScale.get(o1);
            Double d2 = matricesByScale.get(o2);
            if (d1 == null) d1 = Double.NaN;
            if (d2 == null) d2 = Double.NaN;
            int v = Double.compare(d2, d1);
            if (v != 0) return v;
            //we NEED ordering, otherwise entry will be replaced.
            return o1.toString().compareTo(o2.toString());
        }
    }
}
