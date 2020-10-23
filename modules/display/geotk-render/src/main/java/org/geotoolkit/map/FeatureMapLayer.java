/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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
package org.geotoolkit.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.internal.storage.query.SimpleQuery;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Range;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import static org.geotoolkit.map.MapLayer.SELECTION_FILTER_PROPERTY;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * MapLayer holding a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 * @deprecated use MapLayer interface instead
 */
@Deprecated
public final class FeatureMapLayer extends MapLayer {

    public static final String PROP_EXTRA_DIMENSIONS = "extra_dims";

    protected Id selectionFilter = null;

    private final List<DimensionDef> extraDims = new NotifiedCheckedList<DimensionDef>(DimensionDef.class) {

        @Override
        protected void notifyAdd(DimensionDef item, int index) {
            firePropertyChange(PROP_EXTRA_DIMENSIONS, null, extraDims);
        }

        @Override
        protected void notifyAdd(Collection<? extends DimensionDef> items, NumberRange<Integer> range) {
            firePropertyChange(PROP_EXTRA_DIMENSIONS, null, extraDims);
        }

        @Override
        protected void notifyChange(DimensionDef oldItem, DimensionDef newItem, int index) {
            firePropertyChange(PROP_EXTRA_DIMENSIONS, null, extraDims);
        }

        @Override
        protected void notifyRemove(DimensionDef item, int index) {
            firePropertyChange(PROP_EXTRA_DIMENSIONS, null, extraDims);
        }

        @Override
        protected void notifyRemove(Collection<? extends DimensionDef> items, NumberRange<Integer> range) {
            firePropertyChange(PROP_EXTRA_DIMENSIONS, null, extraDims);
        }
    };

    /**
     * Creates a new instance of DefaultFeatureMapLayer
     *
     * @param collection : the data source for this layer
     * @param style : the style used to represent this layer
     */
    FeatureMapLayer(final FeatureSet collection, final MutableStyle style) {
        super(collection);
        ArgumentChecks.ensureNonNull("FeatureSet", collection);
        setStyle(style);
        trySetName(collection);
    }

    /**
     * A separate filter for datas that are selected on this layer.
     * @return Filter, can be null or empty.
     */
    public Id getSelectionFilter(){
        return selectionFilter;
    }

    /**
     * Set the selection filter.
     * @param filter Id
     */
    public void setSelectionFilter(final Id filter){

        final Filter oldfilter;
        synchronized (this) {
            oldfilter = this.selectionFilter;
            if(Objects.equals(oldfilter, filter)){
                return;
            }
            this.selectionFilter = filter;
        }
        firePropertyChange(SELECTION_FILTER_PROPERTY, oldfilter, this.selectionFilter);
    }

    /**
     * The feature collection of this layer.
     *
     * @return The features for this layer, can not be null.
     */
    @Override
    public FeatureSet getResource() {
        return (FeatureSet) resource;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        final FeatureSet featureSet = getResource();
        CoordinateReferenceSystem sourceCrs = null;
        Envelope env = null;
        try {
            sourceCrs = FeatureExt.getCRS(featureSet.getType());
            env = FeatureStoreUtilities.getEnvelope(featureSet);
        } catch (DataStoreException e) {
            LOGGER.log(Level.WARNING, "Could not create referecenced envelope.",e);
        }

        if(env == null){
            //no data
            //never return a null envelope, we better return an infinite envelope
            env = new Envelope2D(sourceCrs,Double.NaN,Double.NaN,Double.NaN,Double.NaN);

//            Envelope crsEnv = CRS.getEnvelope(sourceCrs);
//            if(crsEnv != null){
//                //we couldn't estime the features envelope, return the crs envelope if possible
//                //we assume the features are not out of the crs valide envelope
//                env = new GeneralEnvelope(crsEnv);
//            }else{
//                //never return a null envelope, we better return an infinite envelope
//                env = new Envelope2D(sourceCrs,Double.NaN,Double.NaN,Double.NaN,Double.NaN);
//            }
        }

        return env;
    }

    /**
     * Manage extra dimensions.
     *
     * @return live list of dimensiondef, never null.
     */
    public List<DimensionDef> getExtraDimensions() {
        return extraDims;
    }

    /**
     * Get all values of given extra dimension.
     * @return collection never null, can be empty.
     */
    public Collection<Range> getDimensionRange(final DimensionDef def) throws DataStoreException {
        final Expression lower = def.getLower();
        final Expression upper = def.getUpper();

        final Set<String> properties = new HashSet<>();
        lower.accept(ListingPropertyVisitor.VISITOR, properties);
        upper.accept(ListingPropertyVisitor.VISITOR, properties);

        final SimpleQuery qb = new SimpleQuery();
        final List<SimpleQuery.Column> columns = new ArrayList<>();
        final FilterFactory ff = DefaultFactories.forBuildin(FilterFactory.class);
        for (String property : properties) {
            columns.add(new SimpleQuery.Column(ff.property(property)));
        }
        qb.setColumns(columns.toArray(new SimpleQuery.Column[0]));
        final FeatureSet col = getResource().subset(qb);

        try (Stream<Feature> stream = col.features(false)) {
            return stream
                    .map(f -> {
                        return new Range(
                                Comparable.class,
                                lower.evaluate(f, Comparable.class),
                                true,
                                upper.evaluate(f, Comparable.class),
                                true
                        );
                    })
                    .collect(Collectors.toList());
        }
    }

    public static final class DimensionDef {
        private final CoordinateReferenceSystem crs;
        private final Expression lower;
        private final Expression upper;

        public DimensionDef(CoordinateReferenceSystem crs, Expression lower, Expression upper) {
            this.crs = crs;
            this.lower = lower;
            this.upper = upper;
        }

        public CoordinateReferenceSystem getCrs() {
            return crs;
        }

        public Expression getLower() {
            return lower;
        }

        public Expression getUpper() {
            return upper;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + (this.crs != null ? this.crs.hashCode() : 0);
            hash = 71 * hash + (this.lower != null ? this.lower.hashCode() : 0);
            hash = 71 * hash + (this.upper != null ? this.upper.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DimensionDef other = (DimensionDef) obj;
            if (!Utilities.equalsIgnoreMetadata(this.crs, other.crs)) {
                return false;
            }
            if (this.lower != other.lower && (this.lower == null || !this.lower.equals(other.lower))) {
                return false;
            }
            if (this.upper != other.upper && (this.upper == null || !this.upper.equals(other.upper))) {
                return false;
            }
            return true;
        }
    }
}
