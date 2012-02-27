/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display.axis;

import java.text.NumberFormat;
import javax.measure.unit.Unit;


/**
 * A graduation using numbers on a logarithmic axis.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class LogarithmicNumberGraduation extends NumberGraduation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8514854171546232887L;

    /**
     * Contructs a new logarithmic graduation with the supplied units.
     *
     * @param unit The graduation unit.
     */
    public LogarithmicNumberGraduation(final Unit<?> unit) {
        super(unit);
    }

    /**
     * Constructs or reuses an iterator. This method override
     * the default {@link NumberGraduation} implementation.
     */
    @Override
    NumberIterator getTickIterator(final TickIterator reuse) {
        final NumberFormat format = getFormat();
        if (reuse instanceof LogarithmicNumberIterator) {
            final NumberIterator it = (NumberIterator) reuse;
            it.setFormat(format);
            return it;
        } else {
            return new LogarithmicNumberIterator(format);
        }
    }
}
