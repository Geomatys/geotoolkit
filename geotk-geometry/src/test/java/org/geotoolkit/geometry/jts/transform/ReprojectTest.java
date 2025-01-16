/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.geometry.jts.transform;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.factory.GeodeticObjectFactory;
import org.apache.sis.referencing.operation.transform.DefaultMathTransformFactory;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.junit.Assert.*;

/**
 *
 * @author jsorel
 */
public class ReprojectTest {

    @Test
    public void testReproject() throws FactoryException, TransformException{
        //a user reported reprojection cause the geometry first and last point to be different.
        //causing jts to raise an error

        final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

        final LinearRing ring = GF.createLinearRing(new Coordinate[]{
                new Coordinate(29.5314900850289,67.6621244561062),
                new Coordinate(29.5314900850289,80.8837216369139),
                new Coordinate(42.7530872658366,80.8837216369139),
                new Coordinate(42.7530872658366,67.6621244561062),
                new Coordinate(29.5314900850289,67.6621244561062)
        });
        final Polygon poly = GF.createPolygon(ring, new LinearRing[0]);

        final CoordinateReferenceSystem crs1 = CommonCRS.WGS84.normalizedGeographic();
        final CoordinateReferenceSystem crs2 = getLocalLambertCRS(10, 60);

        final MathTransform mt = CRS.findOperation(crs1,crs2, null).getMathTransform();
        final CoordinateSequenceTransformer cst = new CoordinateSequenceMathTransformer(mt);
        final GeometryCSTransformer trs = new GeometryCSTransformer(cst);

        trs.transform(poly);

    }

    @Test
    public void testLinearRingClosing(){
        final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();
        CoordinateSequence sq;
        sq = GF.getCoordinateSequenceFactory().create(new Coordinate[]{
                new Coordinate(29.5314900850289,67.6621244561062,0),
                new Coordinate(29.5314900850289,80.8837216369139,1),
                new Coordinate(42.7530872658366,80.8837216369139,2),
                new Coordinate(42.7530872658366,67.6621244561062,3),
                new Coordinate(29.5314900850290,67.6621244561063,4)
        });

        sq = GeometryCSTransformer.ensureClosed(sq);

        assertTrue(sq.getX(0) == sq.getX(sq.size()-1));
        assertTrue(sq.getY(0) == sq.getY(sq.size()-1));

        //check it does not raise any error
        GF.createLinearRing(sq);

        sq = GF.getCoordinateSequenceFactory().create(5,3);
        sq.setOrdinate(0,0,29.5314900850289);sq.setOrdinate(0, 1, 67.6621244561062);sq.setOrdinate(0, 2, 0);
        sq.setOrdinate(1,0,29.5314900850289);sq.setOrdinate(1, 1, 80.8837216369139);sq.setOrdinate(1, 2, 1);
        sq.setOrdinate(2,0,42.7530872658366);sq.setOrdinate(2, 1, 80.8837216369139);sq.setOrdinate(2, 2, 2);
        sq.setOrdinate(3,0,42.7530872658366);sq.setOrdinate(3, 1, 67.6621244561062);sq.setOrdinate(3, 2, 3);
        sq.setOrdinate(4,0,29.5314900850290);sq.setOrdinate(4, 1, 67.6621244561063);sq.setOrdinate(4, 2, 4);

        sq = GeometryCSTransformer.ensureClosed(sq);

        assertTrue(sq.getX(0) == sq.getX(sq.size()-1));
        assertTrue(sq.getY(0) == sq.getY(sq.size()-1));

        //check it does not raise any error
        GF.createLinearRing(sq);

        sq = GF.getCoordinateSequenceFactory().create(5,3);
        sq.setOrdinate(0,0,29.5314900850289);sq.setOrdinate(0, 1, 67.6621244561062);sq.setOrdinate(0, 2, 0);
        sq.setOrdinate(1,0,29.5314900850289);sq.setOrdinate(1, 1, 80.8837216369139);sq.setOrdinate(1, 2, 1);
        sq.setOrdinate(2,0,42.7530872658366);sq.setOrdinate(2, 1, 80.8837216369139);sq.setOrdinate(2, 2, 2);
        sq.setOrdinate(3,0,42.7530872658366);sq.setOrdinate(3, 1, 67.6621244561062);sq.setOrdinate(3, 2, 3);
        sq.setOrdinate(4,0,29.5314900850289);sq.setOrdinate(4, 1, 67.6621244561062);sq.setOrdinate(4, 2, 4);

        sq = GeometryCSTransformer.ensureClosed(sq);

        assertTrue(sq.getX(0) == sq.getX(sq.size()-1));
        assertTrue(sq.getY(0) == sq.getY(sq.size()-1));

        //check it does not raise any error
        GF.createLinearRing(sq);
    }

    public static ProjectedCRS getLocalLambertCRS(double central_meridan, double latitude_of_origin) {
        try {
            MathTransformFactory mtFactory = DefaultMathTransformFactory.provider();
            var builder = mtFactory.builder("Lambert_Conformal_Conic_1SP");
            ParameterValueGroup parameters = builder.parameters();
            parameters.parameter("central_meridian").setValue(central_meridan);
            parameters.parameter("latitude_of_origin").setValue(latitude_of_origin);
            int centralMeridian  = (int) Math.floor(central_meridan);
            int latitudeOfOrigin = (int) Math.floor(latitude_of_origin);
            DefiningConversion conversion = new DefiningConversion("My conversion", parameters);
            CRSFactory crsFactory = GeodeticObjectFactory.provider();
            final Map<String, Object> properties = new HashMap<>();
            properties.put(ProjectedCRS.NAME_KEY, "LambertCC_" + latitudeOfOrigin + "_" + centralMeridian);
            ProjectedCRS targetCRS = crsFactory.createProjectedCRS(properties, CommonCRS.WGS84.normalizedGeographic(), conversion, PredefinedCS.PROJECTED);
            return targetCRS;
        } catch (Exception ex) {
            //LOGGER.log(Level.WARNING, "Exception ", ex);
            return null;
        }
    }

}
