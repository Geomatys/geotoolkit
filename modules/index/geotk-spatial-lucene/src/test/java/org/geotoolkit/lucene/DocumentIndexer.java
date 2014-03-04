
package org.geotoolkit.lucene;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.geotoolkit.lucene.DocumentIndexer.DocumentEnvelope;
import org.geotoolkit.lucene.index.AbstractIndexer;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DocumentIndexer extends AbstractIndexer<DocumentEnvelope>{

    private final List<DocumentEnvelope> docs;

    public DocumentIndexer(final File directory, final List<DocumentEnvelope> docs, final Analyzer analyzer) {
        super("", directory, analyzer);
        this.docs = docs;
    }
    
    @Override
    protected Collection<String> getAllIdentifiers() throws IndexingException {
        final List<String> ids = new ArrayList<>();
        for (DocumentEnvelope doc : docs) {
            ids.add(doc.doc.get("id"));
        }
        return ids;
    }

    @Override
    protected Iterator<String> getIdentifierIterator() throws IndexingException {
        final Collection<String> ids = getAllIdentifiers();
        return ids.iterator();
    }

    @Override
    protected DocumentEnvelope getEntry(String identifier) throws IndexingException {
        for (DocumentEnvelope doc : docs) {
            if (doc.doc.get("id").equals(identifier))  {
                return doc;
            }
        }
        return null;
    }

    @Override
    protected String getIdentifier(DocumentEnvelope doc) {
        return doc.doc.get("id");
    }

    @Override
    protected Document createDocument(DocumentEnvelope object, int docId) throws IndexingException {
        if (object.env != null) {
            try {
                rTree.insert(object.env);
            } catch (StoreIndexException ex) {
                throw new IndexingException("Unable to insert BBOX for document", ex);
            }
        }
        return object.doc;
    }

    @Override
    protected Iterator<DocumentEnvelope> getEntryIterator() throws IndexingException {
        throw new UnsupportedOperationException("Not supported byt this implementation.");
    }

    @Override
    protected boolean useEntryIterator() {
        return false;
    }

    public static class DocumentEnvelope {
        public NamedEnvelope env;
        public Document doc;

        public DocumentEnvelope(Document doc, NamedEnvelope env) {
            this.doc = doc;
            this.env = env;

        }
    }
}
