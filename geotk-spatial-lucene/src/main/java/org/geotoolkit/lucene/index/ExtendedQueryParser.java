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
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

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
                case 'd': double startD = Double.parseDouble(part1);
                          if (!startInclusive) {
                              startD = DoublePoint.nextUp(startD);
                          }
                          double endD   = Double.parseDouble(part2);
                          if (!endInclusive) {
                              endD = DoublePoint.nextDown(endD);
                          }
                          return DoublePoint.newRangeQuery(field, startD, endD);

                case 'i': int startI = Integer.parseInt(part1);
                          if (!startInclusive) {
                              startI = Math.addExact(startI, 1);
                          }
                          int endI = Integer.parseInt(part2);
                          if (!endInclusive) {
                              endI = Math.addExact(endI, -1);
                          }
                          return IntPoint.newRangeQuery(field, startI, endI);

                case 'f': float startF = Float.parseFloat(part1);
                          if (!startInclusive) {
                              startF = FloatPoint.nextUp(startF);
                          }
                          float endF = Float.parseFloat(part2);
                          if (!endInclusive) {
                              endF = FloatPoint.nextDown(endF);
                          }
                          return FloatPoint.newRangeQuery(field, startF, endF);

                case 'l': long startL = Long.parseLong(part1);
                          if (!startInclusive) {
                              startL = Math.addExact(startL, 1);
                          }
                          long endL = Long.parseLong(part2);
                          if (!endInclusive) {
                              endL = Math.addExact(endL, -1);
                          }
                          return LongPoint.newRangeQuery(field, startL, endL);

                default: throw new IllegalArgumentException("Unexpected field type:" + field);
            }

        } else {
            // i don't know why. but it seems that the default query parsera has an issue with TermRangeQuery
            return new TermRangeQuery(field, new BytesRef(part1.getBytes()), new BytesRef(part2.getBytes()), startInclusive, endInclusive);
            //return (TermRangeQuery) super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
        }
    }
}
