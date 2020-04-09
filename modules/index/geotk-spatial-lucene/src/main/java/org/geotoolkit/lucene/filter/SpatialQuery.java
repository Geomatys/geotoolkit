/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007-2009, Geomatys
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.geotoolkit.index.LogicalFilterType;
import org.geotoolkit.index.tree.Tree;

import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author guilhem
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class SpatialQuery implements org.geotoolkit.index.SpatialQuery {

    /**
     * The Lucene Query added to the textual query.
     */
    private final Query query ;

    /**
     * The lucene text query to be parsed.
     */
    private StringBuilder textQuery;

    /**
     * Logical operator to apply between the spatial filter and the query
     * default operator is AND.
     */
    private final LogicalFilterType logicalOperator;

    /**
     * A list of sub-queries with have to be executed separely.
     */
    private final List<SpatialQuery> subQueries = new ArrayList<>();

    /**
     * An lucene Sort object allowing to sort the results
     */
    private Sort sort;

    /**
     * Build a new Simple Text Query.
     *
     * @param textQuery  A well-formed Lucene query.
     */
    public SpatialQuery(final String textQuery) {
        this(textQuery,null,LogicalFilterType.AND,null);
    }

    /**
     * Build a new SpatialQuery with only a Lucene Query object.
     *
     * @param query a Lucene Query object
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.util.FactoryException
     * @throws org.opengis.referencing.operation.TransformException
     */
    public SpatialQuery(final Query query) throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        this(null,query,LogicalFilterType.AND);
    }

    /**
     * Build a new Query combinating a lucene query and a lucene filter.
     *
     * @param textQuery  A well-formed Lucene query.
     * @param query A lucene Query Object
     * @param logicalOperator The logical operator to apply between the query and the spatialFilter.
     */
    public SpatialQuery(final String textQuery, final Query query, final LogicalFilterType logicalOperator) {
        this(textQuery,query,logicalOperator,null);
    }

    public SpatialQuery(final String textQuery, final LogicalFilterType logicalOperator) {
        this(textQuery,null,logicalOperator,null);
    }


    private SpatialQuery(final String textQuery, final Query query, final LogicalFilterType logicalOperator, final List<SpatialQuery> sub){
        if (textQuery != null) {
            this.textQuery = new StringBuilder();
            this.textQuery.append(textQuery);
        }
        this.query   = query;
        if (logicalOperator != null) {
            this.logicalOperator = logicalOperator;
        } else {
            this.logicalOperator = LogicalFilterType.AND;
        }
        if(sub != null){
            this.subQueries.addAll(sub);
        }
    }



    /**
     * Return the spatial filter (it can be a SerialChainFilter) to add to the lucene query.
     */
    @Override
    public Query getQuery() {
        return query;
    }

    /**
     * Return the lucene query associated with the filter.
     */
    @Override
    public String getTextQuery() {
        if (textQuery != null) {
            return textQuery.toString();
        }
        return null;
    }

    /**
     * Return the logical operator to apply between the query and the filter.
     */
    public LogicalFilterType getLogicalOperator() {
        return logicalOperator;
    }

    /**
     * Return the sort Object joinded to this Query.
     */
    @Override
    public Sort getSort() {
        return sort;
    }

    /**
     * Add a sort Object to the query
     *
     * @param sort
     */
    public void setSort(final Sort sort) {
        this.sort = sort;
        for (SpatialQuery sub: getSubQueries()) {
            sub.setSort(sort);
        }
    }

    @Override
    public void setSort(String fieldName, boolean desc, Character fieldType) {
        final SortField sf;
        if (fieldType != null) {
            switch (fieldType) {
                case 'd': sf = new SortField(fieldName, SortField.Type.DOUBLE, desc);break;
                case 'i': sf = new SortField(fieldName, SortField.Type.INT, desc);break;
                case 'f': sf = new SortField(fieldName, SortField.Type.FLOAT, desc);break;
                case 'l': sf = new SortField(fieldName, SortField.Type.LONG, desc);break;
                default : sf = new SortField(fieldName, SortField.Type.STRING, desc);break;
            }
        } else {
            sf = new SortField(fieldName, SortField.Type.STRING, desc);
        }

        final Sort sortFilter     = new Sort(sf);
        setSort(sortFilter);
    }

    /**
     * Return the subQueries joined to this query.
     */
    public List<SpatialQuery> getSubQueries() {
        return subQueries;
    }

    /**
     * Set the sub-queries list.
     *
     * @param subQueries a list of spatial queries.
     */
    public void setSubQueries(final List<SpatialQuery> subQueries) {
        this.subQueries.clear();
        this.subQueries.addAll(subQueries);
    }

    /**
     * Add a new spatial query to the list of sub-queries
     *
     * @param sq a spatial query.
     */
    public void addSubQuery(final SpatialQuery sq) {
        subQueries.add(sq);
    }

    /**
     * Set the lucene text query associated with the Query object.
     */
    public void setQuery(final String textQuery) {
        if (textQuery != null) {
            this.textQuery = new StringBuilder();
            this.textQuery.append(textQuery);
        }
    }

    /**
     * Append a piece of lucene query to the main query.
     *
     * @param s a piece of lucene query.
     */
    public void appendToTextQuery(final String s) {
        if (this.textQuery == null) {
            this.textQuery = new StringBuilder();
        }
        textQuery.append(s);
    }

    public void applyRtreeOnQuery(final Tree rTree, final boolean envelopeOnly) {
        applyRtreeOnQuery(query, rTree, envelopeOnly);
    }

    private void applyRtreeOnQuery(final Query query, final Tree rTree, final boolean envelopeOnly) {
        if (query instanceof org.geotoolkit.lucene.filter.Filter) {
            ((org.geotoolkit.lucene.filter.Filter)query).applyRtreeOnFilter(rTree, envelopeOnly);
        } else if (query instanceof BooleanQuery) {
            BooleanQuery bQuery = (BooleanQuery) query;
            for (BooleanClause clause : bQuery.clauses()) {
                applyRtreeOnQuery(clause.getQuery(), rTree, envelopeOnly);
            }
        }
    }

    /**
     * Return a String representation of the object.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[SpatialQuery]:").append('\n');

        if (query == null && !textQuery.toString().equals("") && logicalOperator == LogicalFilterType.NOT) {
            s.append("textQuery: NOT <").append(textQuery).append(">").append('\n');

        } else if (!textQuery.toString().equals("")) {
            s.append('\t').append("textQuery: |").append(textQuery.toString()).append('|').append('\n');
        }

        if (query != null && !textQuery.toString().equals("")) {
            s.append(logicalOperator.valueOf()).append('\n');
        }

        if (query != null) {
            s.append('\t').append(query).append('\n');
        }
        if (subQueries != null && subQueries.size() > 0) {
            s.append("subqueries:").append('\n');
            int i = 0;
            for (SpatialQuery sq: subQueries) {
                s.append("sub ").append(i).append(':').append(sq);
                i++;
            }
        }
        if (sort != null) {
            s.append("Sort: ").append(sort).append('\n');
        }
        return s.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SpatialQuery) {
            final SpatialQuery that = (SpatialQuery) object;

            return (this.logicalOperator ==  that.logicalOperator)          &&
                   Objects.equals(this.getTextQuery(), that.getTextQuery())       &&
                   Objects.equals(this.sort, that.sort)                   &&
                   Objects.equals(this.query, that.query) &&
                   Objects.equals(this.subQueries, that.subQueries);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.query != null ? this.query.hashCode() : 0);
        hash = 97 * hash + (this.textQuery != null ? getTextQuery().hashCode() : 0);
        hash = 97 * hash + (this.logicalOperator != null ? logicalOperator.hashCode() : 0);
        hash = 97 * hash + (this.subQueries != null ? this.subQueries.hashCode() : 0);
        hash = 97 * hash + (this.sort != null ? this.sort.hashCode() : 0);
        return hash;
    }
}
