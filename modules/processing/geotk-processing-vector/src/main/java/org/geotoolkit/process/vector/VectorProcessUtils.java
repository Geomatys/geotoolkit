/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.vector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import javax.management.Descriptor;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.lang.Static;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.vector.intersect.IntersectDescriptor;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Set of function and methods useful for vector process
 * @author Quentin Boileau
 * @module pending
 */
@Static
public final class VectorProcessUtils {

    private VectorProcessUtils() {
    }

    /**
     * Change the geometry descriptor to Geometry type needed.
     * @param oldFeatureType FeatureType
     * @param class the new type of geometry
     * @return newFeatureType FeatureType
     */
    public static FeatureType changeGeometryFeatureType(final FeatureType oldFeatureType, final Class clazz) {

        AttributeDescriptorBuilder descBuilder;
        AttributeTypeBuilder typeBuilder;

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.copy(oldFeatureType);

        final ListIterator<PropertyDescriptor> ite = ftb.getProperties().listIterator();

        while (ite.hasNext()) {

            final PropertyDescriptor desc = ite.next();
            if (desc instanceof GeometryDescriptor) {

                final GeometryType type = (GeometryType) desc.getType();

                descBuilder = new AttributeDescriptorBuilder();
                typeBuilder = new AttributeTypeBuilder();
                descBuilder.copy((AttributeDescriptor) desc);
                typeBuilder.copy(type);
                typeBuilder.setBinding(clazz);
                descBuilder.setType(typeBuilder.buildGeometryType());
                final PropertyDescriptor newDesc = descBuilder.buildDescriptor();
                ite.set(newDesc);
            }
        }

        return ftb.buildFeatureType();
    }
    
    
    /**
     *
     * @param oldFeatureType
     * @param keepedGeometry
     * @return
     */
    public static FeatureType oneGeometryFeatureType(final FeatureType oldFeatureType, String keepedGeometry) {

        AttributeDescriptorBuilder descBuilder;
        AttributeTypeBuilder typeBuilder;

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.copy(oldFeatureType);

        //if keepedGeometry is null we use the default Geometry
        if(keepedGeometry == null){
            keepedGeometry = ftb.getDefaultGeometry().getLocalPart();
        }
        
        final Collection<String> listToRemove = new ArrayList<String>();

        final ListIterator<PropertyDescriptor> ite = ftb.getProperties().listIterator();
        while (ite.hasNext()) {

            final PropertyDescriptor desc = ite.next();
            if (desc instanceof GeometryDescriptor) {
                
                final GeometryType type = (GeometryType) desc.getType();

                if(desc.getName().getLocalPart().equals(keepedGeometry)){
                    descBuilder = new AttributeDescriptorBuilder();
                    typeBuilder = new AttributeTypeBuilder();
                    descBuilder.copy((AttributeDescriptor) desc);
                    typeBuilder.copy(type);
                    typeBuilder.setBinding(Geometry.class);
                    descBuilder.setType(typeBuilder.buildGeometryType());
                    final PropertyDescriptor newDesc = descBuilder.buildDescriptor();
                    ite.set(newDesc);
                }
                else{
                    listToRemove.add(desc.getName().getLocalPart());
                }
            }
        }
        
        ftb.setDefaultGeometry(keepedGeometry);
        for (String delPropertyDesc : listToRemove) {
            ftb.remove(delPropertyDesc);
        }


        return ftb.buildFeatureType();
    }

    
    /**
     * Create a custom projection (Conic or Mercator) for the geometry using the
     * geometry envelope.
     * @param geomEnvelope Geometry bounding envelope
     * @param longLatCRS WGS84 projection
     * @param unit unit wanted for the geometry
     * @return MathTransform
     * @throws NoSuchIdentifierException
     * @throws FactoryException
     */
    public static MathTransform changeProjection(final Envelope geomEnvelope, final GeographicCRS longLatCRS,
            final Unit<Length> unit) throws FactoryException {

        //collect data to create the projection
        final double centerMeridian = geomEnvelope.getWidth() / 2 + geomEnvelope.getMinX();
        final double centerParallal = geomEnvelope.getHeight() / 2 + geomEnvelope.getMinY();
        final double northParallal = geomEnvelope.getMaxY() - geomEnvelope.getHeight() / 3;
        final double southParallal = geomEnvelope.getMinY() + geomEnvelope.getHeight() / 3;

        boolean conicProjection = true;
        //if the geomery is near the equator we use the mercator projection
        if (geomEnvelope.getMaxY() > 0 && geomEnvelope.getMinY() < 0) {
            conicProjection = false;
        }
        //conicProjection = true;

        //create geometry lambert projection or mercator projection
        final Ellipsoid ellipsoid = longLatCRS.getDatum().getEllipsoid();
        double semiMajorAxis = ellipsoid.getSemiMajorAxis();
        double semiMinorAxis = ellipsoid.getSemiMinorAxis();

        final Unit<Length> projectionUnit = ellipsoid.getAxisUnit();
        //check for unit conversion
        if (unit != projectionUnit) {
            final UnitConverter converter = projectionUnit.getConverterTo(unit);
            semiMajorAxis = converter.convert(semiMajorAxis);
            semiMinorAxis = converter.convert(semiMinorAxis);
        }

        final MathTransformFactory f = AuthorityFactoryFinder.getMathTransformFactory(null);
        ParameterValueGroup p;
        if (conicProjection) {

            p = f.getDefaultParameters("Albers_Conic_Equal_Area");
            p.parameter("semi_major").setValue(semiMajorAxis);
            p.parameter("semi_minor").setValue(semiMinorAxis);
            p.parameter("central_meridian").setValue(centerMeridian);
            p.parameter("standard_parallel_1").setValue(northParallal);
            p.parameter("standard_parallel_2").setValue(southParallal);
        } else {

            p = f.getDefaultParameters("Mercator_2SP");
            p.parameter("semi_major").setValue(semiMajorAxis);
            p.parameter("semi_minor").setValue(semiMinorAxis);
            p.parameter("central_meridian").setValue(centerMeridian);
            p.parameter("standard_parallel_1").setValue(centerParallal);
        }

        return f.createParameterizedTransform(p);
    }

