package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.*;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Build features representing MIF Collection geometries.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class MIFCollectionBuilder extends MIFGeometryBuilder {
    public static final Name NAME = new DefaultName("COLLECTION");
    public static final Name GEOM_NAME = new DefaultName("GEOMETRY");

    private CoordinateReferenceSystem collectionCRS = null;
    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        int numGeom = 0;
        try {
            numGeom = scanner.nextInt();
        } catch (Exception e) {
            throw new DataStoreException("Number of geometries in Collection is not specified", e);
        }

        for(int geomCount=0 ; geomCount < numGeom ; geomCount++ ) {
            while(scanner.hasNextLine()) {
                final String tmpWord = scanner.findInLine("\\w+");
                final MIFUtils.GeometryType enumType = MIFUtils.getGeometryType(tmpWord);
                if (enumType != null) {
                    final FeatureType type = enumType.getBinding(collectionCRS, null);
                    final Feature currentFeature = FeatureUtilities.defaultFeature(type, tmpWord+geomCount);
                    enumType.readGeometry(scanner, toFill, toApply);
                    toFill.getProperties(GEOM_NAME).add(currentFeature);
                    break;
                }
            }
        }
    }

    @Override
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        AttributeType type = new DefaultAttributeType(NAME, Feature.class, false, false, null, null, null);
        AttributeDescriptor desc = new DefaultAttributeDescriptor(type, NAME, 1, 3, false, null);

        collectionCRS = crs;

        return new DefaultSimpleFeatureType(NAME, Collections.singletonList(desc), null, false, null, parent, null);
    }

    @Override
    public String toMIFSyntax(Feature geometry) throws DataStoreException {
        super.toMIFSyntax(geometry);

        StringBuilder builder = new StringBuilder(NAME.getLocalPart()).append(' ');
        if(geometry.getProperty(GEOM_NAME) != null) {
            Object value = geometry.getProperty(GEOM_NAME).getValue();
            if(value instanceof Feature) {
                featureToMIFCollection((Feature)value, builder);
            } else if (value instanceof GeometryCollection) {
                jtsToMIFCollection((GeometryCollection)value, builder);
            }
        } else if(geometry.getDefaultGeometryProperty().getValue() instanceof GeometryCollection) {
            final GeometryCollection source = (GeometryCollection) geometry.getDefaultGeometryProperty().getValue();
            jtsToMIFCollection(source, builder);
        } else {
             throw new DataStoreException("Incompatible geometry type.");
        }

        return builder.toString();
    }


    private void jtsToMIFCollection(GeometryCollection source, StringBuilder builder) throws DataStoreException {
        final ArrayList<Polygon> polygons = new ArrayList<Polygon>();
        final ArrayList<LineString> lines = new ArrayList<LineString>();
        final ArrayList<Point> points = new ArrayList<Point>();

        sortGeometries(source, polygons, lines, points);

        int count = 0;
        ArrayList<String> geomsStr = new ArrayList<String>();
        if(polygons.size() > 0) {
            count++;
            final MultiPolygon multiPolygon = GEOMETRY_FACTORY.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
            final FeatureType type = MIFUtils.GeometryType.REGION.getBinding(collectionCRS, null);
            final Feature feature = FeatureUtilities.defaultFeature(type, "polygons");
            feature.getDefaultGeometryProperty().setValue(multiPolygon);
            geomsStr.add(MIFUtils.GeometryType.REGION.toMIFSyntax(feature));
        }
        if(lines.size() > 0) {
            count++;
            final MultiLineString multiLine = GEOMETRY_FACTORY.createMultiLineString(lines.toArray(new LineString[lines.size()]));
            final FeatureType type = MIFUtils.GeometryType.PLINE.getBinding(collectionCRS, null);
            final Feature feature = FeatureUtilities.defaultFeature(type, "lines");
            feature.getDefaultGeometryProperty().setValue(multiLine);
            geomsStr.add(MIFUtils.GeometryType.PLINE.toMIFSyntax(feature));
        }
        if(points.size() > 0) {
            count++;
            final MultiPoint multiPoint = GEOMETRY_FACTORY.createMultiPoint(points.toArray(new Point[points.size()]));
            final FeatureType type = MIFUtils.GeometryType.PLINE.getBinding(collectionCRS, null);
            final Feature feature = FeatureUtilities.defaultFeature(type, "points");
            feature.getDefaultGeometryProperty().setValue(multiPoint);
            geomsStr.add(MIFUtils.GeometryType.PLINE.toMIFSyntax(feature));
        }

        builder.append(count).append('\n');
        for(String mifMulti : geomsStr) {
            builder.append(mifMulti).append('\n');
        }
    }

    /**
     * MIF defines geometry collection as :
     * 0 or 1 region,
     * 0 or 1 polyline,
     * 0 or 1 multipoint.
     * So, we have to parse source geometries and group them per type.
     *
     * @param source The collection of JTS geometries to create MIF collection with.
     * @param polygons list to store region.
     * @param lines list to store polyline.
     * @param points list to store multipoint.
     */
    private void sortGeometries(GeometryCollection source, List<Polygon> polygons, List<LineString> lines, List<Point> points) {
        for(int i = 0 ; i < source.getNumGeometries() ; i++) {
            final Geometry geom = source.getGeometryN(i);
            if(geom instanceof Polygon) {
                polygons.add((Polygon) geom);
            } else if(geom instanceof LineString) {
                lines.add((LineString) geom);
            } else if(geom instanceof Point) {
                points.add((Point) geom);
            } else if(geom instanceof GeometryCollection) {
                sortGeometries((GeometryCollection) geom, polygons, lines, points);
            }
        }
    }

    private void featureToMIFCollection(Feature source, StringBuilder builder) {

    }

    @Override
    public Class getGeometryBinding() {
        return Feature.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{Feature.class, GeometryCollection.class};
    }

    @Override
    public Name getName() {
        return NAME;
    }

    @Override
    protected List<AttributeDescriptor> getAttributes() {
        AttributeType type = new DefaultAttributeType(NAME, Feature.class, false, false, null, null, null);
        AttributeDescriptor desc = new DefaultAttributeDescriptor(type, NAME, 1, 3, false, null);
        return Collections.singletonList(desc);
    }
}
