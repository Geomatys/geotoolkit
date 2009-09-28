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

import java.util.Date;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.Envelope;

/**
 * View Definition holds the different navigation parameter of the
 * geographic area to be rendered.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ViewDef {

    private Envelope envelope = new JTSEnvelope2D(DefaultGeographicCRS.WGS84);
    private double azimuth = 0;
    private Date start = null;
    private Date end = null;
    private CanvasMonitor monitor = null;

    public ViewDef() {
    }

    public ViewDef(Envelope env, double azimuth) {
        setEnvelope(envelope);
        setAzimuth(azimuth);
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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setMonitor(CanvasMonitor monitor) {
        this.monitor = monitor;
    }

    public CanvasMonitor getMonitor() {
        return monitor;
    }

    @Override
    public String toString() {
        return "ViewDef[envelope=" + envelope + ", azimuth=" + azimuth + ", startDate="+start+", endDate="+end+"]";
    }
}
