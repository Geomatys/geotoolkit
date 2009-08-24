/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.Result;
import org.opengis.metadata.quality.Element;
import org.opengis.metadata.quality.EvaluationMethodType;
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.resources.Errors;


/**
 * Type of test applied to the data specified by a data quality scope.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public class AbstractElement extends MetadataEntity implements Element {
    /**
     * Serial number for interoperability with different versions.
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
     * Date or range of dates on which a data quality measure was applied.
     * The array length is 1 for a single date, or 2 for a range. Returns
     * {@code null} if this information is not available.
     */
    private long date1 = Long.MIN_VALUE, date2 = Long.MIN_VALUE;

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
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public AbstractElement(final Element source) {
        super(source);
    }

    /**
     * Creates an element initialized to the given result.
     *
     * @param result The value obtained from applying a data quality measure against a specified
     *               acceptable conformance quality level.
     */
    public AbstractElement(final Result result) {
        setResults(Collections.singleton(result));
    }

    /**
     * Returns the name of the test applied to the data.
     */
    @Override
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
     * @param newValues The new dates.
     *
     * @since 2.4
     */
    public synchronized void setDates(final Collection<Date> newValues) {
        checkWritePermission();
        date1 = date2 = Long.MIN_VALUE;
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

    /**
     * Returns the value (or set of values) obtained from applying a data quality measure or
     * the out come of evaluating the obtained value (or set of values) against a specified
     * acceptable conformance quality level.
     *
     * @since 2.4
     */
    @Override
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
