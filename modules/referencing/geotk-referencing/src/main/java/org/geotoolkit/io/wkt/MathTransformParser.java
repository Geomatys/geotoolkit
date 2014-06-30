/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.io.wkt;

import java.text.ParsePosition;
import java.text.ParseException;

import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.OperationMethod;

import org.apache.sis.io.wkt.Symbols;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.referencing.IdentifiedObjects;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
 * Known Text</cite> (WKT)</A> parser for {@linkplain MathTransform math transform}s. Note that
 * while this base class is restricted to math transforms, subclasses may parse a wider range of
 * objects.
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD)
 * @author Rueben Schulz (UBC)
 * @version 3.00
 *
 * @see <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html">Well Know Text specification</A>
 *
 * @since 2.0
 * @level advanced
 * @module
 */
public class MathTransformParser extends Parser {
    /**
     * The factory to use for creating math transforms.
     */
    final MathTransformFactory mtFactory;

    /**
     * The classification of the last math transform or projection parsed,
     * or {@code null} if none.
     */
    private transient String classification;

    /**
     * The method for the last math transform passed, or {@code null} if none.
     *
     * @see #getOperationMethod
     */
    private transient OperationMethod lastMethod;

    /**
     * Creates a parser using the default set of symbols and factory.
     */
    public MathTransformParser() {
        this(Symbols.getDefault(), FactoryFinder.getMathTransformFactory(null));
    }

    /**
     * Creates a parser using the specified set of symbols and factory.
     *
     * @param symbols The set of symbols to use.
     * @param mtFactory The factory to use to create {@link MathTransform} objects.
     */
    public MathTransformParser(final Symbols symbols, final MathTransformFactory mtFactory) {
        super(symbols);
        this.mtFactory = mtFactory;
        ensureNonNull("mtFactory", mtFactory);
    }

    /**
     * Parses a math transform element.
     *
     * @param  text The text to be parsed.
     * @return The math transform.
     * @throws ParseException if the string can't be parsed.
     */
    public final MathTransform parseMathTransform(final String text) throws ParseException {
        final Element element = getTree(text, new ParsePosition(0));
        final MathTransform mt = parseMathTransform(element, true);
        element.close();
        return mt;
    }

    /**
     * Parses the next element in the specified <cite>Well Know Text</cite> (WKT) tree.
     *
     * @param  element The element to be parsed.
     * @return The parsed object.
     * @throws ParseException if the element can't be parsed.
     */
    @Override
    Object parse(final Element element) throws ParseException {
        return parseMathTransform(element, true);
    }

    /**
     * Parses the next element (a {@link MathTransform}) in the specified
     * <cite>Well Know Text</cite> (WKT) tree.
     *
     * @param  element The parent element.
     * @param  required True if parameter is required and false in other case.
     * @return The next element as a {@link MathTransform} object.
     * @throws ParseException if the next element can't be parsed.
     */
    final MathTransform parseMathTransform(final Element element, final boolean required)
            throws ParseException
    {
        lastMethod = null;
        classification = null;
        final Object key = element.peek();
        if (key instanceof Element) {
            final String keyword = keyword((Element) key);
            switch (keyword) {
                /*
                 * Note: the following code is copied in ReferencingParser in order to take
                 * advantage of a single switch statement. If new cases are added there, we
                 * need to add them in ReferencingParser as well.
                 */
                case "PARAM_MT":       return parseParamMT      (element);
                case "CONCAT_MT":      return parseConcatMT     (element);
                case "INVERSE_MT":     return parseInverseMT    (element);
                case "PASSTHROUGH_MT": return parsePassThroughMT(element);
            }
        }
        if (required) {
            throw element.parseFailed(null, Errors.format(Errors.Keys.UNKNOWN_TYPE_1, key));
        }
        return null;
    }

