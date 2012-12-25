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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation;

import java.util.Objects;
import java.io.Serializable;
import net.jcip.annotations.Immutable;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.operation.Formula;

import org.geotoolkit.util.SimpleInternationalString;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Specification of the coordinate operation method formula.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@Immutable
public class DefaultFormula implements Formula, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1929966748615362698L;

    /**
     * Formula(s) or procedure used by the operation method.
     */
    private final InternationalString formula;

    /**
     * Reference to a publication giving the formula(s) or procedure used by the
     * coordinate operation method.
     */
    private final Citation citation;

    /**
     * Creates a new formula from the given string.
     *
     * @param formula The formula.
     */
    public DefaultFormula(final CharSequence formula) {
        ensureNonNull("formula", formula);
        if (formula instanceof InternationalString) {
            this.formula = (InternationalString) formula;
        } else {
            this.formula = new SimpleInternationalString(formula.toString());
        }
        this.citation = null;
    }

    /**
     * Creates a new formula from the given citation.
     *
     * @param citation The citation.
     */
    public DefaultFormula(final Citation citation) {
        ensureNonNull("citation", citation);
        this.citation = citation;
        this.formula  = null;
    }

    /**
     * Returns the formula(s) or procedure used by the operation method, or {@code null} if none.
     */
    @Override
    public InternationalString getFormula() {
        return formula;
    }

    /**
     * Returns the reference to a publication giving the formula(s) or procedure used by the
     * coordinate operation method, or {@code null} if none.
     */
    @Override
    public Citation getCitation() {
        return citation;
    }

    /**
     * Returns a hash code value for this formula.
     */
    @Override
    public int hashCode() {
        int code = (int) serialVersionUID;
        if (formula  != null) code += formula .hashCode();
        if (citation != null) code += citation.hashCode() * 31;
        return code;
    }

    /**
     * Compares this formula with the given object for equality.
     *
     * @param  object The object to compare with this formula.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass() == getClass()) {
            final DefaultFormula that = (DefaultFormula) object;
            return Objects.equals(this.formula,  that.formula) &&
                   Objects.equals(this.citation, that.citation);
        }
        return false;
    }

    /**
     * Returns a string representation of this formula.
     */
    @Override
    public String toString() {
        final CharSequence text;
        if (citation != null) {
            text = citation.getTitle();
        } else {
            text = formula;
        }
        return "FORMULA[\"" + text + "\"]";
    }
}
