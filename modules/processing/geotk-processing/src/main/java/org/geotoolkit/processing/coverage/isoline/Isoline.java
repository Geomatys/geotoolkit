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
package org.geotoolkit.processing.coverage.isoline;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.vecmath.Point3d;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.kriging.IsolineCreator;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.processing.coverage.isoline.IsolineDescriptor.*;


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
        ftb.addAttribute(LineString.class).setName("geometry").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Double.class).setName("value");
        final FeatureType type = ftb.build();

        final FeatureCollection col = FeatureStoreUtilities.collection("id", type);
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
            } catch (MismatchedDimensionException | TransformException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
            final double value = p.z;

            final Feature f = type.newInstance();
            f.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), String.valueOf(inc++));
            f.setPropertyValue("geometry", geometry);
            f.setPropertyValue("value", value);
            col.add(f);
        }
        getOrCreate(FCOLL, outputParameters).setValue(col);
    }
}
