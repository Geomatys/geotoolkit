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
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.Quantity;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ConstantScoreScorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.BitSetIterator;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.filter.SpatialFilterType;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.TreeX;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import static org.geotoolkit.lucene.LuceneUtils.getExtendedReprojectedEnvelope;
import static org.geotoolkit.lucene.LuceneUtils.getReprojectedEnvelope;
import static org.geotoolkit.lucene.LuceneUtils.getSpatialFilterType;
import org.opengis.filter.Literal;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.DistanceOperator;
import org.opengis.filter.DistanceOperatorName;
import org.opengis.filter.Expression;
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
    private final org.opengis.filter.Filter<Object> filter;
    private final SpatialFilterType filterType;

    private boolean envelopeOnly;

    private final ScoreMode scoreMode;
    private final float boost;

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.lucene.filter");

    public LuceneOGCWeight(Query parentQuery, final Tree tree, org.opengis.filter.Filter filter, IndexSearcher searcher, ScoreMode scoreMode, float boost, boolean envelopeOnly) {
        super(parentQuery);
        this.boost = boost;
        this.scoreMode = scoreMode;
        this.filter = filter;
        this.filterType = getSpatialFilterType(filter);
        this.tree = tree;
        this.envelopeOnly = envelopeOnly;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        // do nothing
    }

    @Override
    public Explanation explain(LeafReaderContext context, int doc) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
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
            List<Expression<Object,?>> expressions = filter.getExpressions();
            Expression e2 = (expressions.size() >= 2) ? expressions.get(1) : null;
            if (filter instanceof DistanceOperator) {
                distanceFilter = true;
                reverse        = filter.getOperatorType() == DistanceOperatorName.BEYOND;
                final DistanceOperator sp = (DistanceOperator) filter;
                if (e2 instanceof Literal) {
                    try {
                        final Literal lit = (Literal) e2;
                        Quantity distance = sp.getDistance();
                        final GeneralEnvelope bound = getExtendedReprojectedEnvelope(lit.getValue(), tree.getCrs(),
                                distance.getUnit().toString(), distance.getValue().doubleValue());
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
                    LOGGER.log(Level.WARNING, "Not a literal for spatial filter:{0}", e2);
                }
            } else if (filter instanceof BinarySpatialOperator) {
                final BinarySpatialOperator sp = (BinarySpatialOperator)filter;
                if (e2 instanceof Literal) {
                    final Literal lit = (Literal) e2;
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
                    LOGGER.log(Level.WARNING, "Not a literal for spatial filter:{0}", e2);
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
                final boolean match = treeMatching.contains(id);
                if (treeSearch && reverse && !match) {
                    set.set(docId);

                } else if (!treeSearch || match) {
                    if (envelopeOnly && !distanceFilter) {
                        set.set(docId);
                    } else {
                        final Document geoDoc = reader.document(docId, GEOMETRY_FIELDS);
                        if (filter.test(geoDoc)) {
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
