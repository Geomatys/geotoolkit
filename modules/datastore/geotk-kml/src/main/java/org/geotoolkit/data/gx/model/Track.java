/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.gx.model;

import com.vividsolutions.jts.geom.CoordinateSequence;

import java.util.Calendar;
import java.util.List;

import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Model;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface Track extends AbstractGeometry {

    /**
     *
     * @return
     */
    AltitudeMode getAltitudeMode();

    /**
     *
     * @return
     */
    List<Calendar> getWhens();

    /**
     *
     * @return
     */
    CoordinateSequence getCoord();

    /**
     *
     * @return
     */
    List<Angles> getAngles();

    /**
     *
     * @return
     */
    Model getModel();

    /**
     *
     * @return
     */
    ExtendedData getExtendedData();

    /**
     *
     * @param altitudeMode
     */
    void setAltitudeMode(AltitudeMode altitudeMode);

    /**
     *
     * @param when
     */
    void setWhens(List<Calendar> whens);

    /**
     *
     * @param coordinates
     */
    void setCoord(CoordinateSequence coordinates);

    /**
     *
     * @param anglesList
     */
    void setAngles(List<Angles> anglesList);

    /**
     *
     * @param model
     */
    void setModel(Model model);

    /**
     * 
     * @param extendedData
     */
    void setExtendedData(ExtendedData extendedData);
}
