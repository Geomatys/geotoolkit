/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.ConstantScoreScorer;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TwoPhaseIterator;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.BitSetIterator;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.filter.SpatialFilterType;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.TreeX;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import static org.geotoolkit.lucene.LuceneUtils.getExtendedReprojectedEnvelope;
import static org.geotoolkit.lucene.LuceneUtils.getReprojectedEnvelope;
import static org.geotoolkit.lucene.LuceneUtils.getSpatialFilterType;
import static org.geotoolkit.lucene.filter.LuceneOGCFilter.GEOMETRY_FIELD_NAME;
import static org.geotoolkit.lucene.filter.LuceneOGCFilter.IDENTIFIER_FIELD_NAME;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.DistanceBufferOperator;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LuceneOGCWeight extends Weight {

    public static final String GEOMETRY_FIELD_NAME     = "idx_lucene_geometry";
    public static final String IDENTIFIER_FIELD_NAME   = "id";
    private static final Set<String> GEOMETRY_FIELDS = new HashSet<>(1);
    static {
        GEOMETRY_FIELDS.add(GEOMETRY_FIELD_NAME);
    }

    private static final Set<String> ID_FIELDS = new HashSet<>(1);
    static {
        ID_FIELDS.add(IDENTIFIER_FIELD_NAME);
    }
    private final Tree tree;
    private final org.opengis.filter.Filter filter;
    private final SpatialFilterType filterType;

    private boolean envelopeOnly = true;

    private final ScoreMode scoreMode;
    private final float boost;

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.lucene.filter");

    public LuceneOGCWeight(Query parentQuery, final Tree tree, org.opengis.filter.Filter filter, IndexSearcher searcher, ScoreMode scoreMode, float boost) {
        super(parentQuery);
        this.boost = boost;
        this.scoreMode = scoreMode;
        this.filter = filter;
        this.filterType = getSpatialFilterType(filter);
        this.tree = tree;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        for (Term t : terms) {
            System.out.println("TERM: " + t);
        }
    }

    @Override
    public Explanation explain(LeafReaderContext context, int doc) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Scorer scorer(LeafReaderContext context) throws IOException {
       //final SortedDocValues values = context.reader().getSortedDocValues(IDENTIFIER_FIELD_NAME);
        final LeafReader reader = context.reader();

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

        final BitSet set = new FixedBitSet( reader.maxDoc());

        Bits b = reader.getLiveDocs();
        if (b == null) {
            b = new Bits.MatchAllBits(reader.maxDoc());
        }
        for (int i = 0; i < b.length(); i++){
            if (b.get(i)) {
                final int docId     = i;
                final Document doc  = reader.document(docId, ID_FIELDS);
                final String id     = doc.get(IDENTIFIER_FIELD_NAME);
                System.out.println("ID:" + id);
                final boolean match = treeMatching.contains(id);
                if (treeSearch && reverse && !match) {
                    set.set(docId);

                } else if (!treeSearch || match) {
                    if (envelopeOnly && !distanceFilter) {
                        set.set(docId);
                    } else {
                        final Document geoDoc = reader.document(docId, GEOMETRY_FIELDS);
                        if (filter.evaluate(geoDoc)) {
                            set.set(docId);
                        }
                    }
                }
            }
        }

        return new ConstantScoreScorer(this, boost, scoreMode, new BitSetIterator(set, 5));
      }


    @Override
    public boolean isCacheable(LeafReaderContext ctx) {
        return true;
    }

}
