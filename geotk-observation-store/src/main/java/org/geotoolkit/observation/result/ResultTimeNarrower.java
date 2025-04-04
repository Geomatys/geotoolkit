/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014-2023 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.geotoolkit.observation.result;

import org.geotoolkit.temporal.object.ISODateParser;
import org.opengis.filter.Filter;
import org.opengis.temporal.Period;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.temporal.TemporalObjects;
import org.geotoolkit.observation.model.ComplexResult;
import org.geotoolkit.observation.model.TextEncoderProperties;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.opengis.filter.Literal;
import org.opengis.filter.TemporalOperator;
import org.opengis.filter.TemporalOperatorName;
import org.opengis.observation.Observation;
import org.opengis.temporal.Instant;
import org.opengis.temporal.TemporalPrimitive;
import org.opengis.util.CodeList;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ResultTimeNarrower {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.observation.result");


    /**
     * Apply a time constraint on a observation result.
     *
     * The time constraint is only applied if the observation has a period
     * sampling time and has a complex result. Results in the datablock that
     * will not match the time filters will be removed from it.
     *
     * @param observation An observation.
     * @param timeFilters A list of time fiters.
     */
    public static void applyTimeConstraint(final Observation observation, final List<Filter> timeFilters) {
        if (observation.getSamplingTime() instanceof Period p) {
            final Timestamp tbegin;
            final Timestamp tend;
            if (p.getBeginning() != null) {
                tbegin = new Timestamp(TemporalUtilities.toInstant(p.getBeginning()).toEpochMilli());
            } else {
                tbegin = null;
            }
            if (p.getEnding() != null) {
                tend = new Timestamp(TemporalUtilities.toInstant(p.getEnding()).toEpochMilli());
            } else {
                tend = null;
            }
            if (observation.getResult() instanceof ComplexResult cr) {
                applyTimeConstraint(tbegin, tend, cr, timeFilters);
            }
        }
    }

    @Deprecated
    public static void applyTimeConstraint(final Timestamp tBegin, final Timestamp tEnd, final ComplexResult array, final List<Filter> timeFilters) {
        final java.time.Instant begin = tBegin.toInstant();
        final java.time.Instant end = tEnd.toInstant();
        applyTimeConstraint(begin, end, array, timeFilters);
    }

    /**
     * Apply a time constraint on a complex observation result.
     *
     * Results in the datablock that will not match the time filters will be removed from it.
     *
     * @param tBegin starting bound of the observation.
     * @param tEnd ending bound of the observation.
     * @param array Complex observation result.
     * @param timeFilters A list of time fiters.
     *
     */
    public static void applyTimeConstraint(final java.time.Instant tBegin, final java.time.Instant tEnd, final ComplexResult array, final List<Filter> timeFilters) {

        if (tBegin == null || tEnd == null) return;

        Values values = new Values();
        values.values.append(array.getValues());
        values.nbBlock = array.getNbValues();

        for (Filter bound: timeFilters) {
            LOGGER.log(Level.FINER, " Values: {0}", values);
            CodeList<?> type = bound.getOperatorType();
            if (type == TemporalOperatorName.EQUALS) {
                final TemporalOperator<?> filter = (TemporalOperator) bound;
                final TemporalPrimitive time = rmLiteral(filter.getExpressions().get(1));
                if (time instanceof Instant ti) {
                    final java.time.Instant boundEquals = TemporalUtilities.toInstant(ti.getPosition());

                    LOGGER.finer("TE case 1");
                    //case 1 the periods contains a matching values
                    values = parseDataBlock(values.values.toString(), array.getTextEncodingProperties(), null, null, boundEquals);
                }

            } else if (type == TemporalOperatorName.AFTER) {
                final TemporalOperator filter = (TemporalOperator) bound;
                final TemporalPrimitive time = rmLiteral(filter.getExpressions().get(1));
                if (time instanceof Instant ti) {
                    final java.time.Instant boundBegin = TemporalUtilities.toInstant(ti.getPosition());

                    // case 1 the period overlaps the bound
                    if (tBegin.isBefore(boundBegin) && tEnd.isAfter(boundBegin)) {
                        LOGGER.finer("TA case 1");
                        values = parseDataBlock(values.values.toString(), array.getTextEncodingProperties(), boundBegin, null, null);
                    }
                }

            } else if (type == TemporalOperatorName.BEFORE) {
                final TemporalOperator filter = (TemporalOperator) bound;
                final TemporalPrimitive time = rmLiteral(filter.getExpressions().get(1));
                if (time instanceof Instant ti) {
                    final java.time.Instant boundEnd = TemporalUtilities.toInstant(ti.getPosition());

                    // case 1 the period overlaps the bound
                    if (tBegin.isBefore(boundEnd) && tEnd.isAfter(boundEnd)) {
                        LOGGER.finer("TB case 1");
                        values = parseDataBlock(values.values.toString(), array.getTextEncodingProperties(), null, boundEnd, null);
                    }
                }

            } else if (type == TemporalOperatorName.DURING) {
                final TemporalOperator filter = (TemporalOperator) bound;
                final TemporalPrimitive time = rmLiteral(filter.getExpressions().get(1));
                if (time instanceof Period tp) {
                    final java.time.Instant boundBegin = TemporalUtilities.toInstant(tp.getBeginning());
                    final java.time.Instant boundEnd   = TemporalUtilities.toInstant(tp.getEnding());

                    // case 1 the period overlaps the first bound
                    if (tBegin.isBefore(boundBegin) && tEnd.isBefore(boundEnd) && tEnd.isAfter(boundBegin)) {
                        LOGGER.finer("TD case 1");
                        values = parseDataBlock(values.values.toString(), array.getTextEncodingProperties(), boundBegin, boundEnd, null);

                    // case 2 the period overlaps the second bound
                    } else if (tBegin.isAfter(boundBegin) && tEnd.isAfter(boundEnd) && tBegin.isBefore(boundEnd)) {
                        LOGGER.finer("TD case 2");
                        values = parseDataBlock(values.values.toString(), array.getTextEncodingProperties(), boundBegin, boundEnd, null);

                    // case 3 the period totaly overlaps the bounds
                    } else if (tBegin.isBefore(boundBegin) && tEnd.isAfter(boundEnd)) {
                        LOGGER.finer("TD case 3");
                        values = parseDataBlock(values.values.toString(), array.getTextEncodingProperties(), boundBegin, boundEnd, null);
                    }
                }
            }
        }
        array.setValues(values.values.toString());
        array.setNbValues(values.nbBlock);
    }

    private static TemporalPrimitive rmLiteral(Object obj) {
        if (obj instanceof TemporalPrimitive tp) {
            return tp;
        }
        if (obj instanceof Literal<?,?> lit) {
            obj = lit.getValue();
            if (obj instanceof TemporalPrimitive tp) {
                return tp;
            }
        }
        return TemporalObjects.createInstant(TemporalUtilities.toTemporal(obj).orElseThrow(
                () -> new IllegalArgumentException("Expecting a temporal primitive in a temporal filter.")));
    }

    /**
     * Parse a data block and return only the values matching the time filter.
     *
     * @param brutValues The data block.
     * @param abstractEncoding The encoding of the data block.
     * @param boundBegin The begin bound of the time filter.
     * @param boundEnd The end bound of the time filter.
     * @param boundEquals An equals time filter (implies boundBegin and boundEnd null).
     *
     * @return a datablock containing only the matching observations.
     */
    private static Values parseDataBlock(final String brutValues, final TextEncoderProperties encoding, final java.time.Instant boundBegin, final java.time.Instant boundEnd, final java.time.Instant boundEquals) {
        final Values values = new Values();
        final String[] blocks = brutValues.split(encoding.getBlockSeparator());
        for (String block : blocks) {
            final String samplingTimeValue = block.substring(0, block.indexOf(encoding.getTokenSeparator()));
            Date d = null;
            try {
                final ISODateParser parser = new ISODateParser();
                d = parser.parseToDate(samplingTimeValue);
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.FINER, "unable to parse the value: {0}", samplingTimeValue);
            }
            if (d == null) {
                LOGGER.log(Level.WARNING, "unable to parse the value: {0}", samplingTimeValue);
                continue;
            }
            final java.time.Instant t = d.toInstant();

            // time during case
            if (boundBegin != null && boundEnd != null) {
                if (t.isAfter(boundBegin) && t.isBefore(boundEnd)) {
                    values.values.append(block).append(encoding.getBlockSeparator());
                    values.nbBlock++;
                }

            //time after case
            } else if (boundBegin != null && boundEnd == null) {
                if (t.isAfter(boundBegin)) {
                    values.values.append(block).append(encoding.getBlockSeparator());
                    values.nbBlock++;
                }

            //time before case
            } else if (boundBegin == null && boundEnd != null) {
                if (t.isBefore(boundEnd)) {
                    values.values.append(block).append(encoding.getBlockSeparator());
                    values.nbBlock++;
                }

            //time equals case
            } else if (boundEquals != null) {
                if (t.equals(boundEquals)) {
                    values.values.append(block).append(encoding.getBlockSeparator());
                    values.nbBlock++;
                }
            }
        }
        return values;
    }

    private static class Values {
        public StringBuilder values = new StringBuilder();
        public int nbBlock = 0;
    }
}
