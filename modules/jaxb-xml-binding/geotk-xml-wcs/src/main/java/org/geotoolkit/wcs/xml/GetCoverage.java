/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wcs.xml;

import java.awt.Dimension;
import org.geotoolkit.util.Versioned;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Super abstract type for all the different versions of GetCoverage request.
 *
 * @author Guilhem Legal
 * @author Cédric Briançon (Geomatys)
 */
public interface GetCoverage extends Versioned {

    /**
     * Returns the {@link CoordinateReferenceSystem} of the request, or {@code null}
     * if none.
     *
     * @throws FactoryException if the generation of the {@link CoordinateReferenceSystem} fails.
     */
    public CoordinateReferenceSystem getCRS() throws FactoryException;

    /**
     * Returns the coverage name of the request, or {@code null} if none.
     */
    public String getCoverage();

    /**
     * Returns the {@link Envelope} of the request, or {@code null} if none.
     *
     * @throws FactoryException if the generation of the {@link Envelope} fails.
     */
    public Envelope getEnvelope() throws FactoryException;

    /**
     * Returns the output format of the request, or {@code null} if none.
     */
    public String getFormat();

    /**
     * Returns the {@linkplain CoordinateReferenceSystem response CRS} of the request,
     * or {@code null} if none.
     *
     * @throws FactoryException if the generation of the {@link CoordinateReferenceSystem} fails.
     */
    public CoordinateReferenceSystem getResponseCRS() throws FactoryException;

    /**
     * Returns the output size of the request, or {@code null} if none.
     */
    public Dimension getSize();

    /**
     * Returns the time of the request, or {@code null} if none.
     */
    public String getTime();

    public String toKvp();
}
