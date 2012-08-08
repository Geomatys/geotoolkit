/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.process.coverage.isoline;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point3d;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.process.coverage.isoline.IsolineDescriptor.*;
import org.geotoolkit.process.coverage.kriging.IsolineCreator;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class Isoline extends AbstractProcess {

    public Isoline(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    @Override
    protected void execute() throws ProcessException {
        final GridCoverage2D coverage = value(COVERAGE, inputParameters);
        final double[] intervals = value(INTERVALS, inputParameters);

        final CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();
        final RenderedImage image = coverage.getRenderedImage();
        final MathTransform2D trs = coverage.getGridGeometry().getGridToCRS2D();

        final IsolineCreator creator = new IsolineCreator(image, intervals);
        final Map<Point3d, List<Coordinate>> steps = creator.createIsolines();

        final GeometryFactory GF = new GeometryFactory();
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("isoline");
        ftb.add("geometry", LineString.class, crs);
        ftb.add("value", Double.class);
        ftb.setDefaultGeometry("geometry");
        final FeatureType type = ftb.buildFeatureType();

        final FeatureCollection col = DataUtilities.collection("id", type);
        int inc = 0;

        for (final Point3d p : steps.keySet()) {
            final List<Coordinate> cshps = steps.get(p);

            if (cshps.get(0).x > cshps.get(cshps.size()-1).x) {
                //the coordinates are going left, reverse order
                Collections.reverse(cshps);
            }

            LineString geometry = GF.createLineString(cshps.toArray(new Coordinate[cshps.size()]));
            try {
                geometry = (LineString) JTS.transform(geometry, trs);
            } catch (MismatchedDimensionException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            } catch (TransformException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
            final double value = p.z;

            final Feature f = FeatureUtilities.defaultFeature(type, String.valueOf(inc++));
            f.getProperty("geometry").setValue(geometry);
            f.getProperty("value").setValue(value);
            col.add(f);
        }

        getOrCreate(FCOLL, outputParameters).setValue(col);
    }

}