    /**
     * Compute clipping between the feature geometry and the clipping geometry
     * @param featureGeometry Geometry
     * @param clippingGeometry Geometry
     * @return the intersection Geometry
     * If featureGeometry didn't intersect clippingGeometry the function return null;
     */
    public static Geometry clipping(final Geometry featureGeometry, final Geometry clippingGeometry) {
        if (featureGeometry == null || clippingGeometry == null) {
            return null;
        }

        if (featureGeometry.intersects(clippingGeometry)) {
            return featureGeometry.intersection(clippingGeometry);
        } else {
            return null;
        }
    }

    /**
     * Compute difference between the feature's geometry and the geometry
     * @param featureGeometry Geometry
     * @param diffGeometry Geometry
     * @return the computed geometry. Return the featureGeometry if there is no intersections
     * between geometries. And return null if the featureGeometry is contained into
     * the diffGeometry
     */
    public static Geometry difference(final Geometry featureGeometry, final Geometry diffGeometry) {
        if (featureGeometry == null || diffGeometry == null) {
            return null;
        }

        if (featureGeometry.intersects(diffGeometry)) {
            if (diffGeometry.contains(featureGeometry)) {
                return null;
            } else {
                return featureGeometry.difference(diffGeometry);
            }
        } else {
            return featureGeometry;
        }
    }


