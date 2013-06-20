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

import java.util.Date;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.Result;
import org.opengis.metadata.quality.Element;
import org.opengis.metadata.quality.Usability;
import org.opengis.metadata.quality.Completeness;
import org.opengis.metadata.quality.TemporalAccuracy;
import org.opengis.metadata.quality.ThematicAccuracy;
import org.opengis.metadata.quality.PositionalAccuracy;
import org.opengis.metadata.quality.LogicalConsistency;
import org.opengis.metadata.quality.EvaluationMethodType;
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * Type of test applied to the data specified by a data quality scope.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Guilhem Legal (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "AbstractDQ_Element_Type", propOrder={
    "namesOfMeasure",
    "measureIdentification",
    "measureDescription",
    "evaluationMethodType",
    "evaluationMethodDescription",
    "evaluationProcedure",
    "dates",
    "results"
})
@XmlRootElement(name = "DQ_Element")
@XmlSeeAlso({
    AbstractCompleteness.class,
    AbstractLogicalConsistency.class,
    AbstractPositionalAccuracy.class,
    AbstractThematicAccuracy.class,
    AbstractTemporalAccuracy.class,
    DefaultUsability.class
})
public class AbstractElement extends MetadataEntity implements Element {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3542504624077298894L;

    /**
     * Name of the test applied to the data.
     */
    private Collection<InternationalString> namesOfMeasure;

    /**
     * Code identifying a registered standard procedure, or {@code null} if none.
     */
    private Identifier measureIdentification;

    /**
     * Description of the measure being determined.
     */
    private InternationalString measureDescription;

    /**
     * Type of method used to evaluate quality of the dataset, or {@code null} if unspecified.
     */
    private EvaluationMethodType evaluationMethodType;

    /**
     * Description of the evaluation method.
     */
    private InternationalString evaluationMethodDescription;

    /**
     * Reference to the procedure information, or {@code null} if none.
     */
    private Citation evaluationProcedure;

    /**
     * Start time ({@code date1}) and end time ({@code date2}) on which a data quality measure
     * was applied. Value is {@link Long#MIN_VALUE} if this information is not available.
     */
    private long date1, date2;

    /**
     * Value (or set of values) obtained from applying a data quality measure or the out
     * come of evaluating the obtained value (or set of values) against a specified
     * acceptable conformance quality level.
     */
    private Collection<Result> results;

