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
     * The spatial filter added to the lucene query.
     */
    private final Query spatialFilter ;

    /**
     * The lucene query
     */
    private final StringBuilder query;

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
     * @param query  A well-formed Lucene query.
     */
    public SpatialQuery(final String query) {
        this(query,null,LogicalFilterType.AND,null);
    }

    /**
     * Build a new Query combinating a lucene query and a spatial filter.
     *
     * @param spatialFilter spatial filter
     *
     * @throws org.opengis.referencing.NoSuchAuthorityCodeException
     * @throws org.opengis.util.FactoryException
     * @throws org.opengis.referencing.operation.TransformException
     */
    public SpatialQuery(final LuceneOGCFilter spatialFilter) throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        this("",spatialFilter,LogicalFilterType.AND);
    }

    /**
     * Build a new Query combinating a lucene query and a lucene filter.
     *
     * @param query  A well-formed Lucene query.
     * @param filter A lucene filter (spatial, serialChain, ...)
     * @param logicalOperator The logical operator to apply between the query and the spatialFilter.
     */
    public SpatialQuery(final String query, final Query filter, final LogicalFilterType logicalOperator) {
        this(query,filter,logicalOperator,null);
    }

    public SpatialQuery(final String query, final LogicalFilterType logicalOperator) {
        this(query,null,logicalOperator,null);
    }


    private SpatialQuery(final String query, final Query filter, final LogicalFilterType logicalOperator, final List<SpatialQuery> sub){
        this.query           = new StringBuilder(query);
        this.spatialFilter   = filter;
        this.logicalOperator = logicalOperator;
        if(sub != null){
            this.subQueries.addAll(sub);
        }
    }



    /**
     * Return the spatial filter (it can be a SerialChainFilter) to add to the lucene query.
     */
    @Override
    public Query getSpatialFilter() {
        return spatialFilter;
    }

    /**
     * Return the lucene query associated with the filter.
     */
    @Override
    public String getQuery() {
        if (query == null || query.toString().equals("") || query.toString().equals(" ")) {
            return "metafile:doc";
        }
        return query.toString();
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
     * Set the lucene query associated with the filter.
     */
    public void setQuery(final String query) {
        this.query.delete(0, this.query.length()-1);
        this.query.append(query);
    }

    /**
     * Append a piece of lucene query to the main query.
     *
     * @param s a piece of lucene query.
     */
    public void appendToQuery(final String s) {
        query.append(s);
    }

    public void applyRtreeOnFilter(final Tree rTree, final boolean envelopeOnly) {
        if (spatialFilter instanceof org.geotoolkit.lucene.filter.Filter) {
            ((org.geotoolkit.lucene.filter.Filter)spatialFilter).applyRtreeOnFilter(rTree, envelopeOnly);
        }
    }

    /**
     * Return a String representation of the object.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[SpatialQuery]:").append('\n');

        if (spatialFilter == null && !query.toString().equals("") && logicalOperator == LogicalFilterType.NOT) {
            s.append("query: NOT <").append(query).append(">").append('\n');

        } else if (!query.toString().equals("")) {
            s.append('\t').append("query: |").append(query.toString()).append('|').append('\n');
        }

        if (spatialFilter != null && !query.toString().equals("")) {
            s.append(logicalOperator.valueOf()).append('\n');
        }

        if (spatialFilter != null) {
            s.append('\t').append(spatialFilter).append('\n');
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
                   Objects.equals(this.getQuery(), that.getQuery())       &&
                   Objects.equals(this.sort, that.sort)                   &&
                   Objects.equals(this.spatialFilter, that.spatialFilter) &&
                   Objects.equals(this.subQueries, that.subQueries);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.spatialFilter != null ? this.spatialFilter.hashCode() : 0);
        hash = 97 * hash + (this.query != null ? getQuery().hashCode() : 0);
        hash = 97 * hash + (this.logicalOperator != null ? logicalOperator.hashCode() : 0);
        hash = 97 * hash + (this.subQueries != null ? this.subQueries.hashCode() : 0);
        hash = 97 * hash + (this.sort != null ? this.sort.hashCode() : 0);
        return hash;
    }
}
