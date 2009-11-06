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

package org.geotoolkit.internal.jaxb;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.CurveInterpolation;
import org.opengis.geometry.primitive.CurveSegment;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CurveSegmentAdapter extends XmlAdapter<CurveSegmentAdapter, CurveSegment>{

    @XmlAttribute
    @XmlJavaTypeAdapter(CurveInterpolationAdapter.class)
    private CurveInterpolation interpolation = CurveInterpolation.LINEAR;

    @XmlElement(name="pos", namespace="http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(DirectPositionAdapter.class)
    private List<Position> positions;

    public CurveSegmentAdapter() {

    }

    public CurveSegmentAdapter(CurveSegment lineString) {
        this.positions = lineString.getSamplePoints().positions();
    }

    @Override
    public CurveSegment unmarshal(CurveSegmentAdapter v) throws Exception {
       JTSLineString line = new JTSLineString();
       for (Position p : v.positions) {
            line.getPositions().add(p);
       }
       return line;
    }

    @Override
    public CurveSegmentAdapter marshal(CurveSegment v) throws Exception {
        return new CurveSegmentAdapter(v);
    }

}
