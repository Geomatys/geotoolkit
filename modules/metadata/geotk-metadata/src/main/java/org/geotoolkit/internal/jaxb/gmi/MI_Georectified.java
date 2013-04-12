/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.jaxb.gmi;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import org.opengis.metadata.spatial.Georectified;
import org.geotoolkit.metadata.iso.spatial.DefaultGeorectified;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * A wrapper for a metadata using the {@code "gmi"} namespace.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.17
 * @module
 */
@XmlType(name = "MI_Georectified_Type")
@XmlRootElement(name = "MI_Georectified")
public class MI_Georectified extends DefaultGeorectified {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 6964334064255912684L;

    /**
     * Creates an initially empty metadata.
     * This is also the default constructor used by JAXB.
     */
    public MI_Georectified() {
    }

    /**
     * Creates a new metadata as a copy of the given one.
     * This is a shallow copy constructor.
     *
     * @param original The original metadata to copy.
     */
    public MI_Georectified(final Georectified original) {
        super(original);
    }

    /**
     * Wraps the given metadata into a Geotk implementation that can be marshalled,
     * using the {@code "gmi"} namespace if necessary.
     *
     * @param  original The original metadata provided by the user.
     * @return The metadata to marshall.
     */
    public static DefaultGeorectified castOrCopy(final Georectified original) {
        if (original != null && !(original instanceof MI_Georectified)) {
            if (!isNullOrEmpty(original.getCheckPoints())) {
                return new MI_Georectified(original);
            }
        }
        return DefaultGeorectified.castOrCopy(original);
    }
}
