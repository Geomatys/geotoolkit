/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.lucene.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.geotoolkit.index.tree.manager.SQLRtreeManager;
import org.geotoolkit.index.IndexingException;
import org.geotoolkit.index.LogicalFilterType;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.lucene.LuceneUtils;
import org.geotoolkit.index.SearchingException;
import org.geotoolkit.index.SpatialQuery;


/**
 * An Lucene index searcher. allowing to perform query on the index.
 *
 * @author Guilhem legal (Geomatys)
 * @module
 */
public class LuceneIndexSearcher extends IndexLucene {

    /**
     * This is the index searcher of Lucene.
     */
    protected IndexSearcher searcher;

    /**
     * A default Query requesting all the document
     */
    private final static Query SIMPLE_QUERY = new TermQuery(new Term("metafile", "doc"));

    /**
     * A map of cached request
     */
    private final Map<SpatialQuery, Set<String>> cachedQueries = new ConcurrentHashMap<>();

    /**
     * The maximum size of the map of queries.
     */
    private static final int MAX_CACHED_QUERIES_SIZE = 50;

    /**
     * A flag indicating if the cache system for query is enabled.
     */
    private final boolean isCacheEnabled;

    /**
     * A Map of DocID -> metadata ID .
     */
    private final Map<Integer, String> identifiers = new HashMap<>();

    /**
     * A list of numeric fields names.
     */
    private Map<String, Character> numericFields;

    /**
     * A flag indicating if all the geometry indexed are envelope.
     * if set, no JTS filter will be applied on geometry search (only R-tree search)
     */
    private final boolean envelopeOnly;

    /**
     * Build a new index searcher.
     *
     * @param configDir The configuration directory where to build the index directory.
     * @param serviceID the "ID" of the service (allow multiple index in the same directory). The value "" is allowed.
     *
     * @throws IndexingException
     */
    public LuceneIndexSearcher(final Path configDir, final String serviceID) throws IndexingException {
        this(configDir, serviceID, null, false);
    }

    /**
     * Build a new index searcher.
     *
     * @param configDir The configuration directory where to build the index directory.
     * @param serviceID the "ID" of the service (allow multiple index in the same directory). The value "" is allowed.
     * @param analyzer  A lucene Analyzer (Default is ClassicAnalyzer)
     *
     * @throws org.geotoolkit.lucene.IndexingException
     */
    public LuceneIndexSearcher(final Path configDir, final String serviceID, final Analyzer analyzer) throws IndexingException {
        this(configDir, serviceID, analyzer, false);
    }

