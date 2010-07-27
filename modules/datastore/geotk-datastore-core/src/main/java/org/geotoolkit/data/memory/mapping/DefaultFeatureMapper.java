/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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
package org.geotoolkit.data.memory.mapping;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.List;
import java.util.Map;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.Converters;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class DefaultFeatureMapper implements FeatureMapper {

    private final SimpleFeatureBuilder builder;
    private final SimpleFeatureType typeSource;
    private final SimpleFeatureType typeTarget;
    private final Map<PropertyDescriptor, Object> defaults;
    private final Map<PropertyDescriptor, List<PropertyDescriptor>> mapping;
    private int id = 1;

    public DefaultFeatureMapper(SimpleFeatureType typeSource, SimpleFeatureType typeTarget,
            Map<PropertyDescriptor, List<PropertyDescriptor>> mapping,
            Map<PropertyDescriptor, Object> defaults) {
        this.typeSource = typeSource;
        this.typeTarget = typeTarget;
        this.mapping = mapping;
        this.defaults = defaults;

        this.builder = new SimpleFeatureBuilder(typeTarget);
    }

    @Override
    public Feature transform(Feature feature) {
        builder.reset();

        //set all default values
        for (final PropertyDescriptor desc : typeTarget.getAttributeDescriptors()) {
            Object val = defaults.get(desc);
            if (val == null) {
                val = ((AttributeDescriptor) desc).getDefaultValue();
            }
            try {
                builder.set(desc.getName(), Converters.convert(val, desc.getType().getBinding()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        for (final PropertyDescriptor sourceDesc : mapping.keySet()) {
            final Object value = feature.getProperty(sourceDesc.getName()).getValue();

            final List<PropertyDescriptor> links = mapping.get(sourceDesc);
            if (links == null || links.isEmpty()) {
                continue;
            }

            for (final PropertyDescriptor targetDesc : links) {
                Object converted = convert(value, sourceDesc, targetDesc);
                if (converted != null) {
                    builder.set(targetDesc.getName(), converted);
                }
            }
        }

        return builder.buildFeature("" + id++);
    }




    private static Object convert(Object value, PropertyDescriptor source, PropertyDescriptor target){

        //special case for geometry attributs
        if(source instanceof GeometryDescriptor){
            final GeometryDescriptor sourceGeomDesc = (GeometryDescriptor) source;
            Geometry candidateGeom = (Geometry) value;

            if(target instanceof GeometryDescriptor){
                //must change geometry type and crs if needed
                final GeometryDescriptor targetGeomDesc = (GeometryDescriptor) target;

                final CoordinateReferenceSystem sourceCRS = sourceGeomDesc.getCoordinateReferenceSystem();
                final CoordinateReferenceSystem targetCRS = targetGeomDesc.getCoordinateReferenceSystem();
                if(!CRS.equalsIgnoreMetadata(sourceCRS,targetCRS)){
                    //crs are different, reproject source geometry
                    try {
                        candidateGeom = JTS.transform(candidateGeom, CRS.findMathTransform(sourceCRS, targetCRS, true));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }

                candidateGeom = convertType(candidateGeom,targetGeomDesc.getType().getBinding());
                return candidateGeom;

            }else{
                //types doesnt match
                return null;
            }
        }else if(target instanceof GeometryDescriptor){
            //source attribut doesnt match
            return null;
        }

        //normal attributs type, string, numbers, dates ...
        try{
            return Converters.convert(value, target.getType().getBinding());
        }catch(Exception ex){
            ex.printStackTrace();
            //could not convert between types
            return null;
        }
    }

    private static Geometry convertType(Geometry geom, Class targetClass){
        if(geom == null) return null;

        if(targetClass.isInstance(geom)){
            return geom;
        }

        if(targetClass == Point.class){
            return convertToPoint(geom);
        }else if(targetClass == MultiPoint.class){
            return convertToMultiPoint(geom);
        }else if(targetClass == LineString.class){
            return convertToLineString(geom);
        }else if(targetClass == MultiLineString.class){
            return convertToMultiLineString(geom);
        }else if(targetClass == Polygon.class){
            return convertToPolygon(geom);
        }else if(targetClass == MultiPolygon.class){
            return convertToMultiPolygon(geom);
        }

        return null;
    }

    private static GeometryFactory GF = new GeometryFactory();

    // Convert to Point --------------------------------------------------------

    private static Point convertToPoint(Geometry geom){
        if(geom instanceof Point){
            return convertToPoint((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToPoint((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToPoint((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToPoint((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToPoint((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToPoint((MultiPolygon)geom);
        }
        return null;
    }

    private static Point convertToPoint(Point pt){
        return pt;
    }

    private static Point convertToPoint(MultiPoint pt){
        return pt.getCentroid();
    }

    private static Point convertToPoint(LineString pt){
        return pt.getCentroid();
    }

    private static Point convertToPoint(MultiLineString pt){
        return pt.getCentroid();
    }

    private static Point convertToPoint(Polygon pt){
        return pt.getCentroid();
    }

    private static Point convertToPoint(MultiPolygon pt){
        return pt.getCentroid();
    }

    // Convert to MultiPoint ---------------------------------------------------

    private static MultiPoint convertToMultiPoint(Geometry geom){
        if(geom instanceof Point){
            return convertToMultiPoint((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToMultiPoint((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToMultiPoint((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToMultiPoint((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToMultiPoint((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToMultiPoint((MultiPolygon)geom);
        }
        return null;
    }

    private static MultiPoint convertToMultiPoint(Point pt){
        return GF.createMultiPoint(new Point[]{pt});
    }

    private static MultiPoint convertToMultiPoint(MultiPoint pt){
        return pt;
    }

    private static MultiPoint convertToMultiPoint(LineString pt){
        return GF.createMultiPoint(pt.getCoordinates());
    }

    private static MultiPoint convertToMultiPoint(MultiLineString pt){
        return GF.createMultiPoint(pt.getCoordinates());
    }

    private static MultiPoint convertToMultiPoint(Polygon pt){
        return GF.createMultiPoint(pt.getCoordinates());
    }

    private static MultiPoint convertToMultiPoint(MultiPolygon pt){
        return GF.createMultiPoint(pt.getCoordinates());
    }

    // Convert to LineString ---------------------------------------------------

    private static LineString convertToLineString(Geometry geom){
        if(geom instanceof Point){
            return convertToLineString((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToLineString((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToLineString((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToLineString((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToLineString((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToLineString((MultiPolygon)geom);
        }
        return null;
    }

    private static LineString convertToLineString(Point pt){
        return GF.createLineString(new Coordinate[]{pt.getCoordinate(),pt.getCoordinate()});
    }

    private static LineString convertToLineString(MultiPoint pt){
        final Coordinate[] coords = pt.getCoordinates();
        if(coords.length == 1){
            return GF.createLineString(new Coordinate[]{coords[0],coords[0]});
        }else{
            return GF.createLineString(coords);
        }
    }

    private static LineString convertToLineString(LineString pt){
        return pt;
    }

    private static LineString convertToLineString(MultiLineString pt){
        return GF.createLineString(pt.getCoordinates());
    }

    private static LineString convertToLineString(Polygon pt){
        return GF.createLineString(pt.getCoordinates());
    }

    private static LineString convertToLineString(MultiPolygon pt){
        return GF.createLineString(pt.getCoordinates());
    }

    // Convert to MultiLineString ----------------------------------------------

    private static MultiLineString convertToMultiLineString(Geometry geom){
        if(geom instanceof Point){
            return convertToMultiLineString((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToMultiLineString((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToMultiLineString((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToMultiLineString((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToMultiLineString((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToMultiLineString((MultiPolygon)geom);
        }
        return null;
    }

    private static MultiLineString convertToMultiLineString(Point pt){
        return convertToMultiLineString(convertToLineString(pt));
    }

    private static MultiLineString convertToMultiLineString(MultiPoint pt){
        return convertToMultiLineString(convertToLineString(pt));
    }

    private static MultiLineString convertToMultiLineString(LineString pt){
        return GF.createMultiLineString(new LineString[]{pt});
    }

    private static MultiLineString convertToMultiLineString(MultiLineString pt){
        return pt;
    }

    private static MultiLineString convertToMultiLineString(Polygon pt){
        return convertToMultiLineString(GF.createLineString(pt.getCoordinates()));
    }

    private static MultiLineString convertToMultiLineString(MultiPolygon pt){
        final int n = pt.getNumGeometries();
        final LineString[] geoms = new LineString[n];
        for(int i=0; i<n;i++){
            geoms[i] = convertToLineString(pt.getGeometryN(i));
        }
        return GF.createMultiLineString(geoms);
    }

    // Convert to Polygon ------------------------------------------------------

    private static Polygon convertToPolygon(Geometry geom){
        if(geom instanceof Point){
            return convertToPolygon((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToPolygon((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToPolygon((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToPolygon((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToPolygon((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToPolygon((MultiPolygon)geom);
        }
        return null;
    }

    private static Polygon convertToPolygon(Point pt){
        LinearRing ring = GF.createLinearRing(new Coordinate[]{pt.getCoordinate(),pt.getCoordinate(),pt.getCoordinate()});
        return GF.createPolygon(ring, new LinearRing[0]);
    }

    private static Polygon convertToPolygon(MultiPoint pt){
        return convertToPolygon(convertToLineString(pt));
    }

    private static Polygon convertToPolygon(LineString pt){
        return GF.createPolygon(GF.createLinearRing(pt.getCoordinates()), new LinearRing[0]);
    }

    private static Polygon convertToPolygon(MultiLineString pt){
        return convertToPolygon(convertToLineString(pt));
    }

    private static Polygon convertToPolygon(Polygon pt){
        return pt;
    }

    private static Polygon convertToPolygon(MultiPolygon pt){
        return convertToPolygon(pt.convexHull());
    }

    // Convert to MultiPolygon -------------------------------------------------

    private static MultiPolygon convertToMultiPolygon(Geometry geom){
        if(geom instanceof Point){
            return convertToMultiPolygon((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToMultiPolygon((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToMultiPolygon((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToMultiPolygon((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToMultiPolygon((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToMultiPolygon((MultiPolygon)geom);
        }
        return null;
    }

    private static MultiPolygon convertToMultiPolygon(Point pt){
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(MultiPoint pt){
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(LineString pt){
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(MultiLineString pt){
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(Polygon pt){
        return GF.createMultiPolygon(new Polygon[]{pt});
    }

    private static MultiPolygon convertToMultiPolygon(MultiPolygon pt){
        return pt;
    }



}
