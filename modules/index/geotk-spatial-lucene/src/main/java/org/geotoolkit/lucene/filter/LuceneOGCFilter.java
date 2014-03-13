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
import java.util.BitSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.DocIdBitSet;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.SpatialFilterType;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.Tree;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.TreeX;
import org.geotoolkit.index.tree.manager.NamedEnvelope;

import static org.geotoolkit.lucene.LuceneUtils.*;
        
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.*;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;

/**
 * Wrap an OGC filter object in a Lucene filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LuceneOGCFilter extends org.apache.lucene.search.Filter implements  org.geotoolkit.lucene.filter.Filter {

    public static final String GEOMETRY_FIELD_NAME     = "idx_lucene_geometry";
    public static final String IDENTIFIER_FIELD_NAME   = "id";
    public static final PropertyName GEOMETRY_PROPERTY = FactoryFinder.getFilterFactory(null).property(GEOMETRY_FIELD_NAME);
    public static final Term GEOMETRY_FIELD            = new Term(GEOMETRY_FIELD_NAME);
    public static final Term META_FIELD                = new Term("metafile", "doc");

    private static final Set<String> GEOMETRY_FIELDS = new HashSet<>(1);
    static {
        GEOMETRY_FIELDS.add(GEOMETRY_FIELD_NAME);
    }

    private static final Set<String> ID_FIELDS = new HashSet<>(1);
    static {
        ID_FIELDS.add(IDENTIFIER_FIELD_NAME);
    }

    private final SpatialFilterType filterType;

    private static final Logger LOGGER = Logging.getLogger(LuceneOGCFilter.class);

    private final Filter filter;

    private Tree tree;
    
    private boolean envelopeOnly = false;
    
    private LuceneOGCFilter(final Filter filter){
        this.filter = filter;
        this.filterType = getSpatialFilterType(filter);
    }

    public Filter getOGCFilter(){
        return filter;
    }
    
    @Override
    public void applyRtreeOnFilter(final Tree rTree, final boolean envelopeOnly) {
        this.tree         = rTree;
        this.envelopeOnly = envelopeOnly;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DocIdSet getDocIdSet(final AtomicReaderContext ctx, final Bits b) throws IOException {

        boolean treeSearch     = false;
        boolean reverse        = false;
        boolean distanceFilter = false;
        final Set<String> treeMatching = new HashSet<>();
        if (tree != null) {
            /*
             * For distance buffer filter no envelope only mode
             */
            if (filter instanceof DistanceBufferOperator) {
                distanceFilter = true;
                reverse        = filter instanceof Beyond;
                final DistanceBufferOperator sp = (DistanceBufferOperator)filter;
                if (sp.getExpression2() instanceof Literal) {
                    try {
                        final Literal lit = (Literal) sp.getExpression2();
                        final GeneralEnvelope bound = getExtendedReprojectedEnvelope(lit.getValue(), tree.getCrs(), sp.getDistanceUnits(), sp.getDistance());
                        final int[] resultID = tree.searchID(bound);
                        Arrays.sort(resultID);
                        treeMatching.clear();
                        TreeElementMapper<NamedEnvelope> tem = tree.getTreeElementMapper();
                        for (int id : resultID) {
                            final NamedEnvelope env = tem.getObjectFromTreeIdentifier(id);
                            if (env != null) {
                                treeMatching.add(env.getId());
                            }
                        }
                        treeSearch = true;
                    } catch (FactoryException ex) {
                        throw new IOException(ex);
                    } catch (StoreIndexException ex) {
                        Throwable cause = ex.getCause();
                        if (cause instanceof IOException) {
                            throw (IOException) cause;
                        } else {
                            throw new IOException(ex);
                        }
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Not a literal for spatial filter:{0}", sp.getExpression2());
                }

            } else if (filter instanceof BinarySpatialOperator) {
                final BinarySpatialOperator sp = (BinarySpatialOperator)filter;
                if (sp.getExpression2() instanceof Literal) {
                    final Literal lit = (Literal) sp.getExpression2();
                    final Envelope boundFilter = getReprojectedEnvelope(lit.getValue(), tree.getCrs());
                    try {
                        if (filterType == SpatialFilterType.CROSSES || !envelopeOnly) {
                            if (filterType == SpatialFilterType.DISJOINT) {
                                reverse = true;
                            }
                            final int[] resultID = tree.searchID(boundFilter);
                            Arrays.sort(resultID);
                            final TreeElementMapper<NamedEnvelope> tem = tree.getTreeElementMapper();
                            treeMatching.clear();
                            for (int id : resultID) {
                                final NamedEnvelope env = tem.getObjectFromTreeIdentifier(id);
                                if (env != null) {
                                    treeMatching.add(env.getId());
                                }
                            }
                            treeSearch   = true;
                            envelopeOnly = false;
                        } else {
                            final int[] resultID = TreeX.search(tree, boundFilter, filterType);
                            Arrays.sort(resultID);
                            final TreeElementMapper<NamedEnvelope> tem = tree.getTreeElementMapper();
                            treeMatching.clear();
                            for (int id : resultID) {
                                final NamedEnvelope env = tem.getObjectFromTreeIdentifier(id);
                                if (env != null) {
                                    treeMatching.add(env.getId());
                                }
                            }
                            treeSearch = true;
                        }
                    } catch (StoreIndexException ex) {
                        Throwable cause = ex.getCause();
                        if (cause instanceof IOException) {
                            throw (IOException) cause;
                        } else {
                            throw new IOException(ex);
                        }
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Not a literal for spatial filter:{0}", sp.getExpression2());
                }
            } else {
                LOGGER.log(Level.WARNING, "not a spatial operator:{0}", filter.getClass().getName());
            }
        } else {
            LOGGER.finer("Null R-tree in spatial search");
        }

        final AtomicReader reader = ctx.reader();
        final DocIdBitSet set = new DocIdBitSet(new BitSet(reader.maxDoc()));
        final DocsEnum termDocs = reader.termDocsEnum(META_FIELD);
        int n = termDocs.nextDoc();
        while (n != DocsEnum.NO_MORE_DOCS){
            final int docId     = termDocs.docID();
            final Document doc  = reader.document(docId, ID_FIELDS);
            final String id     = doc.get(IDENTIFIER_FIELD_NAME);
            final boolean match = treeMatching.contains(id);
            if (treeSearch && reverse && !match) {
                set.getBitSet().set(docId);

            } else if (!treeSearch || match) {
                if (envelopeOnly && !distanceFilter) {
                    set.getBitSet().set(docId);
                } else {
                    final Document geoDoc = reader.document(docId, GEOMETRY_FIELDS);
                    if (filter.evaluate(geoDoc)) {
                        set.getBitSet().set(docId);
                    }
                }
            }
            n = termDocs.nextDoc();
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
