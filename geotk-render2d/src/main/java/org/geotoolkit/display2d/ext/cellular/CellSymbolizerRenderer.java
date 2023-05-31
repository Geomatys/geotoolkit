/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2014, Geomatys
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
package org.geotoolkit.display2d.ext.cellular;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.map.ExceptionPresentation;
import org.apache.sis.internal.map.Presentation;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.math.Statistics;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.RenderingRoutines;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.style.DefaultMutableStyle;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;

/**
 * TODO : For features, compute statistics only if input symbolizer needs
 *  it, and compute them only on required fields.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CellSymbolizerRenderer extends AbstractCoverageSymbolizerRenderer<CachedCellSymbolizer> {

    private static final GeometryFactory GF = JTS.getFactory();

    public CellSymbolizerRenderer(SymbolizerRendererService service,
            CachedCellSymbolizer symbol, RenderingContext2D context) {
        super(service, symbol, context);
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Resource resource) {

        if (symbol.getCachedRule() == null) {
            return Stream.empty();
        }

        if (resource instanceof FeatureSet) {
            return presentations(layer, (FeatureSet) resource);
        } else if (resource instanceof GridCoverageResource) {
            return presentations(layer, (GridCoverageResource) resource);
        } else {
            return Stream.empty();
        }
    }

    private Stream<Presentation> presentations(MapLayer layer, FeatureSet fs) {

        //calculate the cells
        final int cellSize = symbol.getSource().getCellSize();
        final AffineTransform trs = renderingContext.getDisplayToObjective();
        final double objCellSize = AffineTransforms2D.getScale(trs) * cellSize;

        //find min and max cols/rows
        final Envelope env = renderingContext.getCanvasObjectiveBounds2D();
        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        final int minCol = (int) (env.getMinimum(0) / objCellSize);
        final int maxCol = (int) ((env.getMaximum(0) / objCellSize) + 0.5);
        final int minRow = (int) (env.getMinimum(1) / objCellSize);
        final int maxRow = (int) ((env.getMaximum(1) / objCellSize) + 0.5);
        final int nbRow = maxRow - minRow;
        final int nbCol = maxCol - minCol;

        //create all cell contours
        final Polygon[][] contours = new Polygon[nbRow][nbCol];
        for (int r = 0; r < nbRow; r++) {
            for (int c = 0; c < nbCol; c++) {
                final double minx = (minCol+c) * objCellSize;
                final double maxx = minx + objCellSize;
                final double miny = (minRow+r) * objCellSize;
                final double maxy = miny + objCellSize;
                contours[r][c] = GF.createPolygon(new Coordinate[]{
                    new Coordinate(minx, miny),
                    new Coordinate(minx, maxy),
                    new Coordinate(maxx, maxy),
                    new Coordinate(maxx, miny),
                    new Coordinate(minx, miny),
                });
                JTS.setCRS(contours[r][c],crs);
            }
        }

        FeatureType baseType = null;
        FeatureType cellType = null;
        String[] numericProperties = null;
        Statistics[][][] stats = null;

        try (final RenderingRoutines.GraphicIterator graphics = RenderingRoutines.getIterator(fs, renderingContext)) {
            while (graphics.hasNext()) {
                final ProjectedObject obj = graphics.next();
                final ProjectedFeature projFeature = (ProjectedFeature) obj;
                if (baseType == null) {
                    //we expect all features to have the same type
                    baseType = projFeature.getCandidate().getType();
                    cellType = CellSymbolizer.buildCellType(baseType,crs);

                    final List<String> props = new ArrayList<>();
                    for (PropertyType desc : baseType.getProperties(true)) {
                        if (desc instanceof AttributeType) {
                            final AttributeType att = (AttributeType) desc;
                            final Class binding = att.getValueClass();
                            if (Number.class.isAssignableFrom(binding) || String.class.isAssignableFrom(binding)) {
                                props.add(att.getName().toString());
                            }
                        }
                    }
                    numericProperties = props.toArray(new String[props.size()]);
                    stats = new Statistics[numericProperties.length][nbRow][nbCol];
                    for (int i = 0; i < numericProperties.length; i++) {
                        for (int j = 0; j < nbRow; j++) {
                            for (int k = 0; k < nbCol; k++) {
                                stats[i][j][k] = new Statistics("");
                            }
                        }
                    }
                }
                final ProjectedGeometry pg = projFeature.getGeometry(geomPropertyName);
                final Geometry[] geoms = pg.getObjectiveGeometryJTS();

                //find in which cell it intersects
                int row=-1;
                int col=-1;
                loop:
                for (Geometry g : geoms) {
                    if (g == null) continue;
                    for (int r = 0; r < nbRow; r++) {
                        for (int c = 0; c < nbCol; c++) {
                            if (contours[r][c].intersects(g)) {
                                row = r;
                                col = c;
                                break loop;
                            }
                        }
                    }
                }

                //fill stats
                if (row != -1) {
                    final Feature feature = projFeature.getCandidate();
                    for (int i = 0; i < numericProperties.length; i++) {
                        final Object value = feature.getProperty(numericProperties[i]).getValue();
                        try {
                            final Number num = ObjectConverters.convert(value, Number.class);
                            if (num != null) {
                                stats[i][row][col].accept(num.doubleValue());
                            }
                        } catch (UnconvertibleObjectException e) {
                            Logging.recoverableException(LOGGER, CellSymbolizerRenderer.class, "portray", e);
                            // TODO - do we really want to ignore?
                        }
                    }
                }
            }
        } catch (DataStoreException | IOException | TransformException ex) {
            ExceptionPresentation ep = new ExceptionPresentation(ex);
            ep.setLayer(layer);
            ep.setResource(fs);
            return Stream.of(ep);
        }

        if (numericProperties == null) {
            //nothing in the iterator
            return Stream.empty();
        }

        //render the cell features
        final Object[] values = new Object[2 + 7 * numericProperties.length];


        final List<Feature> features = new ArrayList<>();
        for (int r = 0; r < nbRow; r++) {
            for (int c = 0; c < nbCol; c++) {

                final Feature feature = cellType.newInstance();
                feature.setPropertyValue(AttributeConvention.IDENTIFIER, "cell-n");

                values[0] = contours[r][c].getCentroid();
                JTS.setCRS( ((Geometry)values[0]), crs);
                values[1] = contours[r][c];
                int k=1;
                for (int b = 0, n = numericProperties.length; b < n; b++) {
                    values[++k] = stats[b][r][c].count();
                    values[++k] = stats[b][r][c].minimum();
                    values[++k] = stats[b][r][c].mean();
                    values[++k] = stats[b][r][c].maximum();
                    values[++k] = stats[b][r][c].span();
                    values[++k] = stats[b][r][c].rms();
                    values[++k] = stats[b][r][c].sum();
                }

                features.add(feature);
            }
        }

        final MapLayer subLayer = createCellLayer(layer, cellType, features);
        return DefaultPortrayalService.present(subLayer, subLayer.getData(), renderingContext);
    }

    private Stream<Presentation> presentations(MapLayer layer, final GridCoverageResource resource) {

        //adjust envelope, we need cells to start at crs 0,0 to avoid artifacts
        //when building tiles
        final int cellSize = symbol.getSource().getCellSize();
        final AffineTransform2D displayToObjective = renderingContext.getDisplayToObjective();
        double objCellSize = AffineTransforms2D.getScale(displayToObjective) * cellSize;
        final GeneralEnvelope env = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
        final int hidx = CRSUtilities.firstHorizontalAxis(env.getCoordinateReferenceSystem());
        //round under and above to match cell size
        env.setRange(hidx, objCellSize * Math.floor(env.getMinimum(hidx)/objCellSize), objCellSize * Math.ceil(env.getMaximum(hidx)/objCellSize));
        env.setRange(hidx+1, objCellSize * Math.floor(env.getMinimum(hidx+1)/objCellSize), objCellSize * Math.ceil(env.getMaximum(hidx+1)/objCellSize));


        GridCoverage coverage;
        try {
            coverage = resource.read(renderingContext.getGridGeometry());
        } catch (NoSuchDataException ex) {
            //no data on requested area
            return Stream.empty();
        } catch (Exception ex) {
            ExceptionPresentation ep = new ExceptionPresentation(ex);
            ep.setLayer(layer);
            ep.setResource(resource);
            return Stream.of(ep);
        }
        if (coverage != null) {
            coverage = coverage.forConvertedValues(true);
        }
        if (coverage == null) {
            LOGGER.log(Level.WARNING, "Reprojected coverage is null.");
            return Stream.empty();
        }
        try {
            coverage = new GridCoverageProcessor().resample(coverage, new GridGeometry(null, env, GridOrientation.HOMOTHETY));
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, "Reprojected coverage is null.");
            return Stream.empty();
        }

        //create all cell features
        final GeneralEnvelope area = new GeneralEnvelope(coverage.getGridGeometry().getEnvelope());
        //round under and above to match cell size
        area.setRange(hidx, objCellSize * Math.floor(area.getMinimum(hidx)/objCellSize), objCellSize * Math.ceil(area.getMaximum(hidx)/objCellSize));
        area.setRange(hidx+1, objCellSize * Math.floor(area.getMinimum(hidx+1)/objCellSize), objCellSize * Math.ceil(area.getMaximum(hidx+1)/objCellSize));
        final int nbx = (int) Math.ceil(area.getSpan(0) / objCellSize);
        final int nby = (int) Math.ceil(area.getSpan(1) / objCellSize);

        final RenderedImage image = coverage.render(null);
        final int nbBand = image.getSampleModel().getNumBands();
        final Statistics[][][] stats = new Statistics[nbBand][nby][nbx];
        MathTransform gridToCRS = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER);


        final PixelIterator ite = PixelIterator.create(image);
        int i,x,y;
        final double[] gridCoord = new double[gridToCRS.getSourceDimensions()];
        final double[] crsCoord = new double[gridToCRS.getTargetDimensions()];
        final double[] pixel = new double[nbBand];
        try {
            while (ite.next()) {
                Point position = ite.getPosition();
                gridCoord[0] = position.getX();
                gridCoord[1] = position.getY();
                gridToCRS.transform(gridCoord, 0, crsCoord, 0, 1);
                crsCoord[0] = (crsCoord[0]-area.getMinimum(0))/objCellSize;
                crsCoord[1] = (crsCoord[1]-area.getMinimum(1))/objCellSize;
                x = (int) crsCoord[0];
                y = (int) crsCoord[1];
                ite.getPixel(pixel);
                for (i = 0; i < nbBand; i++) {
                    if (stats[i][y][x] == null) stats[i][y][x] = new Statistics("");
                    if (!Double.isNaN(pixel[i])) {
                        stats[i][y][x].accept(pixel[i]);
                    }
                }
            }
        } catch (TransformException ex) {
            ExceptionPresentation ep = new ExceptionPresentation(ex);
            ep.setLayer(layer);
            ep.setResource(resource);
            return Stream.of(ep);
        }

        //prepare the cell feature type
        final FeatureType cellType = CellSymbolizer.buildCellType(coverage);

        final Rule rule = symbol.getSource().getRule();

        final List<Feature> features = new ArrayList<>();
        for (y = 0; y < nby; y++) {
            for (x = 0; x < nbx; x++) {
                if (stats[0][y][x] == null) {
                    for (i = 0; i < nbBand; i++) {
                        stats[i][y][x] = new Statistics("");
                    }
                }
                double cx = area.getMinimum(0) + (0.5+x)*objCellSize;
                double cy = area.getMinimum(1) + (0.5+y)*objCellSize;

                final Feature feature = cellType.newInstance();
                feature.setPropertyValue(CellSymbolizer.PROPERY_GEOM_CENTER, GF.createPoint(new Coordinate(cx,cy)));
                int k=0;
                for (int b = 0, n = nbBand; b < n; b++) {
                    feature.setPropertyValue("band_"+b+CellSymbolizer.PROPERY_SUFFIX_COUNT,(double)stats[b][y][x].count());
                    feature.setPropertyValue("band_"+b+CellSymbolizer.PROPERY_SUFFIX_MIN,stats[b][y][x].minimum());
                    feature.setPropertyValue("band_"+b+CellSymbolizer.PROPERY_SUFFIX_MEAN,stats[b][y][x].mean());
                    feature.setPropertyValue("band_"+b+CellSymbolizer.PROPERY_SUFFIX_MAX,stats[b][y][x].maximum());
                    feature.setPropertyValue("band_"+b+CellSymbolizer.PROPERY_SUFFIX_RANGE,stats[b][y][x].span());
                    feature.setPropertyValue("band_"+b+CellSymbolizer.PROPERY_SUFFIX_RMS,stats[b][y][x].rms());
                    feature.setPropertyValue("band_"+b+CellSymbolizer.PROPERY_SUFFIX_SUM,stats[b][y][x].sum());
                }

                features.add(feature);
            }
        }

        final MapLayer subLayer = createCellLayer(layer, cellType, features);
        return DefaultPortrayalService.present(subLayer, subLayer.getData(), renderingContext);
    }

    private MapLayer createCellLayer(final MapLayer sourceLayer, final FeatureType cellType, final List<Feature> cells) {
        final InMemoryFeatureSet subfs = new InMemoryFeatureSet(cellType, cells);
        final MapLayer subLayer = MapBuilder.createLayer(subfs);
        subLayer.setIdentifier(sourceLayer.getIdentifier());
        subLayer.setTitle(sourceLayer.getTitle());

        final Rule rule = symbol.getSource().getRule();

        MutableStyleFactory sf = GO2Utilities.STYLE_FACTORY;
        MutableFeatureTypeStyle fts = sf.featureTypeStyle(rule.symbolizers().toArray(Symbolizer[]::new));
        fts.rules().get(0).setFilter(rule.getFilter());
        final MutableStyle style = new DefaultMutableStyle();
        style.featureTypeStyles().add(fts);
        subLayer.setStyle(style);

        return subLayer;
    }
}
