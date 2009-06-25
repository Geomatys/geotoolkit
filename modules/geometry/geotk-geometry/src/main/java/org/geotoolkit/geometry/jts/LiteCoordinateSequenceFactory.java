/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;

/**
 * @TODO class description
 *
 * @author jeichar
 * @since 2.1.x
 * @source $URL$
 */
public class LiteCoordinateSequenceFactory implements CoordinateSequenceFactory {

	/* (non-Javadoc)
	 * @see com.vividsolutions.jts.geom.CoordinateSequenceFactory#create(com.vividsolutions.jts.geom.Coordinate[])
	 */
    @Override
	public CoordinateSequence create(Coordinate[] coordinates) {
		return new LiteCoordinateSequence(coordinates);
	}

	/* (non-Javadoc)
	 * @see com.vividsolutions.jts.geom.CoordinateSequenceFactory#create(com.vividsolutions.jts.geom.CoordinateSequence)
	 */
    @Override
	public CoordinateSequence create(CoordinateSequence coordSeq) {
		return new LiteCoordinateSequence(coordSeq.toCoordinateArray());
	}

	/* (non-Javadoc)
	 * @see com.vividsolutions.jts.geom.CoordinateSequenceFactory#create(int, int)
	 */
    @Override
	public CoordinateSequence create(int size, int dimension) {
		return new LiteCoordinateSequence(size, dimension);
	}

	/**
	 * @param points
	 */
	public CoordinateSequence create(double[] points) {
		return new LiteCoordinateSequence(points);
	}
}
