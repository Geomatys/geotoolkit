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
package org.geotoolkit.processing.vector;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.Unit;
import org.apache.sis.feature.AbstractOperation;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.lang.Static;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.vector.intersect.IntersectDescriptor;
import org.apache.sis.referencing.CRS;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.feature.AttributeType;


/**
 * Set of function and methods useful for vector process
 *
 * @author Quentin Boileau
 */
public final class VectorProcessUtils extends Static {

    private VectorProcessUtils() {
    }

    /**
     * Change the geometry descriptor to Geometry type needed.
     *
     * @param clazz the new type of geometry
     */
    public static FeatureType changeGeometryFeatureType(final FeatureType oldFeatureType, final Class clazz) {
        ArgumentChecks.ensureNonNull("geometry class", clazz);
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(oldFeatureType);
        for(PropertyTypeBuilder pt : ftb.properties()){
            if(pt instanceof AttributeTypeBuilder && Geometry.class.isAssignableFrom( ((AttributeTypeBuilder)pt).getValueClass())){
                ((AttributeTypeBuilder)pt).setValueClass(clazz);
            }
        }
        return ftb.build();
    }

    /**
     * Create a copy of a FeatureType in keeping only one geometry.
     * if keepingGeometry is null, the keeped one will be the default Geometry
     *
     * @return the new FeatureType
     */
    public static FeatureType oneGeometryFeatureType(final FeatureType oldFeatureType, String keepedGeometry) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(oldFeatureType);

        //if keepedGeometry is null we use the default Geometry
        if (keepedGeometry == null) {
            keepedGeometry = AttributeConvention.GEOMETRY_PROPERTY.toString();
        }

        PropertyType property = oldFeatureType.getProperty(keepedGeometry);
        if(property instanceof AbstractOperation){
            final Set<String> deps = ((AbstractOperation)property).getDependencies();
            if(deps.size()==1){
                keepedGeometry = deps.iterator().next();
            }
        }

        final List<PropertyTypeBuilder> listIterator = new ArrayList<>(ftb.properties());
        for (PropertyTypeBuilder pt : listIterator){
            if(pt instanceof AttributeTypeBuilder && Geometry.class.isAssignableFrom( ((AttributeTypeBuilder)pt).getValueClass())){
                if(!pt.getName().toString().equals(keepedGeometry)){
                    ftb.properties().remove(pt);
                }
            }
        }

