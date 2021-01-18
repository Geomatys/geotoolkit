/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.display2d.ext.pie;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.RenderingRoutines;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.renderer.ExceptionPresentation;
import org.geotoolkit.renderer.Presentation;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;

/**
 * Pie symbolizer renderer.
 *
 * @author Johann Sorel (Geomays)
 * @author Cédric Briançon (Geomatys)
 */
public class PieSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedPieSymbolizer> {
    private static class PropsPie {
        private Geometry[] geometries;
        private Map<Object,Double> vals = new HashMap<>();
        private double size = 100;
    }

    public PieSymbolizerRenderer(final SymbolizerRendererService service, CachedPieSymbolizer cache, RenderingContext2D context){
        super(service, cache, context);
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Resource resource) {

        if (!(resource instanceof FeatureSet)) {
            return Stream.empty();
        }
        final FeatureSet fs = (FeatureSet) resource;

        final Expression size = symbol.getSource().getSize();
        final Expression group = symbol.getSource().getGroup();
        final Expression quarter = symbol.getSource().getQuarter();
        final Expression value = symbol.getSource().getValue();
        final List<PieSymbolizer.ColorQuarter> colorQuarters = symbol.getSource().getColorQuarters();

        if (group == null || quarter == null || value == null) {
            return Stream.empty();
        }

        try (final RenderingRoutines.GraphicIterator ite = RenderingRoutines.getIterator(fs, renderingContext)) {

            final Map<Object,PropsPie> vals = new HashMap<>();
            while(ite.hasNext()) {
                try {
                    final Object next = ite.next();
                    final Feature f;
                    if (next instanceof ProjectedFeature) {
                        f = ((ProjectedFeature) next).getCandidate();
                    } else {
                        continue;
                    }

                    final Object key = group.evaluate(f);
                    final Object quarterKey = quarter.evaluate(f);
                    final Double valueKey = value.evaluate(f, Double.class);
                    PropsPie propsPie = vals.get(key);
                    if (propsPie == null) {
                        propsPie = new PropsPie();
                        vals.put(key, propsPie);
                    }
                    propsPie.geometries = ((ProjectedFeature) next).getGeometry(null).getDisplayGeometryJTS();
                    if (size != null) {
                        final Double s = size.evaluate(f, Double.class);
                        if (s != null && !Double.isNaN(s) && s > 0) {
                            propsPie.size = s;
                        }
                    }

                    Double oldQuarter = propsPie.vals.get(quarterKey);
                    if (oldQuarter == null) {
                        oldQuarter = valueKey;
                    } else {
                        oldQuarter += valueKey;
                    }
                    propsPie.vals.put(quarterKey, oldQuarter);

                } catch (Exception ex) {
                    return Stream.of(new ExceptionPresentation(layer, resource, null, ex));
                }
            }

            renderingContext.switchToDisplayCRS();
            final Graphics2D g = renderingContext.getGraphics();

            for (final PropsPie propsPie : vals.values()) {
                final double pieSize = propsPie.size;
                double nbTotalValue = 0;
                for (final Double val : propsPie.vals.values()) {
                    if (val != null && !Double.isNaN(val)) {
                        nbTotalValue += val;
                    }
                }

                if (nbTotalValue != 0) {
                    double startDegree = 0;
                    double countOthers = 0;

                    for (final Map.Entry<Object,Double> entryPropsVal : propsPie.vals.entrySet()) {
                        if (entryPropsVal.getValue() == null || Double.isNaN(entryPropsVal.getValue())) {
                            continue;
                        }
                        double degrees = entryPropsVal.getValue() * 360 / nbTotalValue;

                        for (final Geometry geom : propsPie.geometries) {
                            // Try to find the matching color for this quarter of pie
                            Color c = null;
                            for (final PieSymbolizer.ColorQuarter candidate : colorQuarters) {
                                if (entryPropsVal.getKey().equals(candidate.getQuarter().evaluate(null))) {
                                    c = candidate.getColor().evaluate(null, Color.class);
                                    break;
                                }
                            }
                            if (c == null) {
                                // Not specified, so go to others group
                                countOthers += entryPropsVal.getValue();
                                break;
                            }

                            final Point center = geom.getCentroid();
                            final Arc2D arc = new Arc2D.Double(center.getX() - pieSize / 2, center.getY() - pieSize / 2, pieSize, pieSize,
                                    startDegree, degrees, Arc2D.PIE);

                            g.setPaint(c);
                            g.fill(arc);

                            g.setStroke(new BasicStroke(1));
                            g.setPaint(Color.BLACK);
                            g.draw(arc);

                            startDegree += degrees;
                        }
                    }

                    if (countOthers > 0) {
                        for (final Geometry geom : propsPie.geometries) {
                            double degrees = countOthers * 360 / nbTotalValue;
                            final Point center = geom.getCentroid();
                            final Arc2D arc = new Arc2D.Double(center.getX() - pieSize / 2, center.getY() - pieSize / 2, pieSize, pieSize,
                                    startDegree, degrees, Arc2D.PIE);
                            g.setPaint(Color.GRAY);
                            g.fill(arc);

                            g.setStroke(new BasicStroke(1));
                            g.setPaint(Color.BLACK);
                            g.draw(arc);
                        }
                    }
                }
            }
            return Stream.empty();

        } catch (DataStoreException | IOException ex) {
            return Stream.of(new ExceptionPresentation(layer, resource, null, ex));
        }
    }

}
