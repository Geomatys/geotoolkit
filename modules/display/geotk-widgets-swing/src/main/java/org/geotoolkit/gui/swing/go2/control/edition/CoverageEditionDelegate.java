/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.gui.swing.go2.control.edition;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.canvas.DefaultRenderingContext2D;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;
import org.geotoolkit.gui.swing.misc.JOptionDialog;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageEditionDelegate extends AbstractEditionDelegate {

    private final CoverageMapLayer layer;
    private final CoverageMapDecoration decoration = new CoverageMapDecoration();

    private int mouseX = 0;
    private int mouseY = 0;
    
    public CoverageEditionDelegate(JMap2D map, CoverageMapLayer layer) {
        super(map);
        this.layer = layer;
        
        try {
            GridCoverage cov = layer.getCoverageReader().read(layer.getImageIndex(), null);
            decoration.setCoverage((GridCoverage2D)cov);
        } catch (CoverageStoreException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        } catch (CancellationException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
        
    }

    @Override
    public MapDecoration getDecoration() {
        return decoration;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        this.mouseX = e.getX();
        this.mouseY = e.getY();
        decoration.repaint();
    }    

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        this.mouseX = e.getX();
        this.mouseY = e.getY();
        decoration.repaint();
        try {
            final Point mouseGridPosition = decoration.getMouseGridPosition();
            if(mouseGridPosition != null){
                
                final JPanel panel = new JPanel(new GridLayout(0, 2));
                final int nbSample = decoration.img.getSampleModel().getNumBands();
                final double[] samples = new double[nbSample];
                decoration.raster.getPixel(mouseGridPosition.x, mouseGridPosition.y, samples);    
                final List<JSpinner> spinners = new ArrayList<JSpinner>();
                for(int i=0;i<samples.length;i++){
                    panel.add(new JLabel(String.valueOf(i)));
                    final JSpinner spinner = new JSpinner();
                    spinner.setModel(new SpinnerNumberModel(samples[i],Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1));
                    spinners.add(spinner);
                    panel.add(spinner);
                }
                
                final int res = JOptionDialog.show(null, panel, JOptionPane.OK_CANCEL_OPTION);
                if(res == JOptionPane.OK_OPTION){
                    //update image
                    final CoverageReference ref = layer.getCoverageReference();
                    if(ref != null){
                        
                        
                        for(int i=0;i<samples.length;i++){
                            samples[i] = (Double)spinners.get(i).getValue();
                        }
                        decoration.raster.setPixel(mouseGridPosition.x, mouseGridPosition.y, samples);
                        
                        final GridCoverageWriter writer = ref.createWriter();
                        final GridCoverageBuilder gcb = new GridCoverageBuilder();
                        gcb.setRenderedImage(decoration.img);
                        gcb.setCoordinateReferenceSystem(decoration.coverage.getCoordinateReferenceSystem2D());
                        gcb.setGridToCRS(decoration.coverage.getGridGeometry().getGridToCRS2D());
                        writer.write(gcb.getGridCoverage2D(), null);
                        
                    }
                    
                }
                
            }
            
        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        } catch (DataStoreException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
        
        
    }
    
    private class CoverageMapDecoration extends JPanel implements MapDecoration {

        private JMap2D map = null;
        private DefaultRenderingContext2D context = null;
        private GridCoverage2D coverage = null; 
        private RenderedImage img;
        private WritableRaster raster;
        private double[] dataPoints;
        private CoordinateReferenceSystem lastObjCRS = null;
        private double[] objPoints;

        protected CoverageMapDecoration() {
            super(new BorderLayout());
            setOpaque(false);
        }

        @Override
        public void refresh() {
        }

        @Override
        public void dispose() {
        }

        public void setCoverage(GridCoverage2D coverage) {
            this.coverage = coverage;
            this.img = coverage.getRenderedImage();
            this.raster = img.copyData(null);
            this.img = new BufferedImage(img.getColorModel(), raster, img.getColorModel().isAlphaPremultiplied(), null);
                        
            final int height = img.getHeight();
            final int width = img.getWidth();
            dataPoints = new double[(width+1)*(height+1)*2];
            int i=0;
            for(int y=0; y<=height; y++){
                for(int x=0; x<=width; x++){
                    dataPoints[i] = x;
                    i++;
                    dataPoints[i] = y;
                    i++;
                }
            }
            
            final MathTransform gridTodata = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
            try{
                gridTodata.transform(dataPoints, 0, dataPoints, 0, i/2);
            }catch(Exception ex){
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
            
        }
        
        private double[] getObjPoints(CoordinateReferenceSystem objCRS) throws FactoryException, TransformException{
            if(objPoints == null || lastObjCRS != objCRS){
                lastObjCRS = objCRS;
                objPoints = new double[dataPoints.length];
                final MathTransform dataToObj = CRS.findMathTransform(coverage.getCoordinateReferenceSystem(), objCRS);
                dataToObj.transform(dataPoints, 0, objPoints, 0, dataPoints.length/2);
            }
            return objPoints;
        }
        
        
        public Point getMouseGridPosition() throws FactoryException, TransformException{
            final MathTransform gridTodata = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
            final double[] coords = new double[2];
            coords[0] = mouseX;
            coords[1] = mouseY;
            final MathTransform dispToObj = context.getDisplayToObjective();
            final MathTransform objToData = CRS.findMathTransform(context.getObjectiveCRS2D(),coverage.getCoordinateReferenceSystem());
            final MathTransform dataToGrid = gridTodata.inverse();

            dispToObj.transform(coords, 0, coords, 0, 1);
            objToData.transform(coords, 0, coords, 0, 1);
            dataToGrid.transform(coords, 0, coords, 0, 1);
            final int gridX = (int)coords[0];
            final int gridY = (int)coords[1];
            
            if(   gridX>=0 && gridX <img.getWidth()
                   && gridY>=0 && gridY <img.getHeight()){
                return new Point(gridX, gridY);
            }
            return null;
        }
        
        @Override
        public void setMap2D(final JMap2D map) {
            this.map = map;
            
            if(map != null && map.getCanvas() instanceof J2DCanvas){
                context = new DefaultRenderingContext2D(map.getCanvas());
            }else{
                context = null;
            }
        }

        @Override
        public JMap2D getMap2D() {
            return map;
        }

        @Override
        public JComponent getComponent() {
            return this;
        }

        @Override
        protected void paintComponent(Graphics gg) {
            super.paintComponent(gg);
            
            final Graphics2D g = (Graphics2D) gg;
            
            //disable anti-aliasing
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            //check if the map has a Java2D canvas
            if(map == null) return;
            final ReferencedCanvas2D candidate = map.getCanvas();
            if(!(candidate instanceof J2DCanvas)) return;

            final J2DCanvas canvas = (J2DCanvas) candidate;
            canvas.prepareContext(context,(Graphics2D) g.create(), null);
            
            if(raster == null) return;
            
            final Rectangle area = context.getCanvasDisplayBounds();
            
            try{
                final MathTransform gridTodata = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
                final MathTransform dataToObj = CRS.findMathTransform(coverage.getCoordinateReferenceSystem(), context.getObjectiveCRS2D());
                final MathTransform objToDisp = context.getObjectiveToDisplay();
                                
                final MathTransform gridToDisp = MathTransforms.concatenate(gridTodata, dataToObj, objToDisp);
                
                g.setColor(Color.GRAY);    
                g.setStroke(new BasicStroke(1));
                
                final double[] pointObj = getObjPoints(context.getObjectiveCRS2D());                
                final double[] pointdisp = Arrays.copyOf(pointObj, pointObj.length);
                objToDisp.transform(pointdisp, 0, pointdisp, 0, pointdisp.length/2);
                
                final int height = img.getHeight();
                final int width = img.getWidth() + 1; //there is one extra point per line
                final double[] coords = new double[8];
                for(int y=0; y<height; y++){
                    for(int x=0; x<width-1; x++){
                        coords[0] = pointdisp[(    y*width+x  )*2]; coords[1] = pointdisp[(    y*width+x  )*2+1];
                        coords[2] = pointdisp[(    y*width+x+1)*2]; coords[3] = pointdisp[(    y*width+x+1)*2+1];
                        coords[4] = pointdisp[((y+1)*width+x+1)*2]; coords[5] = pointdisp[((y+1)*width+x+1)*2+1];
                        coords[6] = pointdisp[((y+1)*width+x  )*2]; coords[7] = pointdisp[((y+1)*width+x  )*2+1];
                        
                        if( coords[2] < area.x || coords[0] > area.x+area.width
                         || coords[5] < area.y || coords[1] > area.y+area.height){
                            continue;
                        }
                        
                        if(x==0){
                            g.drawLine((int)coords[2], (int)coords[3], (int)coords[4], (int)coords[5]);
                            g.drawLine((int)coords[6], (int)coords[7], (int)coords[0], (int)coords[1]);                            
                        }else{
                            g.drawLine((int)coords[2], (int)coords[3], (int)coords[4], (int)coords[5]);                            
                        }
                        
                        if(y==0){
                            g.drawLine((int)coords[0], (int)coords[1], (int)coords[2], (int)coords[3]);
                            g.drawLine((int)coords[4], (int)coords[5], (int)coords[6], (int)coords[7]);
                        }else{
                            g.drawLine((int)coords[4], (int)coords[5], (int)coords[6], (int)coords[7]);
                        }
                    }
                }
                
                
                final Point mouseGridPosition = getMouseGridPosition();
                if(mouseGridPosition != null){
                    final int gridX = mouseGridPosition.x;
                    final int gridY = mouseGridPosition.y;
                    coords[0] = gridX;      coords[1] = gridY;
                    coords[2] = gridX+1;    coords[3] = gridY;
                    coords[4] = gridX+1;    coords[5] = gridY+1;
                    coords[6] = gridX;      coords[7] = gridY+1;

                    gridToDisp.transform(coords, 0, coords, 0, 4);
                    
                    final GeneralPath path = new GeneralPath();
                    path.moveTo(coords[0], (int)coords[1]);
                    path.lineTo(coords[2], (int)coords[3]);
                    path.lineTo(coords[4], (int)coords[5]);
                    path.lineTo(coords[6], (int)coords[7]);
                    path.lineTo(coords[0], (int)coords[1]);
                    path.closePath();

                    g.setColor(Color.RED);            
                    g.fill(path);
                    
                    final int nbSample = img.getSampleModel().getNumBands();
                    final double[] samples = new double[nbSample];
                    raster.getPixel(gridX, gridY, samples);                
                    final String str = Arrays.toString(samples);
                    final FontMetrics fm = g.getFontMetrics();
                    final Rectangle rect = fm.getStringBounds(str, g).getBounds();
                    g.setColor(Color.WHITE);
                    rect.x = mouseX +4;
                    rect.y = mouseY - fm.getHeight();
                    rect.height = rect.height +4;
                    rect.width = rect.width +4;
                    g.fill(rect);
                    g.setColor(Color.BLACK);
                    g.drawString(str, mouseX+6, mouseY);
                    
                }
                
            }catch(Exception ex ){
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
            
            
            
        }
        
    }
    
}
