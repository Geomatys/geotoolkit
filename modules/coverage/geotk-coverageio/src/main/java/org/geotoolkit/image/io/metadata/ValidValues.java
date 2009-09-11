/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.image.io.metadata;

import javax.imageio.metadata.IIOMetadataFormat;
import org.geotoolkit.internal.StringUtilities;


/**
 * The values that are valid for a given element or attribute. A {@code ValidValue}
 * may describe a range or an enumeration. Instances of this class are used by
 * {@link MetadataTreeNode} only.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 * @module
 */
class ValidValues {
    /**
     * Placeholder meaning that we have determined that there is no restriction on valid values.
     */
    static final ValidValues UNRESTRICTED = new ValidValues();

    /**
     * Creates a new {@code ValidValues} instance.
     */
    ValidValues() {
    }

    /**
     * Returns a string representation of this {@code ValidValues} to be used in widgets.
     * The default is an empty string meaning "no restriction". This applies only to the
     * {@link #UNRESTRICTED} case. All subclasses must override this method in order to
     * describe their actual restrictions.
     */
    @Override
    public String toString() {
        return "";
    }




    /**
     * Valid values defined by an enumeration.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.04
     *
     * @since 3.04
     * @module
     */
    static final class Enumeration extends ValidValues {
        /**
         * The valid values.
         */
        private final Object[] values;

        /**
         * Creates a set of valid values.
         *
         * @param values The valid values.
         */
        Enumeration(final Object[] values) {
            this.values = values;
        }

        /**
         * Formats this set of valid values as a comma-separated list.
         */
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder();
            for (final Object code : values) {
                if (buffer.length() != 0) {
                    buffer.append(", ");
                }
                buffer.append(StringUtilities.separateWords(code.toString()));
            }
            return buffer.toString();
        }
    }




    /**
     * Valid values defined by a range.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.04
     *
     * @since 3.04
     * @module
     */
    static final class Range extends ValidValues {
        /**
         * One of {@code IIOMetadataFormat.VALUE_RANGE_*} constants.
         */
        private final int type;

        /**
         * The minimal and maximal value. Note that in the particular case of node attributes,
         * those values are always represented as {@link String} even if the underlying type
         * is a number. Consequently while we can not use the {@code compareTo} method in the
         * case of attributes.
         */
        private final Comparable<?> min, max;

        /**
         * Creates a range from the given values.
         *
         * @param  type One of {@code IIOMetadataFormat.VALUE_RANGE_*} constants.
         * @param  min  The minimal value.
         * @param  max  The maximal value.
         */
        Range(final int type, final Comparable<?> min, final Comparable<?> max) {
            this.type = type;
            this.min  = min;
            this.max  = max;
        }

        /**
         * Formats a range. The symbols used here are consistent with the symbols used
         * by {@link NumberRange#toString()}.
         */
        @Override
        public String toString() {
            return new StringBuilder()
                    .append((type & IIOMetadataFormat.VALUE_RANGE_MIN_INCLUSIVE_MASK) != 0 ? '[' : '(')
                    .append(nonNull(min, "-\u221E")).append(" \u2026 ")
                    .append(nonNull(max,  "\u221E"))
                    .append((type & IIOMetadataFormat.VALUE_RANGE_MAX_INCLUSIVE_MASK) != 0 ? ']' : ')')
                    .toString();
        }

        /**
         * Returns the given value if non-null, or the infinity symbol otherwise.
         */
        private static Comparable<?> nonNull(final Comparable<?> value, final String infinity) {
            return (value != null) ? value : infinity;
        }
    }
}
