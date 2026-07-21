/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.observation.decimation;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.decimation.NumberUtils.NumberComparator;
import org.geotoolkit.observation.model.Field;
import org.geotoolkit.observation.model.FieldDataType;
import static org.geotoolkit.observation.model.FieldDataType.QUANTITY;
import static org.geotoolkit.observation.model.FieldDataType.TIME;
import org.geotoolkit.observation.model.FieldType;
import org.geotoolkit.observation.result.ResultBuilder;
import static org.geotoolkit.observation.decimation.NumberUtils.*;

/**
 * Abstract Values accumulator used for observation measure decimation.
 * The point is to add measure values along a main field value until we reach the bounds of a slice.
 * Then we write the accumulated values (computed by a sub-implementation) and go on with the next slice.
 *
 * This class is not thread-safe.
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class ValuesAccumulator {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.observation.decimation");

    protected static final NumberComparator COMP = new NumberComparator();

    /**
     * Main values accumulator.
     */
    public final Set<Number> mainValues = new TreeSet<>(new NumberComparator());

    /**
     * lower bound of the current slice.
     */
    protected Number start;

    /**
     * length of the current slices.
     */
    protected Number step;

    /**
     * Prefix to apply to an identifier field.
     */
    protected final String identifierPrefix;

    /**
     * List of the result fields.
     */
    protected final List<Field> fields;

    /**
     * Result builder where to append the field values.
     */
    protected final ResultBuilder builder;

    /**
     * Special flag when we don't want to write the main field. This is commonly
     * used for profile datastream.
     */
    protected final boolean skipMain;

    /**
     * Special flag when we want to write only the main field. This is commonly
     * used for profile datastream.
     */
    protected final boolean onlyMain;

    /**
     * Measure counter used to forge measure identifier.
     */
    protected int cpt;

    /**
     * Build a new values accumulator.
     *
     * @param start lower bound of the current slice.
     * @param step length of the current slice.
     * @param fields List of the result fields.
     * @param identifierPrefix Prefix to apply to an identifier field.
     * @param builder Result builder where to append the field values.
     * @param skipProfileMain Special flag when we don't want to write the main field.
     * @param onlyProfileMain Special flag when we want to write only the main field.
     */
    protected ValuesAccumulator(Number start, Number step, final List<Field> fields, String identifierPrefix, ResultBuilder builder, boolean skipProfileMain, boolean onlyProfileMain) {
        this.start = start;
        this.step = step;
        this.fields = fields;
        this.identifierPrefix = identifierPrefix;
        this.builder = builder;
        this.skipMain = skipProfileMain;
        this.onlyMain = onlyProfileMain;
        this.cpt = 0;
    }

    /**
     * Start a new Observation.
     *
     * @param start lower bound of the current slice.
     * @param step length of the current slice.
     */
    public void startNewObservation(Number start, Number step) {
        this.start = start;
        this.step = step;
        mainValues.clear();
        clearValues();
    }

    protected void clearValues() {
        // do nothing on this implementation
    }

    /**
     * Append a new measure to the accumulator, for the specified field
     * (designated by its name).
     *
     * @param mainValue Main field value (like time or depth).
     * @param field Field name of the value.
     * @param value The measure to append to the accumulator.
     */
    public abstract void append(final Number mainValue, final String field, final double value);

    /**
     * Write the current slice accumulation to the result builder.
     *
     * @param first Indicate that this is the first slice we write (the purpose may be to keep full bounds).
     * @param last Indicate that this is the last slice we write (the purpose may be to keep full bounds).
     *
     * @throws DataStoreException
     */
    public void write(boolean first, boolean last) throws DataStoreException {
        write(null, first, last);
    }

    /**
     * Write the current slice accumulation to the result builder.
     *
     * @param fixedTime Indicate the timestamp value to join to the result if a time field is present (Profile case).
     * @param first Indicate that this is the first slice we write (the purpose may be to keep full bounds).
     * @param last Indicate that this is the last slice we write (the purpose may be to keep full bounds).
     *
     * @throws DataStoreException
     */
    public abstract void write(Long fixedTime, boolean first, boolean last) throws DataStoreException;

    /**
     * Return {@code true} if the specified main value is outside of the current slice.
     *
     * @param mainValue  Main field value (like time or depth).
     * @return {@code true} if the specified main value is outside of the current slice.
     */
    public boolean isAfterCurrentSlice(Number mainValue) {
        return COMP.compare(mainValue, add(start, step)) > 0;
    }

    /**
     * Start a new slice from the current main value.
     *
     * @param mainValue Main field value (like time or depth).
     */
    public void startNewSlice(Number mainValue) {
        start = getNextSliceStart(mainValue);
        mainValues.clear();
        clearValues();
    }

    /**
     * Look for the next slice start that contains the main value.
     *
     * @param mainValue Main field value (like time or depth).
     * @return the next slice start.
     */
    protected Number getNextSliceStart(Number mainValue) {
        // if no mainValue supplied return directly the next step
        if (mainValue == null) return add(start, step);
        Number next = start;
        if (COMP.compare(step, 0) > 0) {
            while (COMP.compare(mainValue, add(next, step)) >= 0) {
                next = add(next, step);
            }
        }
        return next;
    }

    /**
     * Write the supplied values into the result builder.
     *
     * @param fixedTime Fixed time value for Temporal metadata field.
     * @param cpt Measure counter value used in identifier.
     * @param mainValue Main field value (like time or depth).
     * @param fieldValues Map of Field name / mesure values to write into the result.
     *
     * @throws DataStoreException
     */
    protected void writeToResult(Long fixedTime, int cpt, Number mainValue, Map<String, Double> fieldValues) throws DataStoreException {
        builder.newBlock();
        for (Field field : fields) {

            // main field
            if (field.getType() == FieldType.MAIN) {
                switch (field.getDataType()) {
                    case TIME     -> builder.appendTime(new Date((long)mainValue), false, field);
                    case QUANTITY -> {
                        if (mainValue instanceof Long l) mainValue = l.doubleValue();
                        // special case for nonTimeseries + datastream on another phenomenon that the main field.
                        // we do not include the main field in the result
                        if (!skipMain) {
                            builder.appendDouble((double)mainValue, onlyMain, field);
                        }
                    }
                    default       -> throw new DataStoreException("main field other than Time or Quantity are not yet allowed");
                }

            // profile time field
            } else if (field.getDataType() == FieldDataType.TIME && field.getType() == FieldType.METADATA) {
                builder.appendTime(fixedTime, false, field);

            // assume that the identifier field
            } else if (field.getDataType() == FieldDataType.TEXT && field.getType() == FieldType.METADATA) {
                builder.appendString(identifierPrefix + "-dec-" + cpt, false, field);
            } else {
                final double value = fieldValues.get(field.getName());
                builder.appendDouble(value, true, field);
            }
        }
        builder.endBlock();
    }

    protected void debugPrint() {
        StringBuilder sb = new StringBuilder("values:\n");
        for (Number mv : mainValues) {
            sb.append(format(mv)).append('\n');
        }
        sb.append("inserted in step: ").append(format(start)).append(" / ").append(format(add(start, step)));
        LOGGER.info(sb.toString());
    }

    private String format(Number n) {
        if (n instanceof Double d) return Double.toString(d);
        if (n instanceof Long l)   return Instant.ofEpochMilli(l).toString();
        throw new IllegalArgumentException("Unexpected main value type (allowed are double or long)");
    }
}
