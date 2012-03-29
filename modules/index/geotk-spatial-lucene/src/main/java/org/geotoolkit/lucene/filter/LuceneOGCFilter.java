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
package org.geotoolkit.lucene.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.DocIdBitSet;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.lucene.index.IDFieldSelector;
import org.geotoolkit.lucene.tree.NamedEnvelope;
import org.geotoolkit.lucene.tree.TreeIndexReaderWrapper;
import org.geotoolkit.measure.Units;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;

import org.opengis.filter.Filter;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.DistanceBufferOperator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Wrap an OGC filter object in a Lucene filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LuceneOGCFilter extends org.apache.lucene.search.Filter{

    public static final String GEOMETRY_FIELD_NAME     = "idx_lucene_geometry";
    public static final String IDENTIFIER_FIELD_NAME   = "id";
    public static final PropertyName GEOMETRY_PROPERTY = FactoryFinder.getFilterFactory(null).property(GEOMETRY_FIELD_NAME);
    public static final Term GEOMETRY_FIELD            = new Term(GEOMETRY_FIELD_NAME);
    public static final Term META_FIELD                = new Term("metafile", "doc");

    private static final Logger LOGGER = Logging.getLogger(LuceneOGCFilter.class);
    
    private static final FieldSelector GEOMETRY_FIELD_SELECTOR = new FieldSelector() {
        @Override
        public FieldSelectorResult accept(String fieldName) {
            if (fieldName.equals(GEOMETRY_FIELD_NAME)) {
                return FieldSelectorResult.LOAD_AND_BREAK;
            }
            return FieldSelectorResult.NO_LOAD;
        }
    };
    
    private final Filter filter;

    private LuceneOGCFilter(final Filter filter){
        this.filter = filter;
    }

    public Filter getOGCFilter(){
        return filter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DocIdSet getDocIdSet(final IndexReader reader) throws IOException {

        final List<String> treeMatching = new ArrayList<String>();
        boolean treeSearch = false;
        boolean reverse = false;
        if (reader instanceof TreeIndexReaderWrapper) {
            final Tree tree = ((TreeIndexReaderWrapper)reader).getrTree();
            final List<org.opengis.geometry.Envelope> results = new ArrayList<org.opengis.geometry.Envelope>();
            if (tree != null) {
                // debug
                final List<org.opengis.geometry.Envelope> temps = new ArrayList<org.opengis.geometry.Envelope>();
                tree.search(tree.getRoot().getBoundary(), temps);
                LOGGER.log(Level.FINE, "nb entry in tree:{0}", temps.size());
                
                if (filter instanceof DistanceBufferOperator) {
                    if (filter instanceof Beyond) {
                        reverse = true;
                    }
                    final DistanceBufferOperator sp = (DistanceBufferOperator)filter;
                    final double distance           = sp.getDistance();
                    if (sp.getExpression2() instanceof Literal) {
                        final Literal lit = (Literal) sp.getExpression2();
                        if (lit.getValue() instanceof Geometry) {
                            final Geometry geom     = (Geometry) lit.getValue();
                            final Envelope jtsBound = geom.getEnvelopeInternal();
                            
                            final String epsgCode   = SRIDGenerator.toSRS(geom.getSRID(), SRIDGenerator.Version.V1);
                            try {
                                final CoordinateReferenceSystem geomCRS = CRS.decode(epsgCode);
                                GeneralEnvelope bound = new GeneralEnvelope(geomCRS);
                                bound.setRange(0, jtsBound.getMinX(), jtsBound.getMaxX());
                                bound.setRange(1, jtsBound.getMinY(), jtsBound.getMaxY());
                               
                                // reproject to cartesian CRS
                                bound = (GeneralEnvelope) Envelopes.transform(bound, tree.getCrs());
                                
                                // add the reprojected distance
                                final String strUnit = sp.getDistanceUnits();
                                final Unit unit = Units.valueOf(strUnit);
                                final UnitConverter converter = unit.getConverterTo(tree.getCrs().getCoordinateSystem().getAxis(0).getUnit());
                                final double rdistance = converter.convert(distance);
                                final double minx = bound.getLower(0) - rdistance;
                                final double miny = bound.getLower(1) - rdistance;
                                final double maxx = bound.getUpper(0) + rdistance;
                                final double maxy = bound.getUpper(1) + rdistance;
                                bound.setRange(0, minx, maxx);
                                bound.setRange(1, miny, maxy);

                                tree.search(bound, results);
                                treeSearch = true;
                            } catch (FactoryException ex) {
                                LOGGER.log(Level.WARNING, "Factory exception while getting filter geometry crs", ex);
                            } catch (TransformException ex) {
                                LOGGER.log(Level.WARNING, "Transform exception while reprojecting filter geometry", ex);
                            }
                        } else {
                            LOGGER.log(Level.WARNING, "Not a geometry for literal:{0} (class: {1})", new Object[]{lit.getValue(), lit.getValue().getClass().getName()});
                        }
                    } else {
                        LOGGER.log(Level.WARNING, "Not a literal for spatial filter:{0}", sp.getExpression2());
                    }

                } else if (filter instanceof BinarySpatialOperator) {
                    if (filter instanceof Disjoint) {
                        reverse = true;
                    }
                    final BinarySpatialOperator sp = (BinarySpatialOperator)filter;
                    if (sp.getExpression2() instanceof Literal) {
                        final Literal lit = (Literal) sp.getExpression2();
                        if (lit.getValue() instanceof Geometry) {
                            final Geometry geom     = (Geometry) lit.getValue();
                            final Envelope jtsBound = geom.getEnvelopeInternal();
                            final String epsgCode   = SRIDGenerator.toSRS(geom.getSRID(), SRIDGenerator.Version.V1);
                            try {
                                final CoordinateReferenceSystem geomCRS = CRS.decode(epsgCode);
                                GeneralEnvelope bound = new GeneralEnvelope(geomCRS);
                                bound.setRange(0, jtsBound.getMinX(), jtsBound.getMaxX());
                                bound.setRange(1, jtsBound.getMinY(), jtsBound.getMaxY());

                                // reproject to cartesian CRS
                                bound = (GeneralEnvelope) Envelopes.transform(bound, tree.getCrs());

                                tree.search(bound, results);
                                treeSearch = true;
                            } catch (FactoryException ex) {
                                LOGGER.log(Level.WARNING, "Factory exception while getting filter geometry crs", ex);
                            } catch (TransformException ex) {
                                LOGGER.log(Level.WARNING, "Transform exception while reprojecting filter geometry", ex);
                            }
                        } else if (lit.getValue() instanceof org.opengis.geometry.Envelope){
                            final org.opengis.geometry.Envelope env =  (org.opengis.geometry.Envelope) lit.getValue();
                            try {
                                org.opengis.geometry.Envelope bound = Envelopes.transform(env, tree.getCrs());
                                tree.search(bound, results);
                                treeSearch = true;
                            } catch (TransformException ex) {
                                LOGGER.log(Level.WARNING, "Transform exception while reprojecting filter geometry", ex);
                            }
                        } else {
                            LOGGER.log(Level.WARNING, "Not a geometry for literal:{0} (class: {1})", new Object[]{lit.getValue(), lit.getValue().getClass().getName()});
                        }
                    } else {
                        LOGGER.log(Level.WARNING, "Not a literal for spatial filter:{0}", sp.getExpression2());
                    }
                } else {
                    LOGGER.log(Level.WARNING, "not a bin spatial op:{0}", filter.getClass().getName());
                }
                for (org.opengis.geometry.Envelope result : results) {
                    treeMatching.add(((NamedEnvelope) result).getName());
                }
            } else {
                LOGGER.warning("Null R-tree in spatial search");
            }
        }
        final DocIdBitSet set = new DocIdBitSet(new BitSet(reader.maxDoc()));

        final TermDocs termDocs;
        termDocs = reader.termDocs();
        termDocs.seek(META_FIELD);

        while (termDocs.next()){
            final int docId = termDocs.doc();
            boolean match = treeMatching.contains(docId + "");
            if (treeSearch && reverse && !match) {
                set.getBitSet().set(docId);
                
            } else if (!treeSearch || match) {
                final Document doc = reader.document(docId,GEOMETRY_FIELD_SELECTOR);
                if (filter.evaluate(doc)) {
                    set.getBitSet().set(docId);
                }
            }
        }

        return set;
    }

    public static LuceneOGCFilter wrap(final Filter filter){
        return new LuceneOGCFilter(filter);
    }

    @Override
    public String toString() {
        return "[LuceneOGCFilter] " + filter.toString();
    }
}
