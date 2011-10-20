/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.Version;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ExtendedQueryParser extends QueryParser {

    private final Map<String, Character> numericFields;
    
    public ExtendedQueryParser(final Version matchVersion, final String field, final Analyzer a, final Map<String, Character> numericFields) {
        super(matchVersion, field, a);
        this.numericFields = numericFields;
    }

    @Override
    public Query getRangeQuery(final String field, final String part1, final String part2, final boolean inclusive) throws ParseException {
        final TermRangeQuery query = (TermRangeQuery) super.getRangeQuery(field, part1, part2, inclusive);
        final Character fieldType = numericFields.get(field);
        if (fieldType != null) {
            switch (fieldType) {
                case 'd': return NumericRangeQuery.newDoubleRange(field,
                                                                  Double.parseDouble(query.getLowerTerm()),
                                                                  Double.parseDouble(query.getUpperTerm()),
                                                                  query.includesLower(), query.includesUpper());
                case 'i': return NumericRangeQuery.newIntRange(field,
                                                               Integer.parseInt(query.getLowerTerm()),
                                                               Integer.parseInt(query.getUpperTerm()),
                                                               query.includesLower(), query.includesUpper());
                case 'f': return NumericRangeQuery.newFloatRange(field,
                                                                 Float.parseFloat(query.getLowerTerm()),
                                                                 Float.parseFloat(query.getUpperTerm()),
                                                                 query.includesLower(), query.includesUpper());
                case 'l': return NumericRangeQuery.newLongRange(field,
                                                                Long.parseLong(query.getLowerTerm()),
                                                                Long.parseLong(query.getUpperTerm()),
                                                                query.includesLower(), query.includesUpper());
                    
                default: throw new IllegalArgumentException("Unexpected field type:" + field);
            }
            
        } else {
            return query;
        }
    }
}
