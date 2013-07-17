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
 */
package org.geotoolkit.geometry;

import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;


/**
 * A two-dimensional envelope on top of {@link Rectangle2D}.
 *
 * <p>This class is kept as a workaround for the "not yet working" {@code Envelope2D(GeographicBoundingBox)}
 * constructor in the Apache SIS class.</p>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.geometry.Envelope2D}.
 */
@Deprecated
public class Envelope2D extends org.apache.sis.geometry.Envelope2D implements Cloneable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3319231220761419351L;

    /**
     * Constructs a new envelope with the same data than the specified
     * geographic bounding box. The coordinate reference system is set
     * to {@linkplain DefaultGeographicCRS#WGS84 WGS84}.
     *
     * @param box The bounding box to copy.
     *
     * @see GeneralEnvelope#GeneralEnvelope(GeographicBoundingBox)
     *
     * @since 3.11
     */
    public Envelope2D(final GeographicBoundingBox box) {
        super(DefaultGeographicCRS.WGS84,
              box.getWestBoundLongitude(),
              box.getSouthBoundLatitude(),
              box.getEastBoundLongitude() - box.getWestBoundLongitude(),
              box.getNorthBoundLatitude() - box.getSouthBoundLatitude());
    }
}
