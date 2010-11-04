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
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.DefaultAbstractGeometry;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Model;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultTrack extends DefaultAbstractGeometry implements Track {

    AltitudeMode altitudeMode;
    List<Calendar> whens;
    CoordinateSequence coord;
    List<Angles> anglesList;
    Model model;
    ExtendedData extendedData;

    public DefaultTrack(){
        this.altitudeMode = DEF_ALTITUDE_MODE;
        this.whens = EMPTY_LIST;
        this.anglesList = EMPTY_LIST;
    }
    
    public DefaultTrack(AltitudeMode altitudeMode,
            List<Calendar> whens, CoordinateSequence coord,
            List<Angles> angleList, Model model, ExtendedData extendedData){
        this.altitudeMode = altitudeMode;
        this.whens = (whens == null) ? EMPTY_LIST : whens;
        this.coord = coord;
        this.anglesList = (angleList == null) ? EMPTY_LIST : angleList;
        this.model = model;
        this.extendedData = extendedData;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {
        return this.altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Calendar> getWhens() {
        return this.whens;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public CoordinateSequence getCoord() {
        return this.coord;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Angles> getAngles() {
        return this.anglesList;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Model getModel() {
        return this.model;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ExtendedData getExtendedData() {
        return this.extendedData;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setWhens(List<Calendar> whens) {
        this.whens = (whens == null) ? EMPTY_LIST : whens;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCoord(CoordinateSequence coord) {
        this.coord = coord;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAngles(List<Angles> anglesList) {
        this.anglesList = (anglesList == null) ? EMPTY_LIST : anglesList;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setExtendedData(ExtendedData extendedData) {
        this.extendedData = extendedData;
    }

}
