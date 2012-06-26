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
package org.geotoolkit.coverage.sql;

import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.MismatchedReferenceSystemException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.image.io.IIOListeners;


/**
 * A request for data in a {@linkplain CoverageDatabase Coverage Database}.
 * A query contains the following components:
 * <p>
 * <ul>
 *   <li>The name of the {@linkplain Layer layer} to be queried.</li>
 *   <li>The {@linkplain CoverageEnvelope envelope of the coverage} to be queried.</li>
 *   <li>The preferred resolution (actually part of the above-cited {@code CoverageEnvelope}).</li>
 *   <li>Optional listeners to inform about reading progress. Strictly speaking, this is not
 *       part of a query. But the listeners are declared in this object anyway in order to
 *       have all reading parameters in a single place.</li>
 * </ul>
 * <p>
 * Every getter methods defined in this class return a direct reference to the objects holds
 * by {@code CoverageQuery} - the objects are not cloned. This approach makes easier to configure
 * a query by modifying directly the returned objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @see CoverageDatabase#readSlice(CoverageQuery)
 *
 * @since 3.10
 * @module
 *
 * @deprecated Experience has shown that this class is rarely used in practice, since
 * {@link LayerCoverageReader} is a more powerful way to get the same functionality.
 * Consequently this class will be removed in order to simplify the API.
 */
@Deprecated
public class CoverageQuery {
    /**
     * The name of the layer to be requested.
     */
    private String layer;

    /**
     * The envelope of the region to be queried.
     */
    private final CoverageEnvelope envelope;

    /**
     * The listeners, or {@code null} if none.
     */
    IIOListeners listeners;

    /**
     * Creates a new query for the given database.
     *
     * @param database The database for which a query is created.
     */
    public CoverageQuery(final CoverageDatabase database) {
        envelope = new CoverageEnvelope(database.database);
    }

    /**
     * Returns the name of the layer to be requested, or {@code null} if unspecified.
     *
     * @return The name of the layer to be requested, or {@code null} if unspecified.
     */
    public String getLayer() {
        return layer;
    }

    /**
     * Sets the name of the layer to be requested.
     *
     * @param name The name of the new layer to request.
     */
    public void setLayer(final String name) {
        layer = name;
    }

    /**
     * Returns the envelope of the coverage to be queried. This method returns a direct
     * reference to the envelope holds by this class - any change to the returned envelope
     * will affect directly this query.
     * <p>
     * <b>Example</b>: the following code defines the time for which a coverage is wanted:
     *
     * {@preformat java
     *     Date startTime = ...;
     *     Date endTime = ...;
     *     query.getEnvelope().setTimeRange(startTime, endTime);
     * }
     *
     * @return The envelope of the coverage to be queried (never {@code null}).
     */
    public CoverageEnvelope getEnvelope() {
        return envelope;
    }

    /**
     * Sets the envelope of the coverage to be queried. This method is provided for completness
     * with the {@link #getEnvelope()} method, but usually don't need to be invoked since the
     * object returned by {@code getEnvelope()} can be modified directly.
     *
     * @param newEnvelope The new envelope, or {@code null} for infinite bounds.
     * @throws MismatchedReferenceSystemException If the given envelope uses an incompatible CRS.
     */
    public void setEnvelope(final CoverageEnvelope newEnvelope) throws MismatchedReferenceSystemException {
        try {
            envelope.setAll(newEnvelope);
        } catch (TransformException e) {
            throw new MismatchedReferenceSystemException(envelope.errors().getString(
                    Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), e);
        }
    }

    /**
     * Returns the listeners to inform about read progress. This method returns a direct
     * reference to the listener list holds by this class - any change to the returned
     * list will affect directly this query.
     * <p>
     * <b>Example</b>: the following code registers a
     * {@link javax.imageio.event.IIOReadProgressListener} which will be informed about the
     * progress of any image file to be read:
     *
     * {@preformat java
     *     query.getIIOListeners().addIIOReadProgressListener(myListener);
     * }
     *
     * @return The list of listeners to inform about read progress (never {@code null}).
     */
    public IIOListeners getIIOListeners() {
        if (listeners == null) {
            listeners = new IIOListeners();
        }
        return listeners;
    }

    /**
     * Sets the listeners to inform about read progress. This method is provided for completness
     * with the {@link #getIIOListeners()} method, but usually don't need to be invoked since the
     * object returned by {@code getIIOListeners()} can be modified directly.
     *
     * @param newListeners The new listeners, or {@code null} for none.
     */
    public void setIIOListeners(final IIOListeners newListeners) {
        if (newListeners != null) {
            getIIOListeners().setListeners(listeners);
        } else {
            listeners = null;
        }
    }
}
