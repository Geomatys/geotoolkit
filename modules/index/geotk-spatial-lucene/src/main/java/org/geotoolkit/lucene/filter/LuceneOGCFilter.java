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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import org.apache.lucene.document.Document;
//import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.geotoolkit.filter.SpatialFilterType;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.system.DefaultFactories;
import org.geotoolkit.index.tree.Tree;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.TreeX;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import static org.geotoolkit.lucene.LuceneUtils.*;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.*;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;

/**
 * Wrap an OGC filter object in a Lucene filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class LuceneOGCFilter extends org.apache.lucene.search.Query implements  org.geotoolkit.lucene.filter.Filter {

    public static final String GEOMETRY_FIELD_NAME     = "idx_lucene_geometry";
    public static final String IDENTIFIER_FIELD_NAME   = "id";
    public static final PropertyName GEOMETRY_PROPERTY = DefaultFactories.forBuildin(FilterFactory.class).property(GEOMETRY_FIELD_NAME);
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

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.lucene.filter");

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

    @Override
    public Weight createWeight(IndexSearcher searcher, ScoreMode scoreMode, float boost) throws IOException {
        return new LuceneOGCWeight(this, tree, filter, searcher, scoreMode, boost);
    }

    public static LuceneOGCFilter wrap(final Filter filter){
        return new LuceneOGCFilter(filter);
    }

    @Override
    public String toString(String s) {
        return "[LuceneOGCFilter] " + filter.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof LuceneOGCFilter) {
            LuceneOGCFilter that = (LuceneOGCFilter) obj;
            return Objects.equals(this.filter, that.filter) &&
                   Objects.equals(this.filterType, that.filterType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.filterType);
        hash = 79 * hash + Objects.hashCode(this.filter);
        return hash;
    }
}
