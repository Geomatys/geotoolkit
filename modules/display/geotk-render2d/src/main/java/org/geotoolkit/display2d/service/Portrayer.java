/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.display2d.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import static org.geotoolkit.display2d.GO2Utilities.*;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.display2d.container.ContextContainer2D;
import static org.geotoolkit.display2d.service.DefaultPortrayalService.*;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.bandselect.BandSelectProcess;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Symbolizer;

/**
 * Portrayal data, caches the Java2D canvas for further reuse.
 * This class is not thread safe.
 *
 * @author Johann Sorel (geomatys)
 */
public final class Portrayer {

    private static final MapContext EMPTY_CONTEXT = MapBuilder.createContext();

    /**
     * Cache the last CoverageWriter.
     */
    private GridCoverageWriter coverageWriter = null;

    private final J2DCanvasBuffered canvas = new J2DCanvasBuffered(CommonCRS.WGS84.normalizedGeographic(), new Dimension(1, 1));
    private final ContextContainer2D container = new ContextContainer2D(canvas, false);

    public Portrayer(){
        canvas.setContainer(container);
        container.setContext(EMPTY_CONTEXT);
    }

    public BufferedImage portray(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef) throws PortrayalException{

        final Envelope contextEnv = viewDef.getEnvelope();
        final CoordinateReferenceSystem crs = contextEnv.getCoordinateReferenceSystem();

        canvas.setSize(canvasDef.getDimension());
        canvas.setRenderingHints(sceneDef.getHints());


        final Color bgColor = canvasDef.getBackground();
        if(bgColor != null){
            canvas.setBackgroundPainter(new SolidColorPainter(bgColor));
        }

        final CanvasMonitor monitor = viewDef.getMonitor();
        if(monitor != null){
            canvas.setMonitor(monitor);
        }

        final MapContext context = sceneDef.getContext();
        container.setContext(context);
        try {
            canvas.setObjectiveCRS(crs);
        } catch (TransformException ex) {
            throw new PortrayalException("Could not set objective crs",ex);
        }

        //we specifically say to not repect X/Y proportions
        if(canvasDef.isStretchImage()) canvas.setAxisProportions(Double.NaN);
        try {
            canvas.setVisibleArea(contextEnv);
            if (viewDef.getAzimuth() != 0) {
                canvas.rotate( -Math.toRadians(viewDef.getAzimuth()) );
            }
        } catch (NoninvertibleTransformException ex) {
            throw new PortrayalException(ex);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        //paints all extensions
        final List<PortrayalExtension> extensions = sceneDef.extensions();
        if(extensions != null){
            for(final PortrayalExtension extension : extensions){
                if(extension != null) extension.completeCanvas(canvas);
            }
        }

        canvas.repaint();
        final BufferedImage buffer = canvas.getSnapShot();
        container.setContext(EMPTY_CONTEXT);

        return buffer;
    }


    /**
     *
     * @param canvasDef
     * @param sceneDef
     * @param viewDef
     * @param outputDef : The compression parameter will not necesarly be used
     *              if the mime type write can not support it.
     * @throws PortrayalException
     */
    public void portray(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef,
            final OutputDef outputDef) throws PortrayalException{

        final String mime = outputDef.getMime();
        if(mime.contains("jpeg") || mime.contains("jpg")){
            //special case for jpeg format, the writer generate incorrect colors
            //if he find out an alpha channel, so we ensure to have a opaque background
            //which will result in at least an RGB palette
            final Color bgColor = canvasDef.getBackground();
            if(bgColor == null){
                //we set the background white
                canvasDef.setBackground(Color.WHITE);
            }else{
                //we merge colors
                canvasDef.setBackground(mergeColors(Color.WHITE, bgColor));
            }
        }

        //directly return false if hints doesnt contain the coverage writer hint enabled
        final Hints hints = sceneDef.getHints();
        final Object val = (hints!=null)?hints.get(GO2Hints.KEY_COVERAGE_WRITER):null;
        final boolean useCoverageWriter = GO2Hints.COVERAGE_WRITER_ON.equals(val);

        if(useCoverageWriter && portrayAsCoverage(canvasDef, sceneDef, viewDef, outputDef)){
            //we succeeded in writing it with coverage writer directly.
            return;
        }

        //use the rendering engine to generate an image
        final BufferedImage image = portray(canvasDef,sceneDef,viewDef);

        if(image == null){
            throw new PortrayalException("No image created by the canvas.");
        }

        if(useCoverageWriter){
            final Envelope env = viewDef.getEnvelope();
            final Dimension dim = canvasDef.getDimension();
            final double[] resolution = new double[]{
                    env.getSpan(0) / (double)dim.width,
                    env.getSpan(1) / (double)dim.height};

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setEnvelope(env);
            gcb.setRenderedImage(image);
            final GridCoverage coverage = gcb.getGridCoverage2D();
            writeCoverage(coverage, env, resolution, outputDef,null);
        }else{
            try {
                writeImage(image, outputDef);
            } catch (IOException ex) {
                throw new PortrayalException(ex);
            }
        }

    }

    /**
     * Detect single raster layers with a default raster style and no special parameters
     * if so we can directly use the grid coverage writer and avoid the rendering chain.
     * It significantly reduce memory usage (minus the buffered image size and graphic objects)
     * and time (minus ~35%).
     *
     * @return true if the optimization have been applied.
     * @throws PortrayalException
     */
    private boolean portrayAsCoverage(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef,
            final OutputDef outputDef) throws PortrayalException {

        //works for one layer only
        final List<MapLayer> layers = sceneDef.getContext().layers();
        if(layers.size() != 1) return false;

        //layer must be a coverage
        final MapLayer layer = layers.get(0);
        final Resource resource = layer.getResource();
        if(!(resource instanceof GridCoverageResource)) return false;

        //we must not have extensions
        if(!sceneDef.extensions().isEmpty()) return false;

        //style must be a default raster style = native original style
        final List<MutableFeatureTypeStyle> ftss = layer.getStyle().featureTypeStyles();
        if(ftss.size() != 1) return false;
        final List<MutableRule> rules = ftss.get(0).rules();
        if(rules.size() != 1) return false;
        final List<Symbolizer> symbols = rules.get(0).symbolizers();
        if(symbols.size() != 1) return false;
        final Symbolizer s = symbols.get(0);
        if(!GO2Utilities.isDefaultRasterSymbolizer(s)) return false;

        //we can bypass the renderer
        try{
            final GridCoverageResource ref = (GridCoverageResource) resource;
            final String mime = outputDef.getMime();
            final Envelope env = viewDef.getEnvelope();
            final Dimension dim = canvasDef.getDimension();
            final double[] resolution = new double[]{
                    env.getSpan(0) / (double)dim.width,
                    env.getSpan(1) / (double)dim.height};

            GridGeometry query = ref.getGridGeometry().derive().subgrid(env, resolution).sliceByRatio(0.5, 0, 1).build();

            GridCoverage coverage = ref.read(query);
            final RenderedImage image = coverage.render(null);

            // HACK TO FIX COLOR ERROR ON JPEG /////////////////////////////////
            if(mime.contains("jpeg") || mime.contains("jpg")){
                if(image.getColorModel().hasAlpha()){
                    final int nbBands = image.getSampleModel().getNumBands();
                    if(nbBands > 3){
                        //we can remove the fourth band assuming it is the alpha
                        coverage = new BandSelectProcess(CoverageUtilities.toGeotk(coverage), new int[]{0,1,2}).executeNow();
                    }
                }
            }
            ////////////////////////////////////////////////////////////////////

            writeCoverage(coverage, env, resolution, outputDef, canvasDef.getBackground());
        } catch (DataStoreException | ProcessException ex) {
            throw new PortrayalException(ex);
        }
        return true;
    }

    /**
     * Write a coverage using the canvas, view and output definition.
     *
     * @param coverage : coverage to write
     * @param canvasDef : canvas definition
     * @param viewDef : view definition
     * @param outputDef : outpout definition
     * @throws PortrayalException if writing failed
     */
    private void writeCoverage(final GridCoverage coverage, final Envelope env, final double[] resolution,
            final OutputDef outputDef, final Color backgroundColor) throws PortrayalException{
        final String mimeType = outputDef.getMime();

        String javaType = MIME_CACHE.get(mimeType);
        if(javaType == null){
            //search the mime type
            final String[] candidates = XImageIO.getFormatNamesByMimeType(mimeType, false, true);
            if(candidates.length > 0){
                javaType = candidates[0];
                //cache the resulting java type
                MIME_CACHE.put(mimeType, javaType);
            }
        }

        if(javaType == null){
            //no related java type, incorrect mime type
            throw new PortrayalException("No java type found for mime type : " + mimeType);
        }


        //get a writer
        GridCoverageWriter writer = coverageWriter;
        if(writer == null){
            writer = new ImageCoverageWriter();
        }

        try{
            final GridCoverageWriteParam writeParam = new GridCoverageWriteParam();
            writeParam.setEnvelope(env);
            writeParam.setResolution(resolution);
            writeParam.setFormatName(javaType);
            writeParam.setCompressionQuality(outputDef.getCompression());

            if(backgroundColor != null){
                final int r = backgroundColor.getRed();
                final int g = backgroundColor.getGreen();
                final int b = backgroundColor.getBlue();
                final int a = backgroundColor.getAlpha();
                writeParam.setBackgroundValues(new double[]{r,g,b,a});
            }

            writer.setOutput(outputDef.getOutput());
            writer.write(CoverageUtilities.toGeotk(coverage), writeParam);

        } catch (DataStoreException ex) {
            throw new PortrayalException(ex);
        } finally {
            try {
                writer.reset();
                coverageWriter = writer;
            } catch (DataStoreException ex) {
                //the writer has problems, we better not put in back in the cache.
                try {
                    writer.dispose();
                } catch (DataStoreException ex1) {
                    Logging.getLogger("org.geotoolkit.display2d.service").log(Level.WARNING, null, ex1);
                }
                throw new PortrayalException(ex);
            }
        }
    }


}
