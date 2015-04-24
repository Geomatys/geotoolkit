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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.math.Statistics;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.DefaultCachedRule;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.util.ObjectConverters;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.feature.AttributeConvention;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;

/**
 * TODO : For features, compute statistics only if input symbolizer needs
 *  it, and compute them only on required fields.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CellSymbolizerRenderer extends AbstractCoverageSymbolizerRenderer<CachedCellSymbolizer>{

    private static final GeometryFactory GF = new GeometryFactory();

    public CellSymbolizerRenderer(SymbolizerRendererService service,
            CachedCellSymbolizer symbol, RenderingContext2D context) {
        super(service, symbol, context);
    }

    /**
     * This symbolizer works with groups when it's features.
     *
     * @param graphics
     * @throws PortrayalException
     */
    @Override
    public void portray(Iterator<? extends ProjectedObject> graphics) throws PortrayalException {
        if(symbol.getCachedRule() == null){
            return;
        }
        //calculate the cells
        final int cellSize = symbol.getSource().getCellSize();
        final AffineTransform trs = renderingContext.getDisplayToObjective();
        final double objCellSize = XAffineTransform.getScale(trs) * cellSize;

        //find min and max cols/rows
        final Envelope env = renderingContext.getCanvasObjectiveBounds2D();
        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        final int minCol = (int)(env.getMinimum(0) / objCellSize);
        final int maxCol = (int)((env.getMaximum(0) / objCellSize)+0.5);
        final int minRow = (int)(env.getMinimum(1) / objCellSize);
        final int maxRow = (int)((env.getMaximum(1) / objCellSize)+0.5);
        final int nbRow = maxRow - minRow;
        final int nbCol = maxCol - minCol;

        //create all cell contours
        final Polygon[][] contours = new Polygon[nbRow][nbCol];
        for(int r=0; r<nbRow; r++){
            for(int c=0; c<nbCol; c++){
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

        try{
            while(graphics.hasNext()){
                final ProjectedObject obj = graphics.next();
                final ProjectedFeature projFeature = (ProjectedFeature) obj;
                if(baseType==null){
                    //we expect all features to have the same type
                    baseType = projFeature.getCandidate().getType();
                    cellType = CellSymbolizer.buildCellType(baseType,crs);

                    final List<String> props = new ArrayList<>();
                    for(PropertyType desc : baseType.getProperties(true)){
                        if(desc instanceof AttributeType){
                            final AttributeType att = (AttributeType) desc;
                            final Class binding = att.getValueClass();
                            if(Number.class.isAssignableFrom(binding) || String.class.isAssignableFrom(binding)){
                                props.add(att.getName().toString());
                            }
                        }
                    }
                    numericProperties = props.toArray(new String[props.size()]);
                    stats = new Statistics[numericProperties.length][nbRow][nbCol];
                    for(int i=0;i<numericProperties.length;i++){
                        for(int j=0;j<nbRow;j++){
                            for(int k=0;k<nbCol;k++){
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
                for(Geometry g : geoms){
                    if(g==null) continue;
                    for(int r=0;r<nbRow;r++){
                        for(int c=0;c<nbCol;c++){
                            if (contours[r][c].intersects(g)){
                                row = r;
                                col = c;
                                break loop;
                            }
                        }
                    }
                }

                //fill stats
                if(row!=-1){
                    final Feature feature = projFeature.getCandidate();
                    for(int i=0;i<numericProperties.length;i++){
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
        }catch(TransformException ex){
            throw new PortrayalException(ex);
        }

        if(numericProperties==null){
            //nothing in the iterator
            return;
        }

        //render the cell features
        final Object[] values = new Object[2+7*numericProperties.length];
        final Feature feature = cellType.newInstance();
        feature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "cell-n");
        final StatelessContextParams params = new StatelessContextParams(renderingContext.getCanvas(), null);
        params.update(renderingContext);
        final ProjectedFeature pf = new ProjectedFeature(params,feature);

        final DefaultCachedRule renderers = new DefaultCachedRule(new CachedRule[]{symbol.getCachedRule()},renderingContext);

        //expand the search area by the maximum symbol size
        float symbolsMargin = renderers.getMargin(null, renderingContext);
        if(symbolsMargin==0) symbolsMargin = 300f;
        if(symbolsMargin>0 && params.objectiveJTSEnvelope!=null){
            params.objectiveJTSEnvelope = new com.vividsolutions.jts.geom.Envelope(params.objectiveJTSEnvelope);
            params.objectiveJTSEnvelope.expandBy(symbolsMargin);
        }

        for(int r=0;r<nbRow;r++){
            for(int c=0;c<nbCol;c++){
                pf.setCandidate(feature);

                values[0] = contours[r][c].getCentroid();
                JTS.setCRS( ((Geometry)values[0]), crs);
                values[1] = contours[r][c];
                int k=1;
                for(int b=0,n=numericProperties.length;b<n;b++){
                    values[++k] = stats[b][r][c].count();
                    values[++k] = stats[b][r][c].minimum();
                    values[++k] = stats[b][r][c].mean();
                    values[++k] = stats[b][r][c].maximum();
                    values[++k] = stats[b][r][c].span();
                    values[++k] = stats[b][r][c].rms();
                    values[++k] = stats[b][r][c].sum();
                }

                renderCellFeature(feature, pf, renderers);
                pf.setCandidate(null);
            }
        }

    }

    @Override
    public void portray(final ProjectedCoverage projectedCoverage) throws PortrayalException {

        if(symbol.getCachedRule() == null){
            return;
        }

        //adjust envelope, we need cells to start at crs 0,0 to avoid artifacts
        //when building tiles
        final int cellSize = symbol.getSource().getCellSize();
        final AffineTransform2D displayToObjective = renderingContext.getDisplayToObjective();
        double objCellSize = XAffineTransform.getScale(displayToObjective) * cellSize;
        final GeneralEnvelope env = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
        final int hidx = CRSUtilities.firstHorizontalAxis(env.getCoordinateReferenceSystem());
        //round under and above to match cell size
        env.setRange(hidx, objCellSize * Math.floor(env.getMinimum(hidx)/objCellSize), objCellSize * Math.ceil(env.getMaximum(hidx)/objCellSize));
        env.setRange(hidx+1, objCellSize * Math.floor(env.getMinimum(hidx+1)/objCellSize), objCellSize * Math.ceil(env.getMaximum(hidx+1)/objCellSize));


        GridCoverage2D coverage;
        try {
            coverage = getObjectiveCoverage(projectedCoverage,env,renderingContext.getResolution(),
                    renderingContext.getObjectiveToDisplay(),false);
        } catch (Exception ex) {
            throw new PortrayalException(ex);
        }
        if(coverage!=null){
            coverage = coverage.view(ViewType.GEOPHYSICS);
        }
        if(coverage == null){
            LOGGER.log(Level.WARNING, "Reprojected coverage is null.");
            return;
        }


        //create all cell features
        final GeneralEnvelope area = new GeneralEnvelope(coverage.getEnvelope2D());
        //round under and above to match cell size
        area.setRange(hidx, objCellSize * Math.floor(area.getMinimum(hidx)/objCellSize), objCellSize * Math.ceil(area.getMaximum(hidx)/objCellSize));
        area.setRange(hidx+1, objCellSize * Math.floor(area.getMinimum(hidx+1)/objCellSize), objCellSize * Math.ceil(area.getMaximum(hidx+1)/objCellSize));
        final int nbx = (int) Math.ceil(area.getSpan(0) / objCellSize);
        final int nby = (int) Math.ceil(area.getSpan(1) / objCellSize);

        final RenderedImage image = coverage.getRenderedImage();
        final int nbBand = image.getSampleModel().getNumBands();
        final Statistics[][][] stats = new Statistics[nbBand][nby][nbx];
        MathTransform2D gridToCRS = coverage.getGridGeometry().getGridToCRS2D();


        final PixelIterator ite = PixelIteratorFactory.createDefaultIterator(image);
        int i,x,y;
        final double[] gridCoord = new double[gridToCRS.getSourceDimensions()];
        final double[] crsCoord = new double[gridToCRS.getTargetDimensions()];
        try{
            while(ite.next()){
                gridCoord[0] = ite.getX();
                gridCoord[1] = ite.getY();
                gridToCRS.transform(gridCoord, 0, crsCoord, 0, 1);
                crsCoord[0] = (crsCoord[0]-area.getMinimum(0))/objCellSize;
                crsCoord[1] = (crsCoord[1]-area.getMinimum(1))/objCellSize;
                x = (int) crsCoord[0];
                y = (int) crsCoord[1];
                for(i=0;i<nbBand;i++){
                    if(stats[i][y][x]==null) stats[i][y][x] = new Statistics("");
                    stats[i][y][x].accept(ite.getSampleDouble());
                    if(i<nbBand-1) ite.next();
                }
            }
        }catch(TransformException ex){
            throw new PortrayalException(ex);
        }

        //prepare the cell feature type
        final FeatureType cellType = CellSymbolizer.buildCellType(coverage);
        final Object[] values = new Object[1+7*nbBand];
        final Feature feature = cellType.newInstance();
        final StatelessContextParams params = new StatelessContextParams(renderingContext.getCanvas(), null);
        params.update(renderingContext);
        params.objectiveJTSEnvelope = new com.vividsolutions.jts.geom.Envelope(
                env.getMinimum(0), env.getMaximum(0),
                env.getMinimum(1), env.getMaximum(1));
        params.displayClipRect = null;
        params.displayClip = null;

        final ProjectedFeature pf = new ProjectedFeature(params,feature);
        final DefaultCachedRule renderers = new DefaultCachedRule(new CachedRule[]{symbol.getCachedRule()},renderingContext);

        //force image interpolation here
        Object oldValue = g2d.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        if(oldValue == null) oldValue = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        renderingContext.getRenderingHints().put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);


        for(y=0;y<nby;y++){
            for(x=0;x<nbx;x++){
                if(stats[0][y][x]==null){
                    for(i=0;i<nbBand;i++)
                    stats[i][y][x] = new Statistics("");
                }
                pf.clearDataCache();
                double cx = area.getMinimum(0) + (0.5+x)*objCellSize;
                double cy = area.getMinimum(1) + (0.5+y)*objCellSize;

                values[0] = GF.createPoint(new Coordinate(cx,cy));
                int k=0;
                for(int b=0,n=nbBand;b<n;b++){
                    values[++k] = stats[b][y][x].count();
                    values[++k] = stats[b][y][x].minimum();
                    values[++k] = stats[b][y][x].mean();
                    values[++k] = stats[b][y][x].maximum();
                    values[++k] = stats[b][y][x].span();
                    values[++k] = stats[b][y][x].rms();
                    values[++k] = stats[b][y][x].sum();
                }

                renderCellFeature(feature, pf, renderers);
            }
        }

        //restore image interpolation
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,oldValue);
        renderingContext.getRenderingHints().put(RenderingHints.KEY_INTERPOLATION,oldValue);

    }

    private void renderCellFeature(Feature feature, final ProjectedFeature pf, DefaultCachedRule renderers) throws PortrayalException{
        boolean painted = false;
        for(int i=0; i<renderers.elseRuleIndex; i++){
            final CachedRule rule = renderers.rules[i];
            final Filter ruleFilter = rule.getFilter();
            //test if the rule is valid for this feature
            if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                painted = true;
                for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                    renderer.portray(pf);
                }
            }
        }

        //the feature hasn't been painted, paint it with the 'else' rules
        if(!painted){
            for(int i=renderers.elseRuleIndex; i<renderers.rules.length; i++){
                final CachedRule rule = renderers.rules[i];
                final Filter ruleFilter = rule.getFilter();
                //test if the rule is valid for this feature
                if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                    for (final SymbolizerRenderer renderer : renderers.renderers[i]) {
                        renderer.portray(pf);
                    }
                }
            }
        }
    }

    private AffineTransform calculateAverageAffine(final RenderingContext2D context,
            final GridCoverage2D coverage) throws FactoryException, TransformException{

        final MathTransform trs = context.getMathTransform(context.getObjectiveCRS(),coverage.getCoordinateReferenceSystem2D() );

        final Envelope refEnv = context.getCanvasObjectiveBounds();
        final GeneralEnvelope coverageEnv = Envelopes.transform(trs, refEnv);

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

    /**
     * {@inheritDoc }
     * <br>
     * Note : do nothing only return coverageSource.
     * In attempt to particulary comportement if exist.
     */
    @Override
    protected GridCoverage2D prepareCoverageToResampling(GridCoverage2D coverageSource, CachedCellSymbolizer symbolizer) {
        return coverageSource;
    }
}