    /**
     * Get recursively all primaries Geometries contained in the input Geometry.
     * @param inputGeom
     * @return a collection of primary geometries
     */
    public static Collection<Geometry> getGeometries(Geometry inputGeom) {

        Collection<Geometry> listGeom = new ArrayList<Geometry>();

        //if geometry is a primary type
        if (inputGeom instanceof Polygon || inputGeom instanceof Point
                || inputGeom instanceof LinearRing || inputGeom instanceof LineString) {
            listGeom.add(inputGeom);
        }

        //if it's a complex type (Multi... or GeometryCollection)
        if (inputGeom instanceof MultiPolygon || inputGeom instanceof MultiPoint
                || inputGeom instanceof MultiLineString || inputGeom instanceof GeometryCollection) {

            for (int i = 0; i < inputGeom.getNumGeometries(); i++) {
                listGeom.addAll(getGeometries(inputGeom.getGeometryN(i)));
            }
        }

        return listGeom;
    }

    /**
     * Convert a geometry to a MultiPolygon. If inputGeometry is a Multi... something or a GeometryCollection,
     * the function use the <code>getGeometries</code> function to get all geometries before covert them.
     * @param inputGeom
     * @return a MultiPolygon
     */
    public static MultiPolygon convertGeomToPolygon(Geometry inputGeom) {

        final GeometryFactory GF = new GeometryFactory();

        MultiPolygon multiPoly;

        final Collection<Geometry> listGeom = getGeometries(inputGeom);
        final Collection<Polygon> multiPolyArray = new ArrayList<Polygon>();
        final Iterator<Geometry> ite = listGeom.iterator();


        while (ite.hasNext()) {
            final Geometry aGeom = ite.next();

            // Polygon
            if (aGeom instanceof Polygon) {
                multiPolyArray.add((Polygon) aGeom);
            }

            //LinearRing
            if (aGeom instanceof LinearRing) {
                multiPolyArray.add(GF.createPolygon((LinearRing) aGeom, null));
            }

            final Coordinate[] coordinates = aGeom.getCoordinates();
            final int nbCoord = coordinates.length;

            Coordinate[] polygonCoordianates;

            //Point
            if (aGeom instanceof Point) {
                polygonCoordianates = new Coordinate[]{
                            coordinates[0],
                            coordinates[0],
                            coordinates[0],
                            coordinates[0]
                        };

                multiPolyArray.add(GF.createPolygon(GF.createLinearRing(polygonCoordianates), null));
            }

            //LineString
            if (aGeom instanceof LineString) {
                polygonCoordianates = new Coordinate[(nbCoord * 2)];
                //copy of array
                System.arraycopy(coordinates, 0, polygonCoordianates, 0, nbCoord);

                int index = nbCoord;

                //adding the reverse array
                for (int i = (nbCoord - 1) ; i >=0  ; i--){
                    polygonCoordianates[index] = coordinates[i];
                    index++;
                }
                multiPolyArray.add(GF.createPolygon(GF.createLinearRing(polygonCoordianates), null));
            }
        }
        final Polygon[] bufferArray = multiPolyArray.toArray(new Polygon[multiPolyArray.size()]);

        return GF.createMultiPolygon(bufferArray);
    }

