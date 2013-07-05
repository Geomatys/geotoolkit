/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.quality;

import java.util.List;
import java.util.Arrays;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.quality.QuantitativeResult;
import org.opengis.util.InternationalString;
import org.opengis.util.Record;
import org.opengis.util.RecordType;


/**
 * Information about the value (or set of values) obtained from applying a data quality measure.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "DQ_QuantitativeResult_Type", propOrder={
    "valueType",
    "valueUnit",
    "errorStatistic"
})
@XmlRootElement(name = "DQ_QuantitativeResult")
public class DefaultQuantitativeResult extends AbstractResult implements QuantitativeResult {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 1230713599561236060L;

    /**
     * Quantitative value or values, content determined by the evaluation procedure used.
     */
    private List<Record> values;

    /**
     * Value type for reporting a data quality result, or {@code null} if none.
     */
    private RecordType valueType;

    /**
     * Value unit for reporting a data quality result, or {@code null} if none.
     */
    private Unit<?> valueUnit;

    /**
     * Statistical method used to determine the value, or {@code null} if none.
     */
    private InternationalString errorStatistic;

    /**
     * Constructs an initially empty quantitative result.
     */
    public DefaultQuantitativeResult() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultQuantitativeResult(final QuantitativeResult source) {
        super(source);
    }

    /**
     * Constructs a quantitative result initialized to the specified value.
     *
     * @param values The quantitative values.
     */
    public DefaultQuantitativeResult(final double[] values) {
        setValues(values);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultQuantitativeResult castOrCopy(final QuantitativeResult object) {
        return (object == null) || (object instanceof DefaultQuantitativeResult)
                ? (DefaultQuantitativeResult) object : new DefaultQuantitativeResult(object);
    }

    /**
     * Returns the quantitative value or values, content determined
     * by the evaluation procedure used.
     *
     * @todo Find an implementation of {@link Record}. The one in this class is deprecated.
     */
    @Override
/// @XmlElement(name = "value", required = true)
    public synchronized List<Record> getValues() {
        return values = nonNullList(values, Record.class);
    }

    /**
     * Sets the quantitative value or values, content determined by the evaluation procedure used.
     *
     * @param newValues The new values.
     *
     * @since 2.4
     */
    public synchronized void setValues(final List<Record> newValues) {
        values = copyList(newValues, values, Record.class);
    }

    /**
     * Sets the quantitative value or values, content determined by the evaluation procedure used.
     *
     * @param newValues The new values.
     */
    public synchronized void setValues(final double[] newValues) {
        final List<Record> records;
        if (newValues == null) {
            records = null;
        } else {
            final Record[] data = new Record[newValues.length];
            for (int i=0; i<newValues.length; i++) {
                data[i] = new SimpleRecord(newValues[i]);
            }
            records = Arrays.asList(data);
        }
        setValues(records);
    }

    /**
     * Temporary record implementation will we wait for a real one.
     *
     * @deprecated To be replaced by a better implementation as soon as we can.
     */
    @Deprecated
    @SuppressWarnings("serial")
    private static final class SimpleRecord implements Record, java.io.Serializable {
        private final java.util.Map<org.opengis.util.MemberName, Object> map;

        public SimpleRecord(final double value) {
            map = java.util.Collections.singletonMap((org.opengis.util.MemberName) null, (Object) value);
        }

        @Override public RecordType getRecordType() {
            throw new UnsupportedOperationException();
        }

        @Override public java.util.Map<org.opengis.util.MemberName, Object> getAttributes() {
            return map;
        }

        @Override public Object locate(org.opengis.util.MemberName name) {
            throw new UnsupportedOperationException();
        }

        @Override public void set(org.opengis.util.MemberName name, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override public boolean equals(final Object other) {
            if (other instanceof SimpleRecord) {
                return map.equals(((SimpleRecord) other).map);
            }
            return false;
        }

        @Override public int hashCode() {
            return map.hashCode();
        }
    }

    /**
     * Return the value type for reporting a data quality result, or {@code null} if none.
     */
    @Override
    @XmlElement(name = "valueType")
    public synchronized RecordType getValueType()  {
        return valueType;
    }

    /**
     * Sets the value type for reporting a data quality result, or {@code null} if none.
     *
     * @param newValue The new value type.
     */
    public synchronized void setValueType(final RecordType newValue) {
        checkWritePermission();
        valueType = newValue;
    }

    /**
     * Returns the value unit for reporting a data quality result, or {@code null} if none.
     */
    @Override
    @XmlElement(name = "valueUnit", required = true)
    public synchronized Unit<?> getValueUnit()  {
        return valueUnit;
    }

    /**
     * Sets the value unit for reporting a data quality result, or {@code null} if none.
     *
     * @param newValue The new value unit.
     */
    public synchronized void setValueUnit(final Unit<?> newValue) {
        checkWritePermission();
        valueUnit = newValue;
    }

    /**
     * Returns the statistical method used to determine the value, or {@code null} if none.
     */
    @Override
    @XmlElement(name = "errorStatistic")
    public synchronized InternationalString getErrorStatistic()  {
        return errorStatistic;
    }

    /**
     * Sets the statistical method used to determine the value, or {@code null} if none.
     *
     * @param newValue The new error statistic.
     */
    public synchronized void setErrorStatistic(final InternationalString newValue) {
        checkWritePermission();
        errorStatistic = newValue;
    }
}
