/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.display2d.service;


import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * View Definition holds the different navigation parameter of the
 * geographic area to be rendered.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ViewDef {

    private Envelope envelope = new JTSEnvelope2D(DefaultGeographicCRS.WGS84);
    private double azimuth = 0;
    private CanvasMonitor monitor = null;

    public ViewDef() {
    }

    public ViewDef(Envelope env) {
        this(env,0);
    }

    public ViewDef(Envelope env, double azimuth) {
        this(env,azimuth,null);
    }

    public ViewDef(Envelope env, double azimuth, CanvasMonitor monitor) {
        setEnvelope(env);
        setAzimuth(azimuth);
        setMonitor(monitor);
    }

    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setMonitor(CanvasMonitor monitor) {
        this.monitor = monitor;
    }

    public CanvasMonitor getMonitor() {
        return monitor;
    }

    @Override
    public String toString() {
        return "ViewDef[envelope=" + envelope + ", azimuth=" + azimuth +"]";
    }

    /**
     * Test and update the Envelope to ensure that the East-West axis is placed horizontaly.
     * This has no effect if the axis direction can not determinate.
     *
     * @return ViewDef : this same object
     */
    public ViewDef setLongitudeFirst() throws TransformException, FactoryException{
        final Envelope env = getEnvelope();
        if(env != null){
            setEnvelope( GO2Utilities.setLongitudeFirst(env) );
        }
        return this;
    }

}