    /**
     * Compute the intersection between a Feature and a FeatureCollection and return a FeatureCollection
     * where each Feature geometry are the intersection.
     * If a feature have many geometries, we concatenate them before compute intersection.
     * @param inputFeature
     * @param featureList
     * @return a FeatureCollection of intersection Geometry. The FeatureCollection ID is "<inputFeatureID>-intersection"
     */
    public static FeatureCollection<Feature> intersection(final Feature inputFeature,
            final FeatureCollection<Feature> featureList) {

        final FeatureType newType = VectorProcessUtils.changeGeometryFeatureType(inputFeature.getType(), Geometry.class);


        //name of the new collection "<inputFeatureID>-intersection"
        final FeatureCollection<Feature> resultFeatureList =
                DataUtilities.collection(inputFeature.getIdentifier().getID() + "-intersection", newType);

        Geometry inputGeometry = null;
        // concat all inputFeature geometries
        for (Property inputProperty : inputFeature.getProperties()) {
            if (inputProperty.getDescriptor() instanceof GeometryDescriptor) {
                 if (inputGeometry != null) {
                     inputGeometry = inputGeometry.union((Geometry) inputProperty.getValue());
                 } else {
                     inputGeometry = (Geometry) inputProperty.getValue();
                 }
            }
        }
        

        //lauch Intersect process to get all features which intersect the inputFeature geometry
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(VectorProcessFactory.NAME, IntersectDescriptor.NAME);
        final org.geotoolkit.process.Process proc = desc.createProcess();
        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter(IntersectDescriptor.FEATURE_IN.getName().getCode()).setValue(featureList);
        in.parameter(IntersectDescriptor.GEOMETRY_IN.getName().getCode()).setValue(inputGeometry);
        proc.setInput(in);
        proc.run();
        
        //get all Feature which intersect the intput Feature geometry
        final FeatureCollection<Feature> featuresOut = (FeatureCollection<Feature>) proc.getOutput().parameter(IntersectDescriptor.FEATURE_OUT.getName().getCode()).getValue();

        if (featuresOut.isEmpty()) {
            //return an empty FeatureCollection
            return resultFeatureList;
        } else {

            //loop in resulting FeatureCollection
            final FeatureIterator<Feature> ite = featuresOut.iterator();
            int num = 0;
            try {
                while (ite.hasNext()) {

                    final Feature outFeature = ite.next();

                    Geometry outGeometry = null;
                    // concat all outFeature geometries
                    for (Property outProperty : outFeature.getProperties()) {
                        if (outProperty.getDescriptor() instanceof GeometryDescriptor) {
                            if (outGeometry != null) {
                                outGeometry = outGeometry.union((Geometry) outProperty.getValue());
                            } else {
                                outGeometry = (Geometry) outProperty.getValue();
                            }
                        }
                    }
                    //conversion into MultiPolygon
                    MultiPolygon multiPolyOut = convertGeomToPolygon(outGeometry);

                    //create the result Feature
                    final Feature resultFeature = FeatureUtilities.defaultFeature(newType,
                            inputFeature.getIdentifier().getID() + "-" + num);

                    for (Property property : inputFeature.getProperties()) {
                        if (property.getDescriptor() instanceof GeometryDescriptor) {
                            //set the intersection as the feature Geometry
                            resultFeature.getProperty(property.getName()).setValue(inputGeometry.intersection(multiPolyOut));
                        } else {

                            resultFeature.getProperty(property.getName()).setValue(property.getValue());
                        }
                    }
                    num++;
                    resultFeatureList.add(resultFeature);
                }
            } finally {
                ite.close();
            }
        }

        return resultFeatureList;
    }


