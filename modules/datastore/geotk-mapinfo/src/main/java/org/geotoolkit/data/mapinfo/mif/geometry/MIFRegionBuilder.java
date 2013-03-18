package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.geotoolkit.data.mapinfo.mif.style.Brush;
import org.geotoolkit.data.mapinfo.mif.style.Pen;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.*;
import org.opengis.referencing.operation.MathTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFRegionBuilder extends MIFGeometryBuilder {

    public static final Name NAME = new DefaultName("REGION");
    public static final Name PEN_NAME = new DefaultName("PEN");
    public static final Name BRUSH_NAME = new DefaultName("BRUSH");

    private static final AttributeDescriptor BRUSH;
    private static final AttributeDescriptor PEN;

    static {
        final AttributeType penType =
                new DefaultAttributeType(PEN_NAME, Pen.class, false, false, null, null, null);
        PEN = new DefaultAttributeDescriptor(penType, PEN_NAME, 0, 1, true, null);

        final AttributeType smoothType =
                new DefaultAttributeType(BRUSH_NAME, Brush.class, false, false, null, null, null);
        BRUSH = new DefaultAttributeDescriptor(smoothType, BRUSH_NAME, 0, 1, true, null);
    }

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        final int numPolygons = scanner.nextInt();
        final Polygon[] polygons = new Polygon[numPolygons];
        for (int polygonCount = 0; polygonCount < numPolygons; polygonCount++) {

            final int numCoords = scanner.nextInt()*2;
            final double[] polygonPts = new double[numCoords];
            for (int coordCount = 0; coordCount < numCoords; coordCount++) {
                polygonPts[coordCount] = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            }

            final CoordinateSequence seq;
            if (toApply != null) {
                try {
                    double[] afterT = new double[numCoords];
                    toApply.transform(polygonPts, 0, afterT, 0, numCoords / 2);
                    seq = new PackedCoordinateSequence.Double(afterT, 2);
                } catch (Exception e) {
                    throw new DataStoreException("Unable to transform geometry", e);
                }
            } else {
                seq = new PackedCoordinateSequence.Double(polygonPts, 2);
            }
            final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(seq);
            polygons[polygonCount] = GEOMETRY_FACTORY.createPolygon(ring, null);
        }

        toFill.getProperty(NAME).setValue(GEOMETRY_FACTORY.createMultiPolygon(polygons));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toMIFSyntax(Feature geometry) throws DataStoreException {
        super.toMIFSyntax(geometry);
        StringBuilder builder = new StringBuilder(NAME.getLocalPart());

        MultiPolygon multiPolygon = null;
        Object value = geometry.getDefaultGeometryProperty().getValue();
        if(value instanceof Polygon) {
            multiPolygon = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[]{(Polygon)value});
        } else {
            multiPolygon = (MultiPolygon) value;
        }
        builder.append(' ').append(multiPolygon.getNumGeometries()).append('\n');

        for(int i = 0 ; i < multiPolygon.getNumGeometries() ; i++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
            builder.append(polygon.getNumPoints()).append('\n');
            for(Coordinate pt : polygon.getCoordinates()) {
                builder.append(pt.x).append(' ').append(pt.y).append('\n');
            }
        }

        return builder.toString();
    }

    @Override
    public Class getGeometryBinding() {
        return MultiPolygon.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{MultiPolygon.class, Polygon.class};
    }

    @Override
    public Name getName() {
        return NAME;
    }

    @Override
    protected List<AttributeDescriptor> getAttributes() {
        final List<AttributeDescriptor> descList = new ArrayList<AttributeDescriptor>(2);
        descList.add(PEN);
        descList.add(BRUSH);

        return descList;
    }
}
