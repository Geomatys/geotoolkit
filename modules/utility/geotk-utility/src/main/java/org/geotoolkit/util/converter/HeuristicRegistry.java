/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.converter;

import org.opengis.util.CodeList;
import net.jcip.annotations.ThreadSafe;


/**
 * A {@link ConverterRegistry} which applies heuristic rules in addition of the
 * explicitly registered converters.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 */
@ThreadSafe
class HeuristicRegistry extends ConverterRegistry {
    /**
     * Creates an initially empty set of object converters. The heuristic
     * rules apply right away, even if no converter have been registered yet.
     */
    public HeuristicRegistry() {
    }

    /**
     * Applies the heuristic rules before to delegate to the registered converters.
     */
    @Override
    public <S,T> ObjectConverter<S,T> converter(final Class<S> source, final Class<T> target)
            throws NonconvertibleObjectException
    {
        /*
         * Before the usual search for converters, perform a special check for the CharSequence
         * interface. The source objects are converted to String, then the final conversions are
         * delegated to a converter from String to the target. We handle CharSequence especially
         * because it is an interface rather than a class (see ConverterRegistry javadoc).
         */
        if (source == CharSequence.class) {
            final ObjectConverter<?,?> converter;
            if (target.isAssignableFrom(source)) {
                converter = IdentityConverter.CHAR_SEQUENCE;
            } else if (target == String.class) {
                converter = CharSequenceConverter.STRING;
            } else {
                converter = CharSequenceConverter.create(target, super.converter(String.class, target));
            }
            final ClassPair<S,T> key = new ClassPair<>(source, target);
            return key.cast(converter);
            // Do not register, because we want to keep the tree free of converters
            // having an interface as its source (to keep the system simpler).
        }
        return super.converter(source, target);
    }

    /**
     * Special case: if the target type is a code list, generate the converter on-the-fly.
     * We do not register every code lists in advance because there is too many of them,
     * and a generic code is available for all of them.
     */
    @Override
    @SuppressWarnings("unchecked")
    <S,T> ObjectConverter<S,T> createConverter(final Class<S> source, final Class<T> target) {
        if (source == String.class && CodeList.class.isAssignableFrom(target)) {
            return StringConverter.CodeList.create(target.asSubclass(CodeList.class));
        }
        return super.createConverter(source, target);
    }
}
