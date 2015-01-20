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
package org.geotoolkit.referencing.operation;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.operation.Formula;


/**
 * @deprecated Moved to {@link org.apache.sis.referencing.operation}.
 * @module
 */
@Deprecated
public class DefaultFormula implements Formula, Serializable {
    private static final long serialVersionUID = 1929966748615362698L;

    private final InternationalString formula;
    private final Citation citation;

    public DefaultFormula(final Citation citation, final InternationalString formula) {
        this.citation = citation;
        this.formula  = formula;
    }

    @Override
    public InternationalString getFormula() {
        return formula;
    }

    @Override
    public Citation getCitation() {
        return citation;
    }

    protected Object writeReplace() throws ObjectStreamException {
        return org.apache.sis.referencing.operation.DefaultFormula.castOrCopy(this);
    }

    protected Object readResolve() throws ObjectStreamException {
        return org.apache.sis.referencing.operation.DefaultFormula.castOrCopy(this);
    }
}
