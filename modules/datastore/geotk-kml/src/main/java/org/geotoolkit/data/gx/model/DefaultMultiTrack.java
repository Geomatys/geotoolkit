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

import java.util.List;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.DefaultAbstractGeometry;
import static org.geotoolkit.data.gx.xml.GxConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultMultiTrack extends DefaultAbstractGeometry implements MultiTrack {

    private AltitudeMode altitudeMode;
    private boolean interpolate;
    private List<Track> tracks;

    /**
     *
     */
    public DefaultMultiTrack(){
        this.altitudeMode = DEF_ALTITUDE_MODE;
        this.interpolate = DEF_INTERPOLATE;
        this.tracks = EMPTY_LIST;
    }

    /**
     * 
     * @param altitudeMode
     * @param interpolate
     * @param tracks
     */
    public DefaultMultiTrack(AltitudeMode altitudeMode,
            boolean interpolate, List<Track> tracks){
        super();
        this.altitudeMode = altitudeMode;
        this.interpolate = interpolate;
        this.tracks = (tracks == null) ? EMPTY_LIST : tracks;
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
    public boolean getInterpolate() {
        return this.interpolate;
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
    public void setInterpolate(boolean interpolate) {
        this.interpolate = interpolate;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Track> getTracks() {
        return this.tracks;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTracks(List<Track> tracks) {
        this.tracks = (tracks == null) ? EMPTY_LIST : tracks;
    }
}
