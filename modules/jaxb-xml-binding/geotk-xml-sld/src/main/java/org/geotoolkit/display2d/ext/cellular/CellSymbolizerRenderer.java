/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.util.Map;
import java.util.logging.Level;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.processing.CoverageProcessingException;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.DefaultProjectedFeature;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.extractQuery;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.fixEnvelopeWithQuery;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.CoverageMapLayer;
import org.apache.sis.math.Statistics;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CellSymbolizerRenderer extends AbstractCoverageSymbolizerRenderer<CachedCellSymbolizer>{

    private static final GeometryFactory GF = new GeometryFactory();

    public CellSymbolizerRenderer(SymbolizerRendererService service,
            CachedCellSymbolizer symbol, RenderingContext2D context) {
        super(service, symbol, context);
    }

    @Override
    public void portray(final ProjectedCoverage projectedCoverage) throws PortrayalException {

        final GridCoverage2D coverage = toObjective(projectedCoverage);
        if(coverage == null){
            LOGGER.log(Level.WARNING, "Reprojected coverage is null.");
            return;
        }

        final MathTransform2D gridToCRS = coverage.getGridGeometry().getGridToCRS2D();

        Envelope env = renderingContext.getCanvasObjectiveBounds();
        final AffineTransform tr;
        try {
            // TODO: handle the case where gridToCRS is not affine.
            tr = ((AffineTransform) gridToCRS).createInverse();
            env = CRS.transform(new AffineTransform2D(tr), env);

            final MathTransform trans = renderingContext.getMathTransform(renderingContext.getDisplayCRS(), coverage.getCoordinateReferenceSystem2D());
            if(trans instanceof AffineTransform){
                tr.concatenate((AffineTransform)trans);
            }else{
                //TODO try to find a better way to calculate the step
                //currently make a fake affinetransform using the difference between envelopes.
                final AffineTransform dispToObjective = renderingContext.getDisplayToObjective();
                final AffineTransform objToCoverage   = calculateAverageAffine(renderingContext, coverage);
                tr.concatenate(dispToObjective);
                tr.concatenate(objToCoverage);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
            return;
        }


        //calculate decimation factor
        final int cellSize = symbol.getSource().getCellSize();
        Point2D delta = new Point2D.Double(cellSize, 0);
        delta = tr.deltaTransform(delta, delta);
        final int decimateX = length(delta);
        delta.setLocation(0, cellSize);
        delta = tr.deltaTransform(delta, delta);
        final int decimateY = length(delta);

        final Rectangle2D shp = new Rectangle2D.Double(env.getMinimum(0)/decimateX, env.getMinimum(1)/decimateY, env.getSpan(0)/decimateX, env.getSpan(1)/decimateY);
        final RenderedImage image = coverage.getRenderedImage();
        final int nbBand = image.getSampleModel().getNumBands();

        //prepare the cell feature type
        final SimpleFeatureType cellType = CellSymbolizer.buildCellType(coverage);
        final Object[] values = new Object[1+7*nbBand];
        final SimpleFeature feature = new DefaultSimpleFeature(cellType, new DefaultFeatureId("cell-n"), values, false);
        final StatelessContextParams params = new StatelessContextParams(renderingContext.getCanvas(), null);
        params.update(renderingContext);
        final DefaultProjectedFeature pf = new DefaultProjectedFeature(params,feature);

        //iterator on image
        final CellIterator ite = new CellIterator(image,decimateX,decimateY);
        final CachedRule rule = symbol.getCachedRule();
        final CanvasMonitor monitor = renderingContext.getMonitor();

        while(ite.next()){
            if(monitor.stopRequested()) break;
            if(!ite.visible(shp)) continue;
            final Point2D point = ite.position();
            pf.clearDataCache();
            try {
                final Point2D obj = gridToCRS.transform(point, null);
                final Statistics[] stats = ite.statistics();

                values[0] = GF.createPoint(new Coordinate(obj.getX(), obj.getY()));
                int i=0;
                for(int b=0,n=nbBand;b<n;b++){
                    values[++i] = stats[b].count();
                    values[++i] = stats[b].minimum();
                    values[++i] = stats[b].mean();
                    values[++i] = stats[b].maximum();
                    values[++i] = stats[b].span();
                    values[++i] = stats[b].rms();
                    values[++i] = stats[b].sum();
                }

                if(rule.getFilter() == null || rule.getFilter().evaluate(feature)){
                    for(CachedSymbolizer cs : rule.symbolizers()){
                        cs.getRenderer().createRenderer(cs, renderingContext).portray(pf);
                    }
                }

            } catch (TransformException ex) {
                LOGGER.log(Level.INFO, ex.getMessage(),ex);
            }
        }

    }

    private GridCoverage2D toObjective(final ProjectedCoverage projectedCoverage) throws PortrayalException{
        double[] resolution = renderingContext.getResolution();
        Envelope bounds = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
        resolution = checkResolution(resolution,bounds);
        final CoverageMapLayer coverageLayer = projectedCoverage.getLayer();
        final CoordinateReferenceSystem coverageMapLayerCRS = coverageLayer.getBounds().getCoordinateReferenceSystem();

        final Map<String,Double> queryValues = extractQuery(projectedCoverage.getLayer());
        if (queryValues != null && !queryValues.isEmpty()) {
            bounds = fixEnvelopeWithQuery(queryValues, bounds, coverageMapLayerCRS);
            resolution = DefaultRasterSymbolizerRenderer.fixResolutionWithCRS(resolution, coverageMapLayerCRS);
        }

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(bounds);
        param.setResolution(resolution);

        GridCoverage2D dataCoverage;
        try {
            dataCoverage = projectedCoverage.getCoverage(param);
        } catch (DisjointCoverageDomainException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
            return null;
        } catch (CoverageStoreException ex) {
            throw new PortrayalException(ex);
        }

        if(dataCoverage == null){
            LOGGER.log(Level.WARNING, "Requested an area where no coverage where found.");
            return null;
        }

        final CoordinateReferenceSystem coverageCRS = dataCoverage.getCoordinateReferenceSystem();
        try{
            final CoordinateReferenceSystem targetCRS = renderingContext.getObjectiveCRS2D();
            final CoordinateReferenceSystem candidate2D = CRSUtilities.getCRS2D(coverageCRS);
            if(!CRS.equalsIgnoreMetadata(candidate2D,targetCRS) ){

                //calculate best intersection area
                final Envelope2D covEnv = dataCoverage.getEnvelope2D();
                final GeneralEnvelope tmp = new GeneralEnvelope(renderingContext.getPaintingObjectiveBounds2D());
                tmp.intersect(CRS.transform(covEnv, targetCRS));

                if(tmp.isEmpty()){
                    dataCoverage = null;
                }else{
                    //calculate gridgeometry
                    final AffineTransform2D trs = renderingContext.getObjectiveToDisplay();
                    final GeneralEnvelope dispEnv = CRS.transform(trs, tmp);
                    final int width = (int)Math.round(dispEnv.getSpan(0));
                    final int height = (int)Math.round(dispEnv.getSpan(1));

                    if(width<=0 || height<=0){
                        dataCoverage = null;
                    }else{
                        final GeneralGridEnvelope ge = new GeneralGridEnvelope(
                                new int[]{0,0},
                                new int[]{width,height},
                                false);
                        final AffineTransform gridToCrs = new AffineTransform(renderingContext.getDisplayToObjective());
                        gridToCrs.translate((int)dispEnv.getMinimum(0), (int)dispEnv.getMinimum(1));
                        final GridGeometry2D gridgeom = new GridGeometry2D(
                                ge, PixelOrientation.UPPER_LEFT,
                                new AffineTransform2D(gridToCrs), targetCRS, null);
                        //TODO we should provide the gridgeometry, but there is a 1/2 pixel displacement
                        //dataCoverage = (GridCoverage2D) Operations.DEFAULT.resample(dataCoverage, targetCRS, gridgeom, null);
                        dataCoverage = (GridCoverage2D) Operations.DEFAULT.resample(dataCoverage, targetCRS, null, null);
                    }
                }
            }
        } catch (CoverageProcessingException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return null;
        } catch(Exception ex){
            //several kind of errors can happen here, we catch anything to avoid blocking the map component.
            monitor.exceptionOccured(
                new IllegalStateException("Coverage is not in the requested CRS, found : \n" +
                coverageCRS +
                "\n Was expecting : \n" +
                renderingContext.getObjectiveCRS() +
                "\nOriginal Cause:"+ ex.getMessage(), ex), Level.WARNING);
            return null;
        }

        return dataCoverage;
    }

    private AffineTransform calculateAverageAffine(final RenderingContext2D context,
            final GridCoverage2D coverage) throws FactoryException, TransformException{

        final MathTransform trs = context.getMathTransform(context.getObjectiveCRS(),coverage.getCoordinateReferenceSystem2D() );

        final Envelope refEnv = context.getCanvasObjectiveBounds();
        final GeneralEnvelope coverageEnv = CRS.transform(trs, refEnv);

        final double objX = refEnv.getSpan(0);
        final double objY = refEnv.getSpan(1);
        final double covX = coverageEnv.getMaximum(0)-coverageEnv.getMinimum(0);
        final double covY = coverageEnv.getMaximum(1)-coverageEnv.getMinimum(1);
        final double scaleX = covX/objX;
        final double scaleY = covY/objY;

        AffineTransform aff = new AffineTransform();
        aff.setToScale(scaleX,scaleY);
        return aff;
    }

    /**
     * Evaluate the lenght of the given vector.
     *
     * @param delta : vector to evaluate
     * @return integer : lenght of the vector rounded at the above integer
     */
    private static int length(final Point2D delta) {
        return Math.max(1, (int) Math.ceil(Math.hypot(delta.getX(), delta.getY())));
    }

}
