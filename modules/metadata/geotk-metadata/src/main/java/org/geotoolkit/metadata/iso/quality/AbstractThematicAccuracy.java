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

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.quality.ThematicAccuracy;
import org.opengis.metadata.quality.ThematicClassificationCorrectness;
import org.opengis.metadata.quality.NonQuantitativeAttributeAccuracy;
import org.opengis.metadata.quality.QuantitativeAttributeAccuracy;


/**
 * Accuracy of quantitative attributes and the correctness of non-quantitative attributes
 * and of the classifications of features and their relationships.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "AbstractDQ_ThematicAccuracy_Type")
@XmlRootElement(name = "DQ_ThematicAccuracy")
@XmlSeeAlso({
    DefaultThematicClassificationCorrectness.class,
    DefaultNonQuantitativeAttributeAccuracy.class,
    DefaultQuantitativeAttributeAccuracy.class
})
public class AbstractThematicAccuracy extends AbstractElement implements ThematicAccuracy {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -781072634778767137L;

    /**
     * Constructs an initially empty thematic accuracy.
     */
    public AbstractThematicAccuracy() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public AbstractThematicAccuracy(final ThematicAccuracy source) {
        super(source);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     * <p>
     * This method checks for the {@link QuantitativeAttributeAccuracy},
     * {@link NonQuantitativeAttributeAccuracy} and {@link ThematicClassificationCorrectness}
     * sub-interfaces. If one of those interfaces is found, then this method delegates to
     * the corresponding {@code castOrCopy} static method. If the given object implements more
     * than one of the above-cited interfaces, then the {@code castOrCopy} method to be used is
     * unspecified.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static AbstractThematicAccuracy castOrCopy(final ThematicAccuracy object) {
        if (object instanceof QuantitativeAttributeAccuracy) {
            return DefaultQuantitativeAttributeAccuracy.castOrCopy((QuantitativeAttributeAccuracy) object);
        }
        if (object instanceof NonQuantitativeAttributeAccuracy) {
            return DefaultNonQuantitativeAttributeAccuracy.castOrCopy((NonQuantitativeAttributeAccuracy) object);
        }
        if (object instanceof ThematicClassificationCorrectness) {
            return DefaultThematicClassificationCorrectness.castOrCopy((ThematicClassificationCorrectness) object);
        }
        return (object == null) || (object instanceof AbstractThematicAccuracy)
                ? (AbstractThematicAccuracy) object : new AbstractThematicAccuracy(object);
    }
}