    /**
     * Constructs an initially empty element.
     */
    public AbstractElement() {
        date1 = Long.MIN_VALUE;
        date2 = Long.MIN_VALUE;
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public AbstractElement(final Element source) {
        super(source);
        if (source != null) {
            // Be careful to not overwrite date values (GEOTK-170).
            if (date1 == 0 && date2 == 0 && isNullOrEmpty(source.getDates())) {
                date1 = Long.MIN_VALUE;
                date2 = Long.MIN_VALUE;
            }
        }
    }

    /**
     * Creates an element initialized to the given result.
     *
     * @param result The value obtained from applying a data quality measure against a specified
     *               acceptable conformance quality level.
     */
    public AbstractElement(final Result result) {
        this(); // Initialize date fields.
        setResults(Collections.singleton(result));
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     * <p>
     * This method checks for the {@link PositionalAccuracy}, {@link TemporalAccuracy},
     * {@link ThematicAccuracy}, {@link LogicalConsistency}, {@link Completeness} and
     * {@link Usability} sub-interfaces. If one of those interfaces is found, then this method
     * delegates to the corresponding {@code castOrCopy} static method. If the given object implements
     * more than one of the above-cited interfaces, then the {@code castOrCopy} method to be used is
     * unspecified.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static AbstractElement castOrCopy(final Element object) {
        if (object instanceof PositionalAccuracy) {
            return AbstractPositionalAccuracy.castOrCopy((PositionalAccuracy) object);
        }
        if (object instanceof TemporalAccuracy) {
            return AbstractTemporalAccuracy.castOrCopy((TemporalAccuracy) object);
        }
        if (object instanceof ThematicAccuracy) {
            return AbstractThematicAccuracy.castOrCopy((ThematicAccuracy) object);
        }
        if (object instanceof LogicalConsistency) {
            return AbstractLogicalConsistency.castOrCopy((LogicalConsistency) object);
        }
        if (object instanceof Completeness) {
            return AbstractCompleteness.castOrCopy((Completeness) object);
        }
        if (object instanceof Usability) {
            return DefaultUsability.castOrCopy((Usability) object);
        }
        return (object == null) || (object instanceof AbstractElement)
                ? (AbstractElement) object : new AbstractElement(object);
    }

    /**
     * Returns the name of the test applied to the data.
     */
    @Override
    @XmlElement(name = "nameOfMeasure")
    public synchronized Collection<InternationalString> getNamesOfMeasure() {
        return namesOfMeasure = nonNullCollection(namesOfMeasure, InternationalString.class);
    }

    /**
     * Sets the name of the test applied to the data.
     *
     * @param newValues The new name of measures.
     */
    public synchronized void setNamesOfMeasure(
            final Collection<? extends InternationalString> newValues)
    {
        namesOfMeasure = copyCollection(newValues, namesOfMeasure, InternationalString.class);
    }

    /**
     * Returns the code identifying a registered standard procedure, or {@code null} if none.
     */
    @Override
    @XmlElement(name = "measureIdentification")
    public synchronized Identifier getMeasureIdentification() {
        return measureIdentification;
    }

    /**
     * Sets the code identifying a registered standard procedure.
     *
     * @param newValue The new measure identification.
     */
    public synchronized void setMeasureIdentification(final Identifier newValue)  {
        checkWritePermission();
        measureIdentification = newValue;
    }

    /**
     * Returns the description of the measure being determined.
     */
    @Override
    @XmlElement(name = "measureDescription")
    public synchronized InternationalString getMeasureDescription() {
        return measureDescription;
    }

    /**
     * Sets the description of the measure being determined.
     *
     * @param newValue The new measure description.
     */
    public synchronized void setMeasureDescription(final InternationalString newValue)  {
        checkWritePermission();
        measureDescription = newValue;
    }

    /**
     * Returns the type of method used to evaluate quality of the dataset,
     * or {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "evaluationMethodType")
    public synchronized EvaluationMethodType getEvaluationMethodType() {
        return evaluationMethodType;
    }

    /**
     * Sets the ype of method used to evaluate quality of the dataset.
     *
     * @param newValue The new evaluation method type.
     */
    public synchronized void setEvaluationMethodType(final EvaluationMethodType newValue)  {
        checkWritePermission();
        evaluationMethodType = newValue;
    }

    /**
     * Returns the description of the evaluation method.
     */
    @Override
    @XmlElement(name = "evaluationMethodDescription")
    public synchronized InternationalString getEvaluationMethodDescription() {
        return evaluationMethodDescription;
    }

    /**
     * Sets the description of the evaluation method.
     *
     * @param newValue The new evaluation method description.
     */
    public synchronized void setEvaluationMethodDescription(final InternationalString newValue)  {
        checkWritePermission();
        evaluationMethodDescription = newValue;
    }

    /**
     * Returns the reference to the procedure information, or {@code null} if none.
     */
    @Override
    @XmlElement(name = "evaluationProcedure")
    public synchronized Citation getEvaluationProcedure() {
        return evaluationProcedure;
    }

    /**
     * Sets the reference to the procedure information.
     *
     * @param newValue The new evaluation procedure.
     */
    public synchronized void setEvaluationProcedure(final Citation newValue) {
        checkWritePermission();
        evaluationProcedure = newValue;
    }

    /**
     * Returns the date or range of dates on which a data quality measure was applied.
     * The array length is 1 for a single date, or 2 for a range. Returns
     * an empty list if this information is not available.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "dateTime")
    public synchronized Collection<Date> getDates() {
        if (date1 == Long.MIN_VALUE) {
            return Collections.emptyList();
        }
        if (date2 == Long.MIN_VALUE) {
            return Collections.singleton(new Date(date1));
        }
        return Arrays.asList(
            new Date[] {new Date(date1), new Date(date2)}
        );
    }

    /**
     * Sets the date or range of dates on which a data quality measure was applied.
     * The collection size is 1 for a single date, or 2 for a range.
     *
     * @param newValues The new dates, or {@code null}.
     *
     * @since 2.4
     */
    public synchronized void setDates(final Collection<Date> newValues) {
        checkWritePermission();
        date1 = date2 = Long.MIN_VALUE;
        if (newValues != null) {
            final Iterator<Date> it = newValues.iterator();
            if (it.hasNext()) {
                date1 = it.next().getTime();
                if (it.hasNext()) {
                    date2 = it.next().getTime();
                    if (it.hasNext()) {
                        throw new IllegalArgumentException(
                                Errors.format(Errors.Keys.MISMATCHED_ARRAY_LENGTH));
                    }
                }
            }
        }
    }

    /**
     * Returns the value (or set of values) obtained from applying a data quality measure or
     * the out come of evaluating the obtained value (or set of values) against a specified
     * acceptable conformance quality level.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "result", required = true)
    public synchronized Collection<Result> getResults() {
        return results = nonNullCollection(results, Result.class);
    }

    /**
     * Sets the value (or set of values) obtained from applying a data quality measure or
     * the out come of evaluating the obtained value (or set of values) against a specified
     * acceptable conformance quality level.
     *
     * @param newValues The new results.
     *
     * @since 2.4
     */
    public synchronized void setResults(final Collection<? extends Result> newValues) {
        results = copyCollection(newValues, results, Result.class);
    }
}
