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

import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ExtendedQueryParser extends QueryParser {

    private final Map<String, Character> numericFields;

    public ExtendedQueryParser(final String field, final Analyzer a, final Map<String, Character> numericFields) {
        super(field, a);
        this.numericFields = numericFields;
    }

    @Override
    public Query getRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) throws ParseException {
        final Character fieldType = numericFields.get(field);
        if (fieldType != null) {
            switch (fieldType) {
                case 'd': return NumericRangeQuery.newDoubleRange(field,
                                                                  Double.parseDouble(part1),
                                                                  Double.parseDouble(part2),
                                                                  startInclusive, endInclusive);
                case 'i': return NumericRangeQuery.newIntRange(field,
                                                               Integer.parseInt(part1),
                                                               Integer.parseInt(part2),
                                                               startInclusive, endInclusive);
                case 'f': return NumericRangeQuery.newFloatRange(field,
                                                                 Float.parseFloat(part1),
                                                                 Float.parseFloat(part2),
                                                                 startInclusive, endInclusive);
                case 'l': return NumericRangeQuery.newLongRange(field,
                                                                Long.parseLong(part1),
                                                                Long.parseLong(part2),
                                                                startInclusive, endInclusive);

                default: throw new IllegalArgumentException("Unexpected field type:" + field);
            }

        } else {
            return (TermRangeQuery) super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
        }
    }
}
