/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.expression;

import javax.xml.bind.annotation.XmlTransient;

import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;
import org.geotoolkit.util.logging.Logging;

import org.opengis.filter.expression.Expression;

/**
 * Abstract superclass of these Expression implementations.
 * <p>
 * Contains additional support for "Expression chaining". This allows
 * Expressions to be constructed as a chain of Java commands similar to the use
 * of the java collections api.
 * </p>
 * <p>
 * Note: Expression chaining is a simple developer convience, it has no effect
 * on the data model exposed by the GeoAPI interfaces.
 * </p>
 * <p>
 * Idea: We may also be able to teach this implementation to make use of
 * JXPath to extract "attribute values" from Java Beans, DOM, JDOM in addition
 * to the geotools & geoapi FeatureType models. It is a cunning plan - any
 * implementation will make use of this abstract base class.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 * @author Jody Garnett
 *
 */
@XmlTransient
public abstract class AbstractExpression implements Expression {

    protected static final ConverterRegistry CONVERTERS = ConverterRegistry.system();

    /**
     * Default implementation delegates handling of context
     * conversion to Value utility class.
     * <p>
     * Subclasses are expected to make use of the Value utility class
     * (as the easiest way to provide value morphing in confirmance with
     *  the Filter specification).
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T evaluate(Object candidate, Class<T> target) {
        return (T) evaluate(candidate, (Class) candidate.getClass(), target);
    }

    private static <S,T> T evaluate(S candidate, Class<S> source, Class<T> target) {

        // handle case of source being an instance of target up front
        if (target.isAssignableFrom(source) ) {
            return (T) candidate;
        }

        final ObjectConverter<S,T> converter;
        try {
            converter = CONVERTERS.converter(source, target);
            return converter.convert(candidate);
        } catch (NonconvertibleObjectException ex) {
            Logging.recoverableException(AbstractExpression.class, "evaluate", ex);
            return null;
        }
    }

}
