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
package org.geotoolkit.metadata.iso.quality;

import java.util.Collections;
import java.io.ObjectStreamException;
import javax.xml.bind.annotation.XmlTransient;
import org.opengis.util.InternationalString;
import org.opengis.metadata.quality.EvaluationMethodType;
import org.opengis.metadata.quality.AbsoluteExternalPositionalAccuracy;
import org.apache.sis.util.Workaround;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.quality.DefaultConformanceResult;


/**
 * Same as {@link DefaultAbsoluteExternalPositionalAccuracy} but in a different implementation class.
 * This is needed in order to avoid a deadlock to occur when {@code DirectEpsgFactory} instantiates
 * a {@code DefaultAbsoluteExternalPositionalAccuracy} in one thread while the static initializer of
 * {@link AbstractPositionalAccuracy} is running in an other thread. This is a know issue with class
 * initialization in Java without satisfying solution. The workaround applied here is to use a
 * package-privated class that users can not instantiate themself.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4891511">Issue 4891511</a>
 *
 * @since 3.00
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@Workaround(library="JDK", version="1.6")
@XmlTransient
final class PositionalAccuracyConstant extends AbstractPositionalAccuracy
       implements AbsoluteExternalPositionalAccuracy
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2554090935254116470L;

    /**
     * Creates an positional accuracy initialized to the given result.
     */
    PositionalAccuracyConstant(final InternationalString desc,
                               final InternationalString eval,
                               final boolean pass)
    {
        DefaultConformanceResult result = new DefaultConformanceResult(Citations.GEOTOOLKIT, eval, pass);
        result.freeze();
        setResults(Collections.singleton(result));
        setMeasureDescription(desc);
        setEvaluationMethodDescription(eval);
        setEvaluationMethodType(EvaluationMethodType.DIRECT_INTERNAL);
        freeze();
    }

    /**
     * Invoked on deserialization. Replace this instance by one of the constants, if applicable.
     */
    private Object readResolve() throws ObjectStreamException {
        if (equals(DATUM_SHIFT_APPLIED)) return DATUM_SHIFT_APPLIED;
        if (equals(DATUM_SHIFT_OMITTED)) return DATUM_SHIFT_OMITTED;
        return this;
    }
}
