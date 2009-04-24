

package org.geotoolkit.gui.swing.go.control.information;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import com.vividsolutions.jts.geom.Polygon;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.internal.referencing.CRSUtilities;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MesureUtilities {

    public static double calculateLenght(Geometry geom, CoordinateReferenceSystem geomCRS, Unit<Length> unit){

        if(geom == null || !(geom instanceof LineString)) return 0;

        final LineString line = (LineString) geom;

        Coordinate[] coords = line.getCoordinates();

        try {
            final GeodeticCalculator calculator = new GeodeticCalculator(geomCRS);
            final GeneralDirectPosition pos = new GeneralDirectPosition(geomCRS);

            double lenght = 0;
            for(int i=0,n=coords.length-1;i<n;i++){
                Coordinate coord1 = coords[i];
                Coordinate coord2 = coords[i+1];

                pos.ordinates[0] = coord1.x;
                pos.ordinates[1] = coord1.y;
                calculator.setStartingPosition(pos);
                pos.ordinates[0] = coord2.x;
                pos.ordinates[1] = coord2.y;
                calculator.setDestinationPosition(pos);

                lenght += calculator.getOrthodromicDistance();
            }

            if(unit != SI.METER){
                UnitConverter converter = SI.METER.getConverterTo(unit);
                lenght = converter.convert(lenght);
            }

            return lenght;

        } catch (MismatchedDimensionException ex) {
                Logger.getLogger(MesureUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformException ex) {
            Logger.getLogger(MesureUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    public static double calculateArea(Geometry geom, CoordinateReferenceSystem geomCRS, Unit unit){

        if(geom == null || !(geom instanceof Polygon)) return 0;

        try {
            ReferencedEnvelope env = JTS.toEnvelope(geom);

            final GeographicCRS geoCRS = CRSUtilities.getStandardGeographicCRS2D(geomCRS);

            final MathTransform step0 = CRS.findMathTransform(geomCRS, geoCRS,true);
            Envelope genv = JTS.transform(env, step0);

            double centerMeridian = genv.getWidth()/2 + genv.getMinX();
            double northParallal = genv.getMaxY() - genv.getHeight()/3 ;
            double southParallal = genv.getMinY() + genv.getHeight()/3 ;

            final Ellipsoid ellipsoid = geoCRS.getDatum().getEllipsoid();

            MathTransformFactory f = AuthorityFactoryFinder.getMathTransformFactory(null);

            ParameterValueGroup p;
            p = f.getDefaultParameters("Albers_Conic_Equal_Area");
            p.parameter("semi_major").setValue(ellipsoid.getSemiMajorAxis());
            p.parameter("semi_minor").setValue(ellipsoid.getSemiMinorAxis());
            p.parameter("central_meridian").setValue(centerMeridian);
            p.parameter("standard_parallel_1").setValue(northParallal);
            p.parameter("standard_parallel_2").setValue(southParallal);

            MathTransform step1 = CRS.findMathTransform(geomCRS, geoCRS);
            MathTransform step2 = f.createParameterizedTransform(p);
            MathTransform trs = f.createConcatenatedTransform(step1, step2);
            
            Geometry calculatedGeom = JTS.transform(geom, trs);
            double area = calculatedGeom.getArea();

            if(unit != SI.SQUARE_METRE){
                UnitConverter converter = SI.SQUARE_METRE.getConverterTo(unit);
                area = converter.convert(area);
            }

            return area;

        } catch (FactoryException ex) {
            Logger.getLogger(MesureUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MismatchedDimensionException ex) {
            Logger.getLogger(MesureUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformException ex) {
            Logger.getLogger(MesureUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } 

        return 0;        
    }


}
