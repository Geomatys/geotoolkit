/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.sql.SQLException;

import org.opengis.geometry.Envelope; // For javadoc
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.coverage.io.GridCoverageReadParam;


/**
 * A request for data in a {@linkplain CoverageDatabase Coverage Database}.
 * A request it typically created by calls to the following methods:
 * <p>
 * <ul>
 *   <li>{@link #setLayer(String)}</li>
 *   <li>{@link #setEnvelope(Envelope)}</li>
 *   <li>{@link #setResolution(double[])}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
public class CoverageQuery extends GridCoverageReadParam {
    /**
     * The layer to be requested.
     */
    private String layer;

    /**
     * Creates an initially empty request.
     */
    public CoverageQuery() {
    }

    /**
     * Returns the layer to be requested.
     *
     * @return The layer to be requested.
     */
    public String getLayer() {
        return layer;
    }

    /**
     * Sets the layer to be requested.
     *
     * @param layer The new layer to request.
     */
    public void setLayer(final String layer) {
        this.layer = layer;
    }

    /**
     * Configures the given table using the information declared in this request.
     */
    final void configure(final GridCoverageTable table) throws SQLException, TransformException {
        table.setLayer(layer);
        table.envelope.setEnvelope(getEnvelope());
    }
}
