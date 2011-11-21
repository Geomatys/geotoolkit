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
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;

import org.apache.lucene.util.DocIdBitSet;
import org.geotoolkit.factory.FactoryFinder;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;

/**
 * Wrap an OGC filter object in a Lucene filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LuceneOGCFilter extends org.apache.lucene.search.Filter{

    public static final String GEOMETRY_FIELD_NAME = "idx_lucene_geometry";
    public static final PropertyName GEOMETRY_PROPERTY =
            FactoryFinder.getFilterFactory(null).property(GEOMETRY_FIELD_NAME);
    public static final Term GEOMETRY_FIELD = new Term(GEOMETRY_FIELD_NAME);
    public static final Term META_FIELD = new Term("metafile", "doc");

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

        final DocIdBitSet set = new DocIdBitSet(new BitSet(reader.maxDoc()));

        final TermDocs termDocs;
        termDocs = reader.termDocs();
        termDocs.seek(META_FIELD);

        while (termDocs.next()){
            final int docId = termDocs.doc();
            final Document doc = reader.document(docId,GEOMETRY_FIELD_SELECTOR);

            if(filter.evaluate(doc)){
                set.getBitSet().set(docId);
            }
        }

        return set;
    }

    // LUCENE IS NOT THREAD SAFE !!! the same index reader seems to be used by several
    // filters, skipTo operations on termdocs brings a big mess in the iterator.
//    /**
//     * {@inheritDoc }
//     */
//    @Override
//    public DocIdSet getDocIdSet(final IndexReader reader) throws IOException {
//        final FilterIterator ite = new FilterIterator(reader);
//
//        return new DocIdSet() {
//            @Override
//            public DocIdSetIterator iterator() {
//                return ite;
//            }
//        };
//    }

    private class FilterIterator extends DocIdSetIterator{

        private final TermDocs termDocs;
        private final IndexReader reader;

        public FilterIterator(final IndexReader reader) throws IOException{
            this.reader = reader;
            this.termDocs = reader.termDocs();
            termDocs.seek(META_FIELD);
        }

        @Override
        public int docID() {
            return termDocs.doc();
        }

        @Override
        public int nextDoc() throws IOException {
            while (termDocs.next()){
                final int docId = termDocs.doc();
                final Document doc = reader.document(docId,GEOMETRY_FIELD_SELECTOR);

                if(filter.evaluate(doc)){
                    return docId;
                }
            }
            return -1;
        }

        @Override
        public int advance(final int i) throws IOException {
            boolean skip =  termDocs.skipTo(i);
            if (!skip)
                return -1;
            else
                return termDocs.doc();
        }

    }

    public static LuceneOGCFilter wrap(final Filter filter){
        return new LuceneOGCFilter(filter);
    }

    @Override
    public String toString() {
        return "[LuceneOGCFilter] " + filter.toString();
    }
}