        return ftb.build();
    }

    /**
     * Create a custom projection (Conic or Mercator) for the geometry using the
     * geometry envelope.
     *
     * @param geomEnvelope Geometry bounding envelope
     * @param longLatCRS WGS84 projection
     * @param unit unit wanted for the geometry
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
     * Get recursively all primaries Geometries contained in the input Geometry.
     *
     * @return a collection of primary geometries
     */
    public static Collection<Geometry> getGeometries(final Geometry inputGeom) {
        final Collection<Geometry> listGeom = new ArrayList<Geometry>();

        //if geometry is a primary type
        if (inputGeom instanceof Polygon || inputGeom instanceof Point
                || inputGeom instanceof LinearRing || inputGeom instanceof LineString)
        {
            listGeom.add(inputGeom);
        }

        //if it's a complex type (Multi... or GeometryCollection)
        if (inputGeom instanceof MultiPolygon || inputGeom instanceof MultiPoint
                || inputGeom instanceof MultiLineString || inputGeom instanceof GeometryCollection)
        {
            for (int i = 0; i < inputGeom.getNumGeometries(); i++) {
                listGeom.addAll(getGeometries(inputGeom.getGeometryN(i)));
            }
        }
        return listGeom;
    }

    /**
     * Compute geometryIntersection between the feature geometry and the clipping geometry
     *
     * @return the intersection Geometry
     * If featureGeometry didn't intersect clippingGeometry the function return null;
     */
    public static Geometry geometryIntersection(final Geometry featureGeometry, final Geometry clippingGeometry) {
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
     *
     * @return the computed geometry. Return the featureGeometry if there is no intersections
     * between geometries. And return null if the featureGeometry is contained into
     * the diffGeometry
     */
    public static Geometry geometryDifference(final Geometry featureGeometry, final Geometry diffGeometry) {
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
     * Re-project a geometry from geometryCRS to wandedCRS. If geometryCRS and wantedCRS are equals,
     * the input geometry will be returned.
     *
     * @return the re-projected Geometry
     */
    public static Geometry repojectGeometry (final CoordinateReferenceSystem wantedCRS, final CoordinateReferenceSystem geometryCRS,
            final Geometry inputGeom) throws TransformException, FactoryException
    {
        if (!(wantedCRS.equals(geometryCRS))) {
            final MathTransform transform = CRS.findOperation(geometryCRS, wantedCRS, null).getMathTransform();
            return JTS.transform(inputGeom, transform);
        } else {
            return inputGeom;
        }
    }

//
//    public static Geometry convertToPolygon(Geometry intersectGeom) {
//        GeometryFactory geomFact = new GeometryFactory();
//        LinearRing ring;
//
//        if(intersectGeom instanceof Point){
//            Point pt = (Point) intersectGeom;
//            ring = geomFact.createLinearRing(new Coordinate[]{
//                new Coordinate(pt.getX(),              pt.getY()),
//                new Coordinate(pt.getX(),              pt.getY()+0.0000000001),
//                new Coordinate(pt.getX()+0.0000000001, pt.getY()+0.0000000001),
//                new Coordinate(pt.getX()+0.0000000001, pt.getY()),
//                new Coordinate(pt.getX(),              pt.getY())
//            });
//            return geomFact.createPolygon(ring, null);
//        }else if(intersectGeom instanceof LineString){
//            LineString line = (LineString) intersectGeom;
//
//            return geomFact.createPolygon(ring, null);
//        }
//
//    }


    /**
     * Compute the intersection geometry between two Features.
     * To determinate which Geometry used from Feature, we use the sourceGeomName and
     * targetGeomName parameters. If input Geometry CRS is different than target one,
     * a conversion into input CRS is done.
     *
     * @param sourceGeomName geometry attribute name to use in sourceFeature
     * @param targetGeomName geometry attribute name to use in targetFeature
     * @return the intersection Geometry.
     */
    public static Geometry intersectionFeatureToFeature(final Feature sourceFeature, final Feature targetFeature,
            final String sourceGeomName, final String targetGeomName) throws FactoryException, TransformException
    {
        Geometry sourceGeometry = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
        CoordinateReferenceSystem sourceCRS = null;

        // found used input geometry with CRS
        for (PropertyType inputProperty : sourceFeature.getType().getProperties(true)) {
            if (AttributeConvention.isGeometryAttribute(inputProperty)) {
                final String name = inputProperty.getName().toString();
                if (name.equals(sourceGeomName)) {
                    sourceGeometry = (Geometry) sourceFeature.getPropertyValue(name);
                    sourceCRS = FeatureExt.getCRS(inputProperty);
                }
            }
        }
        Geometry targetGeometry = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
        CoordinateReferenceSystem targetCRS = null;

        // found used target geometry with CRS
        for (PropertyType inputProperty : targetFeature.getType().getProperties(true)) {
            if (AttributeConvention.isGeometryAttribute(inputProperty)) {
                final String name = inputProperty.getName().toString();
                if (name.equals(targetGeomName)) {
                    targetGeometry = (Geometry) targetFeature.getPropertyValue(name);
                    targetCRS = FeatureExt.getCRS(inputProperty);
                }
            }
        }
        targetGeometry = repojectGeometry(sourceCRS, targetCRS, targetGeometry);
        return sourceGeometry.intersection(targetGeometry);
    }

    /**
     * Compute the intersection between a Feature and a FeatureCollection and return a FeatureCollection
     * where each Feature contained  the intersection geometry as default geometry and other none geometry
     * attributes form input Feature.
     * If a Feature from featureList have many geometries, we concatenate them before compute intersection.
     *
     * @param geometryName the geometry name in inputFeature to compute the intersection
     * @return a FeatureCollection of intersection Geometry. The FeatureCollection ID is "inputFeatureID-intersection"
     * The Feature returned ID will look like "inputFeatureID<->intersectionFeatureID"
     */
    public static FeatureCollection intersectionFeatureToColl(final Feature inputFeature,
            final FeatureCollection featureList, String geometryName)
            throws FactoryException, TransformException, ProcessException {

        //if the wanted feature geometry is null, we use the default geometry
        if (geometryName == null) {
            geometryName = AttributeConvention.GEOMETRY_PROPERTY.toString();
        }

        //create the new FeatureType with only one geometry property
        final FeatureType newType = VectorProcessUtils.oneGeometryFeatureType(inputFeature.getType(), geometryName);

        //name of the new collection "<inputFeatureID>-intersection"
        final FeatureCollection resultFeatureList =
                FeatureStoreUtilities.collection(FeatureExt.getId(inputFeature).getID() + "-intersection", newType);

        Geometry inputGeometry = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
        CoordinateReferenceSystem inputCRS = null;

        // found used input geometry with CRS
        for (PropertyType inputProperty : inputFeature.getType().getProperties(true)) {
            if (AttributeConvention.isGeometryAttribute(inputProperty)) {
                final String name = inputProperty.getName().toString();
                if (name.equals(geometryName)) {
                    inputGeometry = (Geometry) inputFeature.getPropertyValue(name);
                    inputCRS = FeatureExt.getCRS(inputProperty);
                }
            }
        }

        //lauch Intersect process to get all features which intersect the inputFeature geometry
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, IntersectDescriptor.NAME);
        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter(IntersectDescriptor.FEATURE_IN.getName().getCode()).setValue(featureList);
        in.parameter(IntersectDescriptor.GEOMETRY_IN.getName().getCode()).setValue(inputGeometry);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //get all Features which intersects the intput Feature geometry
        final FeatureCollection featuresOut = (FeatureCollection) proc.call().parameter(
                IntersectDescriptor.FEATURE_OUT.getName().getCode()).getValue();

        if (featuresOut.isEmpty()) {
            //return an empty FeatureCollection
            return resultFeatureList;
        } else {

            //loop in resulting FeatureCollection
            try (final FeatureIterator ite = featuresOut.iterator()) {
                while (ite.hasNext()) {

                    //get the next Feature which intersect the inputFeature
                    final Feature outFeature = ite.next();
                    final Map<Geometry, CoordinateReferenceSystem> mapGeomCRS = new HashMap<Geometry, CoordinateReferenceSystem>();

                    //generate a map with all feature geometry and geometry CRS
                    for (PropertyType outProperty : outFeature.getType().getProperties(true)) {
                        if (AttributeConvention.isGeometryAttribute(outProperty)) {
                            final Geometry outGeom = (Geometry) outFeature.getPropertyValue(outProperty.getName().toString());
                            final CoordinateReferenceSystem outputCRS = FeatureExt.getCRS(outProperty);
                            mapGeomCRS.put(outGeom, outputCRS);
                        }
                    }

                    //get the first geometry CRS in the map. It'll be used to homogenize the Feature geometries CRS
                    final CoordinateReferenceSystem outputBaseCRS = mapGeomCRS.entrySet().iterator().next().getValue();
                    Geometry interGeom = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
                    Geometry interGeomBuffer = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);

                    //for each Feature Geometry
                    for (Map.Entry<Geometry, CoordinateReferenceSystem> entry : mapGeomCRS.entrySet()) {
                        Geometry geom = entry.getKey();
                        final CoordinateReferenceSystem geomCRS = entry.getValue();

                        //if geometry is not null
                        if (geom != null) {
                            //reproject geom into outputBaseCRS
                            geom = repojectGeometry(outputBaseCRS, geomCRS, geom);

                            //get all geometries recursively
                            final Collection<Geometry> subGeometry = getGeometries(geom);

                            //each sub geometries
                            for (Geometry aGeometry : subGeometry) {
                                //reproject aGeometry into inputCRS
                                aGeometry = repojectGeometry(inputCRS, outputBaseCRS, aGeometry);

                                //concatenate all intersections between this geometry and the inputGeometry
                                interGeomBuffer = interGeomBuffer.union(inputGeometry.intersection(aGeometry));
                            }
                            //concatenate all intersections between Feature geometries and the inputGeometry
                            interGeom = interGeom.union(interGeomBuffer);
                        }
                    }

                    //create the result Feature
                    final Feature resultFeature = newType.newInstance();
                    resultFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),
                            FeatureExt.getId(inputFeature).getID() + "<->" + FeatureExt.getId(outFeature).getID());

                    for (PropertyType property : inputFeature.getType().getProperties(true)) {
                        final String name = property.getName().toString();
                        if (AttributeConvention.isGeometryAttribute(property)) {
                            if (name.equals(geometryName)) {
                                //set the intersection as the feature Geometry
                                resultFeature.setPropertyValue(name, interGeom);
                            }
                        } else if(property instanceof AttributeType && !(AttributeConvention.contains(property.getName()))){
                            resultFeature.setPropertyValue(name, inputFeature.getPropertyValue(name));
                        }
                    }
                    resultFeatureList.add(resultFeature);
                }
            }
        }
        return resultFeatureList;
    }
}