    /**
     * Parses a "PARAM_MT" element. This element has the following pattern:
     *
     * {@preformat text
     *     PARAM_MT["<classification-name>" {,<parameter>}* ]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "PARAM_MT"} element as an {@link MathTransform} object.
     * @throws ParseException if the {@code "PARAM_MT"} element can't be parsed.
     */
    final MathTransform parseParamMT(final Element parent) throws ParseException {
        final Element element = parent.pullElement("PARAM_MT");
        classification = element.pullString("classification");
        final ParameterValueGroup parameters;
        try {
            parameters = mtFactory.getDefaultParameters(classification);
        } catch (NoSuchIdentifierException exception) {
            throw element.parseFailed(exception, null);
        }
        /*
         * Scan over all PARAMETER["name", value] elements and
         * set the corresponding parameter in the parameter group.
         */
        Element param;
        while ((param=element.pullOptionalElement("PARAMETER")) != null) {
            final String name = param.pullString("name");
            final ParameterValue<?> parameter = parameters.parameter(name);
            final Class<?> type = parameter.getDescriptor().getValueClass();
            if (type == Integer.class) {
                parameter.setValue(param.pullInteger("value"));
            } else if (type == Double.class) {
                parameter.setValue(param.pullDouble("value"));
            } else if (type == Boolean.class) {
                parameter.setValue(param.pullBoolean("value"));
            } else {
                parameter.setValue(param.pullString("value"));
            }
            param.close();
        }
        element.close();
        /*
         * We now have all informations for constructing the math transform.
         */
        final MathTransform transform;
        try {
            transform = mtFactory.createParameterizedTransform(parameters);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
        lastMethod = mtFactory.getLastMethodUsed();
        return transform;
    }

    /**
     * Parses an {@code "INVERSE_MT"} element. This element has the following pattern:
     *
     * {@preformat text
     *     INVERSE_MT[<math transform>]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "INVERSE_MT"} element as an {@link MathTransform} object.
     * @throws ParseException if the {@code "INVERSE_MT"} element can't be parsed.
     */
    final MathTransform parseInverseMT(final Element parent) throws ParseException {
        final Element element = parent.pullElement("INVERSE_MT");
        MathTransform transform = parseMathTransform(element, true);
        try {
            transform = transform.inverse();
        } catch (NoninvertibleTransformException exception) {
            throw element.parseFailed(exception, null);
        }
        element.close();
        return transform;
    }

    /**
     * Parses a {@code "PASSTHROUGH_MT"} element. This element has the following pattern:
     *
     * {@preformat text
     *     PASSTHROUGH_MT[<integer>, <math transform>]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "PASSTHROUGH_MT"} element as an {@link MathTransform} object.
     * @throws ParseException if the {@code "PASSTHROUGH_MT"} element can't be parsed.
     */
    final MathTransform parsePassThroughMT(final Element parent) throws ParseException {
        final Element           element = parent.pullElement("PASSTHROUGH_MT");
        final int firstAffectedOrdinate = parent.pullInteger("firstAffectedOrdinate");
        final MathTransform   transform = parseMathTransform(element, true);
        element.close();
        try {
            return mtFactory.createPassThroughTransform(firstAffectedOrdinate, transform, 0);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a {@code "CONCAT_MT"} element. This element has the following pattern:
     *
     * {@preformat text
     *     CONCAT_MT[<math transform> {,<math transform>}*]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "CONCAT_MT"} element as an {@link MathTransform} object.
     * @throws ParseException if the {@code "CONCAT_MT"} element can't be parsed.
     */
    final MathTransform parseConcatMT(final Element parent) throws ParseException {
        final Element element = parent.pullElement("CONCAT_MT");
        MathTransform transform = parseMathTransform(element, true);
        MathTransform optionalTransform;
        while ((optionalTransform = parseMathTransform(element, false)) != null) {
            try {
                transform = mtFactory.createConcatenatedTransform(transform, optionalTransform);
            } catch (FactoryException exception) {
                throw element.parseFailed(exception, null);
            }
        }
        element.close();
        return transform;
    }

    /**
     * Returns the operation method for the last math transform parsed. This is used by
     * {@link Parser} in order to built {@link org.opengis.referencing.crs.DerivedCRS}.
     */
    final OperationMethod getOperationMethod() {
        if (lastMethod == null) {
            /*
             * Safety in case come MathTransformFactory implementation do not support
             * getLastMethod(). Performs a slower and less robust check as a fallback.
             */
            if (classification != null) {
                for (final OperationMethod method : mtFactory.getAvailableMethods(SingleOperation.class)) {
                    if (IdentifiedObjects.isHeuristicMatchForName(method, classification)) {
                        lastMethod = method;
                        break;
                    }
                }
            }
        }
        return lastMethod;
    }
}
