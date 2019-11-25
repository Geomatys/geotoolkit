/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.util.Version;
import org.opengis.filter.sort.SortOrder;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractSTSRequest implements STSRequest {

    /**
     * The $filter system query option allows clients to filter a collection of entities that are addressed by a request URL.
     * The expression specified with $filter is evaluated for each entity in the collection, and only items where the expression evaluates to true SHALL be included in the response.
     * Entities for which the expression evaluates to false or to null, or which reference properties that are unavailable due to permissions, SHALL be omitted from the response.
     *
     * The expression language that is used in $filter operators SHALL support references to properties and literals.
     * The literal values SHALL be strings enclosed in single quotes, numbers and boolean values (true or false) or datetime values represented as ISO 8601 time string.
     */
    protected String filter;
    /**
     * The $count system query option with a value of true specifies that the total count of items within a collection matching the request SHALL be returned along with the result.
     * A $count query option with a value of false (or not specified) hints that the service SHALL not return a count.
     *
     * The service SHALL return an HTTP Status code of 400 Bad Request if a value other than true or false is specified.
     *
     * The $count system query option SHALL ignore any $top, $skip, or $expand query options,
     * and SHALL return the total count of results across all pages including only those results matching any specified $filter.
     *
     * Clients should be aware that the count returned inline may not exactly equal the actual number of items returned,
     * due to latency between calculating the count and enumerating the last value or due to inexact calculations on the service.
     */
    protected Boolean count;

    /**
     * The $orderby system query option specifies the order in which items are returned from the service.
     * The value of the $orderby system query option SHALL contain a comma-separated list of expressions whose primitive result values are used to sort the items.
     * A special case of such an expression is a property path terminating on a primitive property.
     * A type cast using the qualified entity type name SHALL be ordered by a property defined on a derived type.
     *
     * The expression MAY include the suffix asc for ascending or desc for descending, separated from the property name by one or more spaces.
     * If asc or desc is not specified, the service SHALL order by the specified property in ascending order.
     *
     * Null values SHALL come before non-null values when sorting in ascending order and after non-null values when sorting in descending order.
     *
     * Items SHALL be sorted by the result values of the first expression,
     * and then items with the same value for the first expression SHALL be sorted by the result value of the second expression, and so on.
     */
    protected Map<String, SortOrder> orderby;

    /**
     * The $skip system query option specifies the number for the items of the queried collection that SHALL be excluded from the result.
     * The value of $skip system query option SHALL be a non-negative integer n. The service SHALL return items starting at position n+1.
     *
     * Where $top and $skip are used together, $skip SHALL be applied before $top, regardless of the order in which they appear in the request.
     *
     * If no unique ordering is imposed through an $orderby query option, the service SHALL impose a stable ordering across requests that include $skip.
     */
    protected Integer skip;

    /**
     * The $top system query option specifies the limit on the number of items returned from a collection of entities.
     * The value of the $top system query option SHALL be a non-negative integer n.
     * The service SHALL return the number of available items up to but not greater than the specified value n.
     *
     * If no unique ordering is imposed through an $orderby query option, the service SHALL impose a stable ordering across requests that include $top.
     *
     *
     * In addition, if the $top value exceeds the service-driven pagination limitation (i.e., the largest number of entities the service can return in a single response),
     * the $top query option SHALL be discarded and the server-side pagination limitation SHALL be imposed.
     */
    protected Integer top;


    /**
     * The $expand system query option indicates the related entities to be represented inline.
     * The value of the $expand query option SHALL be a comma separated list of navigation property names.
     * Additionally, each navigation property can be followed by a forward slash and another navigation property to enable identifying a multi-level relationship.
     */
    protected List<String> expand;

    /**
     * The $select system query option requests the service to return only the properties explicitly requested by the client.
     * The value of a $select query option SHALL be a comma-separated list of selection clauses.
     * Each selection clause SHALL be a property name (including navigation property names).
     * In the response, the service SHALL return the specified content, if available, along with any available expanded navigation properties.
     */
    protected List<String> select;

    protected String resultFormat;

    @JsonIgnore
    protected Map<String, String> extraFilter = new HashMap<>();

    @JsonIgnore
    protected Map<String, String> extraFlag = new HashMap<>();

    /**
     * @return the filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * @return the count
     */
    public Boolean getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(Boolean count) {
        this.count = count;
    }

    /**
     * @return the orderby
     */
    public Map<String, SortOrder> getOrderby() {
        return orderby;
    }

    /**
     * @param orderby the orderby to set
     */
    public void setOrderby(Map<String, SortOrder> orderby) {
        this.orderby = orderby;
    }

    /**
     * @return the skip
     */
    public Integer getSkip() {
        return skip;
    }

    /**
     * @param skip the skip to set
     */
    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    /**
     * @return the top
     */
    public Integer getTop() {
        return top;
    }

    /**
     * @param top the top to set
     */
    public void setTop(Integer top) {
        this.top = top;
    }

    /**
     * @return the expand
     */
    @Override
    public List<String> getExpand() {
        if (expand == null) {
            expand = new ArrayList<>();
        }
        return expand;
    }

    /**
     * @param expand the expand to set
     */
    public void setExpand(List<String> expand) {
        this.expand = expand;
    }

    /**
     * @return the select
     */
    @Override
    public List<String> getSelect() {
        return select;
    }

    /**
     * @param select the select to set
     */
    public void setSelect(List<String> select) {
        this.select = select;
    }

    @Override
    public String getResultFormat() {
        return resultFormat;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    public Map<String, String> getExtraFilter() {
        if (extraFilter == null)  {
            extraFilter = new HashMap<>();
        }
        return extraFilter;
    }

    public void setExtraFilter(Map<String, String> extraFilter) {
        this.extraFilter = extraFilter;
    }

    public Map<String, String> getExtraFlag() {
        if (extraFlag == null)  {
            extraFlag = new HashMap<>();
        }
        return extraFlag;
    }

    public void setExtraFlag(Map<String, String> extraFlag) {
        this.extraFlag = extraFlag;
    }

    @Override
    public String getService() {
        return "STS";
    }

    @Override
    public void setService(String value) {
        // hard coded
    }

    @Override
    public Version getVersion() {
        return new Version("1.0.0");
    }

    @Override
    public void setVersion(String version) {
        // hard coded
    }
}