    /**
     * Build a new index searcher.
     *
     * @param configDir The configuration directory where to build the index directory.
     * @param serviceID the "ID" of the service (allow multiple index in the same directory). The value "" is allowed.
     * @param analyzer  A lucene Analyzer (Default is ClassicAnalyzer)
     * @param envelopeOnly A flag indicating if all the geometry indexed are envelope.
     *
     * @throws org.geotoolkit.lucene.IndexingException
     */
    public LuceneIndexSearcher(final Path configDir, final String serviceID, final Analyzer analyzer, final boolean envelopeOnly) throws IndexingException {
        super(analyzer);
        this.envelopeOnly = envelopeOnly;
        if (envelopeOnly) {
            LOGGER.info("envelope only mode activated");
        }
        try {
            // we get the last index directory
            long maxTime = 0;
            Path currentIndexDirectory = null;
            if (configDir != null && Files.isDirectory(configDir)) {
                for (Path indexDirectory : Files.newDirectoryStream(configDir)) {
                    String suffix = indexDirectory.getFileName().toString();
                    suffix = suffix.substring(suffix.lastIndexOf('-') + 1);
                    try {
                        long currentTime = Long.parseLong(suffix);
                        if (currentTime > maxTime) {
                            maxTime = currentTime;
                            currentIndexDirectory = indexDirectory;
                        }
                    } catch (NumberFormatException ex) {
                        LOGGER.log(Level.WARNING, "Unable to parse the timestamp:{0}", suffix);
                    }
                }
            }
            if (currentIndexDirectory != null && Files.exists(currentIndexDirectory)) {
                setFileDirectory(currentIndexDirectory);
                try {
                    this.numericFields          = new HashMap<>();
                    final Path numericFieldFile = currentIndexDirectory.resolve("numericFields.properties");
                    if (Files.isRegularFile(numericFieldFile)) {
                        final Properties prop = IOUtilities.getPropertiesFromFile(numericFieldFile);
                        for (String fieldName : prop.stringPropertyNames()) {
                            this.numericFields.put(fieldName, ((String) prop.get(fieldName)).charAt(0));
                        }
                    }
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "IO exception while reading numericFields file", ex);
                }
             } else {
                throw new IndexingException("The index searcher can't find a index directory.");
            }
            isCacheEnabled        = true;
            initSearcher();
            initIdentifiersList();

        } catch (CorruptIndexException ex) {
            throw new IndexingException("Corruption encountered during index searcher creation", ex);
        } catch (IOException ex) {
            throw new IndexingException("IO Exception during index searcher creation", ex);
        }

    }

    /**
     * initialize the IndexSearcher of this index.
     */
    private void initSearcher() throws CorruptIndexException, IOException {
        final Path indexDirectory = getFileDirectory();
        this.rTree = SQLRtreeManager.get(indexDirectory, this);
        final IndexReader reader  = DirectoryReader.open(LuceneUtils.getAppropriateDirectory(indexDirectory));
        searcher                  = new IndexSearcher(reader);
        LOGGER.log(Level.INFO, "Creating new Index Searcher with index directory:{0}", indexDirectory.toString());

    }

    /**
     * Fill the list of identifiers ordered by doc ID
     */
    private void initIdentifiersList() throws IOException {
        final Map<Integer, String> temp = new HashMap<>();
        final int nbValidDoc = searcher.getIndexReader().numDocs(); // do not take in count deleted document
        long nbDoc = 0;
        CollectionStatistics cs = searcher.collectionStatistics("id"); // contains deleted document
        if (cs != null) {
           nbDoc= cs.maxDoc();
        }
        for (int i = 0; i < nbDoc; i++) {
            final String metadataID = getMatchingID(searcher.doc(i));
            temp.put(i, metadataID);
        }
        identifiers.clear();
        identifiers.putAll(temp);
        LOGGER.log(logLevel, "{0} records found.", nbValidDoc);
    }

    /**
     * Refresh the searcher (must be call after deleting document from the index for example)
     *
     * @throws IndexingException
     */
    public void refresh() throws IndexingException {
        try {
            initSearcher();
            initIdentifiersList();
            cachedQueries.clear();
            LOGGER.log(logLevel, "refreshing index searcher");
        } catch (CorruptIndexException ex) {
            throw new IndexingException("Corruption exception encountered during refreshing the index searcher", ex);
        } catch (IOException ex) {
            throw new IndexingException("IO Exception during refreshing the index searcher", ex);
        }
    }

    /**
     * Add the metadata id to the list of result if its present in the identifiers.
     * @param results
     * @param docID
     */
    private void addToResult(final Set<String> results, final int docID) {
        final String metadataID = identifiers.get(docID);
        if (metadataID != null) {
            results.add(metadataID);
        } else {
            LOGGER.log(Level.WARNING, "Unable to find a metadata ID for doc :{0}", docID);
        }
    }

    /**
     * This method proceed a lucene search and to verify that the identifier exist.
     * If it exist it return the database ID.
     *
     * @param id A simple Term query on "identifier field".
     *
     * @return A database id.
     * @throws SearchingException
     */
    public String identifierQuery(final String id) throws SearchingException {
        try {
            final String idField = getIdentifierSearchField();
            final TermQuery query = new TermQuery(new Term(idField, id));
            final Set<String> results = new LinkedHashSet<>();
            final TopDocs hits = searcher.search(query, 2);
            for (ScoreDoc doc : hits.scoreDocs) {
                final Set<String> fieldsToLoad = Collections.singleton(idField);
                results.add(searcher.doc(doc.doc, fieldsToLoad).get(idField));
            }
            if (results.size() > 1) {
                LOGGER.log(Level.WARNING, "multiple record in lucene index for identifier: {0}", id);
            }
            if (!results.isEmpty()) {
                return results.iterator().next();
            }
        } catch (IOException ex) {
            throw new SearchingException("Parse Exception while performing lucene request", ex);
        }
        return null;
    }

    /**
     * Return the name of the identifier field used in the identifierQuery method.
     *
     * @return the name of the identifier field.
     */
    public String getIdentifierSearchField() {
        return "id";
    }

    public Map<String, Character> getNumericFields() {
        return numericFields;
    }

    /**
     * This method return the database ID of a matching Document
     *
     * @param doc A matching document.
     *
     * @return A database id.
     */
    public String getMatchingID(final Document doc) {
        return doc.get("id");
    }

    /**
     * This method proceed a lucene search and returns a list of ID.
     *
     * @param spatialQueryI The lucene query string with spatials filters.
     *
     * @return A List of metadata identifiers.
     * @throws SearchingException
     */
    public Set<String> doSearch(final SpatialQuery spatialQueryI) throws SearchingException {
        org.geotoolkit.lucene.filter.SpatialQuery spatialQuery = (org.geotoolkit.lucene.filter.SpatialQuery) spatialQueryI;
        try {
            final long start = System.currentTimeMillis();
            final String idField = getIdentifierSearchField();
            final Set<String> results = new LinkedHashSet<>();
            spatialQuery.applyRtreeOnQuery(rTree, envelopeOnly);

            //we look for a cached Query
            if (isCacheEnabled && cachedQueries.containsKey(spatialQuery)) {
                final Set<String> cachedResults = cachedQueries.get(spatialQuery);
                LOGGER.log(logLevel, "returning result from cache ({0} matching documents)", results.size());
                return cachedResults;
            }

            int maxRecords = 0;
            CollectionStatistics cs = searcher.collectionStatistics(idField); // contains deleted document
            if (cs != null) {
               maxRecords = (int) cs.maxDoc();
            }
            if (maxRecords == 0) {
                LOGGER.warning("The index seems to be empty.");
                maxRecords = 1;
            }

            final String field       = "title";
            String stringQuery       = spatialQuery.getTextQuery();

            final Query query;
            if (stringQuery != null && !stringQuery.isEmpty()) {

                final QueryParser parser = new ExtendedQueryParser(field, analyzer, numericFields);
                parser.setDefaultOperator(Operator.AND);

                // remove term:* query
                stringQuery = removeOnlyWildchar(stringQuery);

                // escape '/' character
                stringQuery = stringQuery.replace("/", "\\/");

                // we enable the leading wildcard mode if the first character of the query is a '*'
                if (stringQuery.contains(":*") || stringQuery.contains(":?") || stringQuery.contains(":(*")
                 || stringQuery.contains(":(+*") || stringQuery.contains(":+*")) {
                    parser.setAllowLeadingWildcard(true);
                    LOGGER.log(Level.FINER, "Allowing leading wildChar");
                    BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
                }

                //we set off the mecanism setting all the character to lower case
                /* we do that for range queries only for now. TODO see if we need to set it every time
                if (stringQuery.contains(" TO ")) {
                    parser.setLowercaseExpandedTerms(false);
                }*/
                query = parser.parse(stringQuery);
            } else {
                query = null;
            }

            final Query filter = spatialQuery.getQuery();
            final LogicalFilterType operator  = spatialQuery.getLogicalOperator();
            final Sort sort     = spatialQuery.getSort();
            String sorted = "";
            if (sort != null) {
                sorted = "\norder by: " + sort.toString();
            }
            String f = "";
            if (filter != null) {
                f = '\n' + filter.toString();
            }
            String operatorValue = "";
            if (!(operator == LogicalFilterType.AND || (operator == LogicalFilterType.OR && filter == null))) {
                operatorValue = '\n' + operator.name();
            }
            LOGGER.log(logLevel, "Searching for: " + (query != null ? query.toString(field) : "") + operatorValue +  f + sorted + "\nmax records: " + maxRecords);

            final List<org.geotoolkit.lucene.filter.SpatialQuery> subQueries = spatialQuery.getSubQueries();

            if (filter != null || query != null) {
                // query with an AND
                if (operator == LogicalFilterType.AND || (operator == LogicalFilterType.OR && filter == null)) {
                    Query singleQuery;
                    if (filter != null && query != null) {
                        singleQuery = new BooleanQuery.Builder()
                                        .add(filter, BooleanClause.Occur.MUST)
                                        .add(query,  BooleanClause.Occur.MUST)
                                        .build();
                    }  else if (filter != null && query == null) {
                        singleQuery = filter;
                    } else {
                        singleQuery = query;
                    }
                    final TopDocs docs;
                    if (sort != null) {
                        docs = searcher.search(singleQuery, maxRecords, sort);
                    } else {
                        docs = searcher.search(singleQuery, maxRecords);
                    }
                    for (ScoreDoc doc : docs.scoreDocs) {
                        addToResult(results, doc.doc);
                    }

                // query with an OR
                } else if (operator == LogicalFilterType.OR) {
                    final TopDocs docs;
                    Query singleQuery;
                    if (filter != null && query != null) {
                        singleQuery = new BooleanQuery.Builder()
                                        .add(filter, BooleanClause.Occur.SHOULD)
                                        .add(query,  BooleanClause.Occur.SHOULD)
                                        .build();
                    }  else if (filter != null && query == null) {
                        singleQuery = filter;
                    } else {
                        singleQuery = query;
                    }
                    if (sort != null) {
                        docs = searcher.search(singleQuery, maxRecords, sort);
                    } else {
                        docs = searcher.search(singleQuery, maxRecords);
                    }
                    for (ScoreDoc doc : docs.scoreDocs) {
                        addToResult(results, doc.doc);
                    }

                // for a NOT we need to perform many request
                } else if (operator == LogicalFilterType.NOT) {
                    Query singleQuery;
                    if (filter != null && query != null) {
                        singleQuery = new BooleanQuery.Builder()
                                        .add(filter,        BooleanClause.Occur.MUST_NOT)
                                        .add(query,         BooleanClause.Occur.MUST_NOT)
                                        .add(SIMPLE_QUERY,  BooleanClause.Occur.MUST)
                                        .build();
                    }  else if (filter != null && query == null) {
                        singleQuery = new BooleanQuery.Builder()
                                        .add(filter,        BooleanClause.Occur.MUST_NOT)
                                        .add(SIMPLE_QUERY,  BooleanClause.Occur.MUST)
                                        .build();
                    } else {
                        singleQuery = new BooleanQuery.Builder()
                                        .add(query,         BooleanClause.Occur.MUST_NOT)
                                        .add(SIMPLE_QUERY,  BooleanClause.Occur.MUST)
                                        .build();
                    }

                    final TopDocs docs;
                    if (sort != null) {
                        docs = searcher.search(singleQuery, maxRecords, sort);
                    } else {
                        docs = searcher.search(singleQuery, maxRecords);
                    }
                    for (ScoreDoc doc : docs.scoreDocs) {
                        addToResult(results, doc.doc);
                    }

                } else {
                    throw new IllegalArgumentException("unsupported logical Operator");
                }
            } else if (!subQueries.isEmpty() && operator == LogicalFilterType.NOT) {
                final TopDocs docs;
                if (sort != null) {
                    docs = searcher.search(SIMPLE_QUERY, maxRecords, sort);
                } else {
                    docs = searcher.search(SIMPLE_QUERY, maxRecords);
                }
                for (ScoreDoc doc : docs.scoreDocs) {
                    addToResult(results, doc.doc);
                }
            }

            // if we have some subQueries we execute it separely and merge the result
            if (subQueries.size() > 0) {


                for (SpatialQuery sub : subQueries) {
                    final Set<String> subResults = doSearch(sub);
                    switch (operator) {
                        case AND:
                            final Set<String> toRemove = new HashSet<>();
                            for (String r : results) {
                                if (!subResults.contains(r)) {
                                    toRemove.add(r);
                                }
                            }
                            results.removeAll(toRemove);
                            break;
                        case OR:
                            results.addAll(subResults);
                            break;
                        case NOT:
                            results.removeAll(subResults);
                            break;
                        default:
                            LOGGER.warning("unimplemented case in subQuery");
                            break;
                    }
                }
            }

            //we put the query in cache
            putInCache(spatialQuery, results);

            LOGGER.log(logLevel, results.size() + " total matching documents (" + (System.currentTimeMillis() - start) + "ms)");
            return results;
        } catch (ParseException ex) {
            throw new SearchingException("Parse Exception while performing lucene request", ex);
        } catch (IOException ex) {
           throw new SearchingException("IO Exception while performing lucene request", ex);
        }
    }

    public static String removeOnlyWildchar(String s) {
        final String pattern = "[^: +\\(]*:\\* ";
        s = s.replaceAll(pattern, "metafile:doc ");

        final String pattern2 = "[^: +\\(]*:\\*$";
        s = s.replaceAll(pattern2, "metafile:doc");

        final String pattern3 = "[^: +\\(]*:[(][*][)]";
        s = s.replaceAll(pattern3, "metafile:doc");

        final String pattern4 = "[^: +\\(]*:\\*[)]";
        s = s.replaceAll(pattern4, "metafile:doc)");

        return s;
    }

    /**
     * Add a query and its results to the cache.
     * if the map has reach the maximum size the older query is removed from the cache.
     *
     * @param query a Lucene spatial query.
     * @param results A list of metadataIdentifier.
     */
    private void putInCache(final SpatialQuery query, final Set<String> results) {
        if (isCacheEnabled) {
            // if we had reach the maximum cache size we remove the first request
            if (cachedQueries.size() >= MAX_CACHED_QUERIES_SIZE) {
                cachedQueries.remove(cachedQueries.keySet().iterator().next());
            }
            cachedQueries.put(query, results);
        }
    }

    /**
     * Free the resources when closing the searcher.
     */
    @Override
    public void destroy() {
        super.destroy();
        LOGGER.info("shutting down index searcher");
        cachedQueries.clear();
    }
}
