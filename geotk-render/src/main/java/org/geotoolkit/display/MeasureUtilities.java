/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.display;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.Unit;

import org.apache.sis.measure.Units;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.GeodeticCalculator;
import org.geotoolkit.geometry.jts.JTS;
import org.apache.sis.internal.referencing.ReferencingUtilities;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.util.logging.Logging;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MeasureUtilities {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display");

    public static double calculateLenght(final Geometry geom, final CoordinateReferenceSystem geomCRS, final Unit<Length> unit){

        if(geom == null || !(geom instanceof LineString)) return 0;

        final LineString line = (LineString) geom;

        Coordinate[] coords = line.getCoordinates();

        try {
            final GeodeticCalculator calculator = GeodeticCalculator.create(geomCRS);
            final GeneralDirectPosition pos = new GeneralDirectPosition(geomCRS);

            double length = 0;
            for(int i=0,n=coords.length-1;i<n;i++){
                Coordinate coord1 = coords[i];
                Coordinate coord2 = coords[i+1];

                pos.coordinates[0] = coord1.x;
                pos.coordinates[1] = coord1.y;
                calculator.setStartPoint(pos);
                pos.coordinates[0] = coord2.x;
                pos.coordinates[1] = coord2.y;
                calculator.setEndPoint(pos);

                length += calculator.getGeodesicDistance();
            }

            if(!Units.METRE.equals(unit)){
                UnitConverter converter = Units.METRE.getConverterTo(unit);
                length = converter.convert(length);
            }

            return length;

        } catch (MismatchedDimensionException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return 0;
    }

    public static double calculateArea(final Geometry geom, final CoordinateReferenceSystem geomCRS, final Unit unit){

        if(geom == null || !(geom instanceof Polygon)) return 0;

        try {
            Envelope env = JTS.toEnvelope(geom);

            final GeographicCRS geoCRS = ReferencingUtilities.toNormalizedGeographicCRS(geomCRS, false, false);

            final MathTransform step0 = CRS.findOperation(geomCRS, geoCRS, null).getMathTransform();
            Envelope genv = JTS.transform(env, step0);

            double centerMeridian = genv.getWidth()/2 + genv.getMinX();
            double northParallal = genv.getMaxY() - genv.getHeight()/3 ;
            double southParallal = genv.getMinY() + genv.getHeight()/3 ;

            final Ellipsoid ellipsoid = geoCRS.getDatum().getEllipsoid();

            MathTransformFactory f = DefaultFactories.forBuildin(MathTransformFactory.class);

            ParameterValueGroup p;
            p = f.getDefaultParameters("Albers_Conic_Equal_Area");
            p.parameter("semi_major").setValue(ellipsoid.getSemiMajorAxis());
            p.parameter("semi_minor").setValue(ellipsoid.getSemiMinorAxis());
            p.parameter("central_meridian").setValue(centerMeridian);
            p.parameter("standard_parallel_1").setValue(northParallal);
            p.parameter("standard_parallel_2").setValue(southParallal);

            MathTransform step1 = CRS.findOperation(geomCRS, geoCRS, null).getMathTransform();
            MathTransform step2 = f.createParameterizedTransform(p);
            MathTransform trs = f.createConcatenatedTransform(step1, step2);

            Geometry calculatedGeom = JTS.transform(geom, trs);
            double area = calculatedGeom.getArea();

            if(unit != Units.SQUARE_METRE){
                UnitConverter converter = Units.SQUARE_METRE.getConverterTo(unit);
                area = converter.convert(area);
            }

            return area;

        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (MismatchedDimensionException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return 0;
    }


}