     /**
     * Compute the intersection between a Feature and a FeatureCollection and return a FeatureCollection
     * where each Feature geometry are the intersection.
     * If a feature have many geometries, we concatenate them before compute intersection.
     * @param inputFeature
     * @param featureList
     * @return a FeatureCollection of intersection Geometry. The FeatureCollection ID is "<inputFeatureID>-intersection"
     */
    public static FeatureCollection<Feature> intersection2(final Feature inputFeature,
            final FeatureCollection<Feature> featureList, String geometryName) 
            throws FactoryException, MismatchedDimensionException, TransformException {

        //if the wanted feature geometry is null, we use the default geometry
        if(geometryName == null){
            geometryName = inputFeature.getDefaultGeometryProperty().getName().getLocalPart();
        }

        //create the new FeatureType with only one geometry property
        final FeatureType newType = VectorProcessUtils.oneGeometryFeatureType(inputFeature.getType(), geometryName);

        //name of the new collection "<inputFeatureID>-intersection"
        final FeatureCollection<Feature> resultFeatureList =
                DataUtilities.collection(inputFeature.getIdentifier().getID() + "-intersection", newType);

        Geometry inputGeometry = null;
        CoordinateReferenceSystem inputCRS = null;
        // concat all inputFeature geometries
        for (Property inputProperty : inputFeature.getProperties()) {
            if (inputProperty.getDescriptor() instanceof GeometryDescriptor) {
                if(inputProperty.getName().getLocalPart().equals(geometryName)){
                    inputGeometry = (Geometry) inputProperty.getValue();
                    final GeometryDescriptor geomDesc = (GeometryDescriptor)inputProperty.getDescriptor();
                    inputCRS = geomDesc.getCoordinateReferenceSystem();
                }
            }
        }

        //lauch Intersect process to get all features which intersect the inputFeature geometry
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(VectorProcessFactory.NAME, IntersectDescriptor.NAME);
        final org.geotoolkit.process.Process proc = desc.createProcess();
        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter(IntersectDescriptor.FEATURE_IN.getName().getCode()).setValue(featureList);
        in.parameter(IntersectDescriptor.GEOMETRY_IN.getName().getCode()).setValue(inputGeometry);
        proc.setInput(in);
        proc.run();

        //get all Features which intersects the intput Feature geometry
        final FeatureCollection<Feature> featuresOut = (FeatureCollection<Feature>)
                proc.getOutput().parameter(IntersectDescriptor.FEATURE_OUT.getName().getCode()).getValue();

        if (featuresOut.isEmpty()) {
            //return an empty FeatureCollection
            return resultFeatureList;
        } else {

            //loop in resulting FeatureCollection
            final FeatureIterator<Feature> ite = featuresOut.iterator();
            int num = 0;
            try {
                while (ite.hasNext()) {

                    final Feature outFeature = ite.next();

                    Geometry outGeometry = null;
                    CoordinateReferenceSystem outputCRS = null;

                    // concat all outFeature geometries
                    for (Property outProperty : outFeature.getProperties()) {
                        if (outProperty.getDescriptor() instanceof GeometryDescriptor) {
                            
                            // Others geometries : get there CRS and convert them into first geometry CRS found
                            // and generate an union
                            if (outGeometry != null) {
                                final GeometryDescriptor geomDescOut = (GeometryDescriptor)outProperty.getDescriptor();
                                Geometry newOutGeometry = (Geometry) outProperty.getValue();
                                if(newOutGeometry != null){
                                    final MathTransform transform = CRS.findMathTransform(outputCRS, geomDescOut.getCoordinateReferenceSystem());
                                    newOutGeometry = JTS.transform(newOutGeometry, transform);
                                    outGeometry = outGeometry.union(newOutGeometry);
                                }
                                
                            } else {// First geometry set in outGeometry and store the geometry CRS
                                outGeometry = (Geometry) outProperty.getValue();
                                final GeometryDescriptor geomDescOut = (GeometryDescriptor)outProperty.getDescriptor();
                                outputCRS = geomDescOut.getCoordinateReferenceSystem();
                            }
                        }
                    }
                    //conversion into MultiPolygon
                    MultiPolygon multiPolyOut = convertGeomToPolygon(outGeometry);

                    //create the result Feature
                    final Feature resultFeature = FeatureUtilities.defaultFeature(newType,
                            inputFeature.getIdentifier().getID() + "-" + num);

                    for (Property property : inputFeature.getProperties()) {
                        if (property.getDescriptor() instanceof GeometryDescriptor) {
                            if(property.getName().getLocalPart().equals(geometryName)){

                                if(!(outputCRS.equals(inputCRS))){
                                    final MathTransform transformToOriginal = CRS.findMathTransform(inputCRS, outputCRS);
                                    multiPolyOut = (MultiPolygon) JTS.transform((Geometry)multiPolyOut, transformToOriginal);
                                }
                                System.out.println("####### inputGeom " + inputGeometry);
                                System.out.println("####### outPGeom " + multiPolyOut);
                                //set the intersection as the feature Geometry
                                resultFeature.getProperty(property.getName()).setValue(inputGeometry.intersection(multiPolyOut));
                            }
                        } else {
                            
                            resultFeature.getProperty(property.getName()).setValue(property.getValue());
                        }
                    }
                    num++;
                    resultFeatureList.add(resultFeature);
                }
            } finally {
                ite.close();
            }
        }

        return resultFeatureList;
    }
}
