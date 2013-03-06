package org.geotoolkit.data.mif.geometry;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.geotoolkit.data.mif.style.Font;
import org.geotoolkit.data.mif.style.LabelLine;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.feature.type.DefaultGeometryDescriptor;
import org.geotoolkit.feature.type.DefaultGeometryType;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFTextBuilder extends MIFGeometryBuilder {
    public static final Name NAME = new DefaultName("ENVELOPE");
    public static final Name TEXT_NAME = new DefaultName("TEXT");
    public static final Name FONT_NAME = new DefaultName("FONT");
    public static final Name SPACING_NAME = new DefaultName("SPACING");
    public static final Name ANGLE_NAME = new DefaultName("ANGLE");
    public static final Name LABEL_NAME = new DefaultName("LABEL");

    public static final AttributeDescriptor TEXT_DESCRIPTOR;
    public static final AttributeDescriptor FONT_DESCRIPTOR;
    public static final AttributeDescriptor SPACING_DESCRIPTOR;
    public static final AttributeDescriptor ANGLE_DESCRIPTOR;
    public static final AttributeDescriptor LABEL_DESCRIPTOR;

    private SimpleFeatureType featureType = null;

    static {
        final DefaultAttributeType textType = new DefaultAttributeType(TEXT_NAME, String.class, true, false, null, null, null);
        TEXT_DESCRIPTOR = new DefaultAttributeDescriptor(textType, TEXT_NAME, 1, 1, false, "No data");

        final DefaultAttributeType fontType = new DefaultAttributeType(FONT_NAME, Font.class, true, false, null, null, null);
        FONT_DESCRIPTOR = new DefaultAttributeDescriptor(fontType, FONT_NAME, 0, 1, true, null);

        final DefaultAttributeType spacingType = new DefaultAttributeType(SPACING_NAME, Float.class, true, false, null, null, null);
        SPACING_DESCRIPTOR = new DefaultAttributeDescriptor(spacingType, SPACING_NAME, 1, 1, false, 1.0);

        final DefaultAttributeType angleType = new DefaultAttributeType(ANGLE_NAME, Double.class, true, false, null, null, null);
        ANGLE_DESCRIPTOR = new DefaultAttributeDescriptor(fontType, ANGLE_NAME, 0, 1, true, null);

        final DefaultAttributeType labelType = new DefaultAttributeType(LABEL_NAME, LabelLine.class, true, false, null, null, null);
        LABEL_DESCRIPTOR = new DefaultAttributeDescriptor(fontType, LABEL_NAME, 0, 1, true, null);
    }

    /**
     * Build a feature describing a MIF point geometry. That assume that user gave a {@link Scanner} which is placed on
     * a POINT tag.
     *
     * @param scanner The scanner to use for data reading (must be pointing on a mif POINT element).
     * @param toFill
     * @param toApply
     * @return a {@link SimpleFeature} matching the {@link SimpleFeatureType} given by
     *         {@link MIFPointBuilder#buildType(org.opengis.referencing.crs.CoordinateReferenceSystem, org.opengis.feature.type.FeatureType)} .
     * @throws DataStoreException If there's a problem while parsing stream of the given Scanner.
     */
    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        try {
            String geomText = scanner.next("\\w+");
            if (TEXT_NAME.getLocalPart().equalsIgnoreCase(geomText)) {
                geomText = scanner.next("\\w+");
            }
            toFill.getProperty(TEXT_NAME).setValue(geomText);

            final double[] pts = new double[4];

            for (int i = 0; i < pts.length; i++) {
                pts[i] = scanner.nextDouble();
            }

            final CoordinateSequence seq;
            if (toApply != null) {
                try {
                    double[] afterT = new double[4];
                    toApply.transform(pts, 0, afterT, 0, 2);
                    seq = new PackedCoordinateSequence.Double(afterT, 2);
                } catch (Exception e) {
                    throw new DataStoreException("Unable to transform geometry", e);
                }
            } else {
                seq = new PackedCoordinateSequence.Double(pts, 2);
            }
            final Envelope env = new Envelope(seq.getCoordinate(0), seq.getCoordinate(1));

            toFill.getProperty(NAME).setValue(env);
        } catch (Exception e) {
            throw new DataStoreException("Unable to build envelope from given data", e);
        }

        if (scanner.hasNext("\\w+") && scanner.next().equalsIgnoreCase("font")) {

        }

        if (scanner.hasNext("\\w+") && scanner.next().equalsIgnoreCase("spacing")) {

        }

        if (scanner.hasNext("\\w+") && scanner.next().equalsIgnoreCase("angle")) {

        }
    }

    /**
     * Build a feature type which represents a MIF point geometry
     *
     * @param crs The CRS to put in feature type. If null, no CRS will be pass to the feature type.
     * @return A {@link SimpleFeatureType} which describe a point (as MIF defines it).
     */
    @Override
    public SimpleFeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        GeometryType geomType = new DefaultGeometryType(NAME, Envelope.class, crs, true, false, null, null, null);
        final GeometryDescriptor geomDesc = new DefaultGeometryDescriptor(geomType, NAME, 1, 1, true, null);

        final List<AttributeDescriptor> descList = new ArrayList<AttributeDescriptor>(2);
        descList.add(TEXT_DESCRIPTOR);
        descList.add(FONT_DESCRIPTOR);
        descList.add(SPACING_DESCRIPTOR);
        descList.add(ANGLE_DESCRIPTOR);
        descList.add(LABEL_DESCRIPTOR);

        featureType = new DefaultSimpleFeatureType(NAME, descList, geomDesc, false, null, parent, null);
        return featureType;
    }

    @Override
    public Class getGeometryBinding() {
        return Envelope.class;
    }

    @Override
    public Name getName() {
        return NAME;
    }
}
