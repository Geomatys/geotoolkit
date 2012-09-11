/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.internal.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSRing;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LinearRingPosListType {

    @XmlElement(namespace = "http://www.opengis.net/gml")
    private PosListType posList;

    @XmlElement(namespace = "http://www.opengis.net/gml")
    private CoordinatesType coordinates;

    public LinearRingPosListType() {

    }

    public LinearRingPosListType(final List<Double> value) {
        this.posList = new PosListType(value);
    }

    public LinearRingPosListType(final JTSRing ring) {
        //this.srsName = CoordinateReferenceSystemAdapter.getSrsName(lineString.getCoordinateReferenceSystem());

        if (ring.getElements().size() == 1) {
            Object curveObj = ring.getElements().iterator().next();
            if (curveObj instanceof Curve) {
                Curve curve = (Curve) curveObj;

                List<Double> value = new ArrayList<Double>();
                for (CurveSegment cv : curve.getSegments()) {
                    JTSLineString line = (JTSLineString) cv;

                    for (Position p : line.getPositions()) {
                        for (int i = 0; i < p.getDirectPosition().getDimension(); i++) {
                            value.add(p.getDirectPosition().getOrdinate(i));
                        }
                    }
                }
                posList = new PosListType(value);
            }
        } else {
            throw new IllegalArgumentException("the ring is not linear");
        }
    }

    public JTSRing getJTSRing() {

        final List<Double> values;
        if (posList != null) {
            values = posList.getValue();
        } else if (coordinates != null) {
            values = coordinates.getValues();
        } else {
            values = new ArrayList<Double>();
        }

        final JTSLineString line = new JTSLineString();
        for (int i = 0; i < values.size() - 1; i = i + 2) {
            double x = values.get(i);
            double y = values.get(i + 1);
            final DirectPosition pos = new GeneralDirectPosition(x, y);
            line.getControlPoints().add(pos);
        }

        JTSCurve curve = new JTSCurve();
        curve.getSegments().add(line);
        JTSRing ring = new JTSRing();
        ring.getElements().add(curve);
        return ring;
    }

    /**
     * @return the posList
     */
    public PosListType getPosList() {
        return posList;
    }

    /**
     * @param posList the posList to set
     */
    public void setPosList(final PosListType posList) {
        this.posList = posList;
    }

    /**
     * @return the coordinates
     */
    public CoordinatesType getCoordinates() {
        return coordinates;
    }

    /**
     * @param coordinates the coordinates to set
     */
    public void setCoordinates(CoordinatesType coordinates) {
        this.coordinates = coordinates;
    }
}
