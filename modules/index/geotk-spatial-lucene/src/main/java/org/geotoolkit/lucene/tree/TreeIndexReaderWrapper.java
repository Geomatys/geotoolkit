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
package org.geotoolkit.lucene.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.*;
import org.geotoolkit.index.tree.Tree;

/**
 * This class is used to wrap an IndexReader and a R-Tree.
 * It allow to pass th R-Tree to the LuceneOGCFilter
 * 
 * @author Guilhem Legal (Geomatys)
 */
public class TreeIndexReaderWrapper extends IndexReader {

    private final IndexReader reader;
    private final Tree rTree;
    
    public TreeIndexReaderWrapper(final IndexReader reader, final Tree rTree) {
        this.reader = reader;
        this.rTree  = rTree;
        this.readerFinishedListeners = new ArrayList<ReaderFinishedListener>();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public TermFreqVector[] getTermFreqVectors(int i) throws IOException {
        return reader.getTermFreqVectors(i);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TermFreqVector getTermFreqVector(int i, String string) throws IOException {
        return reader.getTermFreqVector(i, string);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void getTermFreqVector(int i, String string, TermVectorMapper tvm) throws IOException {
        reader.getTermFreqVector(i, string, tvm);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void getTermFreqVector(int i, TermVectorMapper tvm) throws IOException {
        reader.getTermFreqVector(i, tvm);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int numDocs() {
        return reader.numDocs();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int maxDoc() {
        return reader.maxDoc();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Document document(int i, FieldSelector fs) throws CorruptIndexException, IOException {
        return reader.document(i, fs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isDeleted(int i) {
        return reader.isDeleted(i);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasDeletions() {
        return reader.hasDeletions();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public byte[] norms(String string) throws IOException {
        return reader.norms(string);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void norms(String string, byte[] bytes, int i) throws IOException {
        reader.norms(string, bytes, i);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TermEnum terms() throws IOException {
        return reader.terms();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TermEnum terms(Term term) throws IOException {
        return reader.terms(term);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int docFreq(Term term) throws IOException {
        return reader.docFreq(term);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TermDocs termDocs() throws IOException {
        return reader.termDocs();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TermPositions termPositions() throws IOException {
        return reader.termPositions();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<String> getFieldNames(FieldOption fo) {
        return reader.getFieldNames(fo);
    }
    
    /**
     * @return the rTree
     */
    public Tree getrTree() {
        return rTree;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    protected void doSetNorm(int i, String string, byte b) throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("Not supported beacuse this implementation is only on wrapper on an existing IndexReader");
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    protected void doDelete(int i) throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("Not supported beacuse this implementation is only on wrapper on an existing IndexReader");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doUndeleteAll() throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("Not supported beacuse this implementation is only on wrapper on an existing IndexReader");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doCommit(Map<String, String> map) throws IOException {
        throw new UnsupportedOperationException("Not supported beacuse this implementation is only on wrapper on an existing IndexReader");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doClose() throws IOException {
        throw new UnsupportedOperationException("Not supported beacuse this implementation is only on wrapper on an existing IndexReader");
    }
}
