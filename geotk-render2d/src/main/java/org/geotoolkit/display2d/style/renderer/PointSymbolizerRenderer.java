/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.util.stream.Stream;
import org.apache.sis.map.ExceptionPresentation;
import org.apache.sis.map.Presentation;
import org.apache.sis.measure.Units;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.presentation.PointPresentation;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.style.CachedPointSymbolizer;
import org.geotoolkit.renderer.DefaultGroupPresentation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.referencing.operation.TransformException;

/**
 * TODO: remove duplicate code
 * TODO: Use Jacobian matrix OF EACH RENDERED POINT to take CRS orientation/deformation into account.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class PointSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedPointSymbolizer> {

    /**
     * Defines the absolute radian value below which rotation is ignored.
     */
    private static final double ROTATION_TOLERANCE = 5e-2;

    public PointSymbolizerRenderer(final SymbolizerRendererService service,final CachedPointSymbolizer symbol, final RenderingContext2D context) {
        super(service,symbol,context);
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Feature feature) {
        final ProjectedFeature pf = new ProjectedFeature(renderingContext, feature);
        final Feature candidate = pf.getCandidate();

        //test if the symbol is visible on this feature
        if (symbol.isVisible(candidate)) {
            final ProjectedGeometry projectedGeometry = pf.getGeometry(geomPropertyName);
            final DefaultGroupPresentation group = new DefaultGroupPresentation(layer, layer.getData(), candidate);
            try {
                if (presentation(group, projectedGeometry, candidate)) {
                    return Stream.of(group);
                }
            } catch (TransformException ex) {
                ExceptionPresentation ep = new ExceptionPresentation(ex);
                ep.setLayer(layer);
                ep.setResource(layer.getData());
                ep.setCandidate(feature);
                return Stream.of(ep);
            }
        }
        return Stream.empty();
    }

    private boolean presentation(final DefaultGroupPresentation group, final ProjectedGeometry projectedGeometry, Object candidate) throws TransformException {

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if (projectedGeometry == null) return false;

        //we adjust coefficient for rendering ------------------------------
        float coeff;
        if (symbolUnit.equals(Units.POINT)) {
            //symbol is in display unit
            coeff = 1;
        } else {
            //we have a special unit we must adjust the coefficient
            coeff = renderingContext.getUnitCoefficient(symbolUnit);
            // calculate scale difference between objective and display
            final AffineTransform inverse = renderingContext.getObjectiveToDisplay();
            coeff *= Math.abs(AffineTransforms2D.getScale(inverse));
        }

        //create the image--------------------------------------------------
        final RenderedImage img = symbol.getImage(candidate,coeff,false, hints);

        if (img == null) {
            //may be correct, image can be too small for rendering
            return false;
        }

        final float[] disps = new float[2];
        final float[] anchor = new float[2];
        symbol.getDisplacement(candidate,disps);
        symbol.getAnchor(candidate,anchor);
        disps[0] *= coeff ;
        disps[1] *= coeff ;

        final Geometry[] geoms = projectedGeometry.getDisplayGeometryJTS();

        if (geoms == null) {
            //no geometry
            return false;
        }

        /**
         * The Rotation element gives the rotation of a graphic in the clockwise direction about its
         * center point in decimal degrees, encoded as a floating-point number. Negative values
         * mean counter-clockwise rotation. The default value is 0.0 (no rotation). Note that there is
         * no connection between source geometry types and rotations; the point used for plotting
         * has no inherent direction. Also, the point within the graphic about which it is rotated is
         * format dependent. If a format does not include an inherent rotation point, then the point
         * of rotation should be the centroid.
         */
        double cwRotation = symbol.getRotation(candidate);
        if (Math.abs(cwRotation) < ROTATION_TOLERANCE) cwRotation = 0.0;
        final AffineTransform rotation = new AffineTransform();
        rotation.rotate(cwRotation);

        final int postx = (int) (-img.getWidth()*anchor[0] + disps[0]);
        final int posty = (int) (-img.getHeight()*anchor[1] - disps[1]);

        boolean dataRendered = false;
        for (Geometry geom : geoms) {
            if (geom instanceof Point || geom instanceof MultiPoint) {
                //TODO use generalisation on multipoints

                final Coordinate[] coords = geom.getCoordinates();
                for (int i=0, n = coords.length; i<n ; i++) {
                    final Coordinate coord = coords[i];

                    final AffineTransform positioning = AffineTransform.getTranslateInstance(coord.x, coord.y);
                    positioning.concatenate(rotation);
                    positioning.translate(postx, posty);
                    final PointPresentation presentation = new PointPresentation(group.getLayer(), group.getResource(), (Feature) group.getCandidate());
                    presentation.forGrid(renderingContext);
                    presentation.composite = GO2Utilities.ALPHA_COMPOSITE_1F;
                    presentation.displayTransform = positioning;
                    presentation.image = img;
                    group.elements().add(presentation);

                    dataRendered = true;
                }

            } else {
                //get most appropriate point
                final Point pt2d = GO2Utilities.getBestPoint(geom);
                if (pt2d == null || pt2d.isEmpty()) {
                    //no geometry
                    return dataRendered;
                }

                Coordinate pcoord = pt2d.getCoordinate();
                if (Double.isNaN(pcoord.x)) {
                    pcoord = geom.getCoordinate();
                }


                final AffineTransform positioning = AffineTransform.getTranslateInstance(pcoord.x, pcoord.y);
                positioning.concatenate(rotation);
                positioning.translate(postx, posty);
                final PointPresentation presentation = new PointPresentation(group.getLayer(), group.getResource(), (Feature) group.getCandidate());
                presentation.forGrid(renderingContext);
                presentation.composite = GO2Utilities.ALPHA_COMPOSITE_1F;
                presentation.displayTransform = positioning;
                presentation.image = img;
                group.elements().add(presentation);

                dataRendered = true;
            }
        }

        return dataRendered;
    }

}
