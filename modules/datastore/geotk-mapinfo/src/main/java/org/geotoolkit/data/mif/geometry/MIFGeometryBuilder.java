package org.geotoolkit.data.mif.geometry;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.geotoolkit.data.mif.style.Pen;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.feature.type.DefaultGeometryDescriptor;
import org.geotoolkit.feature.type.DefaultGeometryType;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.style.Symbolizer;

import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Used for {@link org.geotoolkit.data.mif.MIFUtils.GeometryType}, to allow us pass a method for building geometry as
 * enum attribute.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 25/02/13
 */
public abstract class MIFGeometryBuilder {

    protected final static Logger LOGGER = Logger.getLogger(MIFGeometryBuilder.class.getName());
    protected final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel());

    protected final static Name STYLE_NAME = new DefaultName("STYLE");

    protected static final AttributeDescriptor STYLE;

    static {
        final AttributeType penType = new DefaultAttributeType(STYLE_NAME, Symbolizer.class, false, false, null, null, null);
        STYLE = new DefaultAttributeDescriptor(penType, STYLE_NAME, 0, 1, true, null);
    }
    public SimpleFeatureType featureType;

    /**
     * Parse an input file to build a JTS Geometry with its data.
     *
     *
     * @param scanner the Scanner to use for geometry parsing (should be placed on the beginning of the geometry).
     * @param toFill The feature to put geometry data. It cannot be null, and should have been built with feature type
     *               given by {@link MIFGeometryBuilder#buildType(org.opengis.referencing.crs.CoordinateReferenceSystem, org.opengis.feature.type.FeatureType)}.
     * @param toApply
     */
    public abstract void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException;

    /**
     * Build a feature type which represents a MIF geometry.
     *
     * @param crs The CRS to put in feature type. If null, no CRS will be pass to the feature type.
     * @return A {@link org.opengis.feature.simple.SimpleFeatureType} which describe a geometry (as MIF defines it).
     */
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        final Name name = getName();
        GeometryType geomType = new DefaultGeometryType(name, getGeometryBinding(), crs, true, false, null, null, null);
        final GeometryDescriptor geomDesc = new DefaultGeometryDescriptor(geomType, name, 1, 1, true, null);

        featureType = new DefaultSimpleFeatureType(name, Collections.singletonList(STYLE), geomDesc, false, null, parent, null);
        return featureType;
    }

    /**
     * Return the java class used to store the MIF geometry.
     * @return
     */
    public abstract Class getGeometryBinding();

    /**
     *
     * @return
     */
    public abstract Name getName();
}
