/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 - 2014, Geomatys
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
package org.geotoolkit.gui.swing.render2d.control.edition;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.CoverageUtilities;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.*;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.navigation.PanHandler;
import org.geotoolkit.gui.swing.render2d.decoration.AbstractMapDecoration;
import org.geotoolkit.gui.swing.render2d.decoration.MapDecoration;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Coverage editor tool.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageEditionDelegate extends AbstractEditionDelegate {

    private static final ImageIcon ICON_COMMIT = IconBuilder.createIcon(FontAwesomeIcons.ICON_FLOPPY_O, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_ROLLBACK = IconBuilder.createIcon(FontAwesomeIcons.ICON_UNDO, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_SELECT = IconBuilder.createIcon(FontAwesomeIcons.ICON_LOCATION_ARROW, 16, FontAwesomeIcons.DEFAULT_COLOR);

    private final CoverageMapDecoration decoration;
    private final CoverageMapLayer layer;

    //mouse gesture variables
    private int mouseX = 0;
    private int mouseY = 0;
    private boolean selectAction = false;
    private Rectangle selectRectangle = null;

    //coverage edition variables
    private JMap2D map = null;
    private RenderingContext2D context = null;
    private GridCoverage2D coverage = null;
    // Write parameters discovered when setting the coverage
    private GridCoverageWriteParam writeParam = null;
    private final GridCoverageReadParam gcrp = new GridCoverageReadParam();
    private RenderedImage img;
    private WritableRaster raster;
    private CoordinateReferenceSystem lastObjCRS = null;
    private double[] dataPoints;
    private double[] objPoints;
    private final Rectangle gridSelectionSize = new Rectangle();
    //pixel which value has been changed
    private final List<Point> editedPixels = new ArrayList<Point>();

    public CoverageEditionDelegate(JMap2D map, CoverageMapLayer layer) {
        super(map);
        this.layer = layer;

        final GeneralEnvelope layerEnv = new GeneralEnvelope(layer.getBounds());
        this.decoration = new CoverageMapDecoration(map, layerEnv);
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
        decoration.getComponent().repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        this.mouseX = e.getX();
        this.mouseY = e.getY();
        decoration.getComponent().repaint();
        try {
            final Point mouseGridPosition = getMouseGridPosition();
            if(mouseGridPosition != null){

                final JPanel panel = new JPanel(new GridLayout(0, 2));
                panel.setFocusCycleRoot(true);
                final int nbSample = img.getSampleModel().getNumBands();
                final int sampleType = img.getSampleModel().getDataType();

                final double[] samples = new double[nbSample];
                raster.getPixel(mouseGridPosition.x, mouseGridPosition.y, samples);
                final List<JSpinner> spinners = new ArrayList<JSpinner>();
                for(int i=0;i<samples.length;i++){
                    panel.add(new JLabel(String.valueOf(i)));
                    final JSpinner spinner = new JSpinner();
                    if(i==0) spinner.requestFocus();

                    switch(sampleType){
                        case DataBuffer.TYPE_BYTE :
                            spinner.setModel(new SpinnerNumberModel((int)samples[i],0,255,1));
                            break;
                        case DataBuffer.TYPE_DOUBLE :
                            spinner.setModel(new SpinnerNumberModel(samples[i],Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1));
                            break;
                        case DataBuffer.TYPE_FLOAT :
                            spinner.setModel(new SpinnerNumberModel(samples[i],Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY,1));
                            break;
                        case DataBuffer.TYPE_INT :
                            spinner.setModel(new SpinnerNumberModel((int)samples[i],Integer.MIN_VALUE,Integer.MAX_VALUE,1));
                            break;
                        case DataBuffer.TYPE_SHORT :
                            spinner.setModel(new SpinnerNumberModel((int)samples[i],Short.MIN_VALUE,Short.MAX_VALUE,1));
                            break;
                        case DataBuffer.TYPE_USHORT :
                            spinner.setModel(new SpinnerNumberModel((int)samples[i],0,Short.MAX_VALUE*2,1));
                            break;
                        default :
                            spinner.setModel(new SpinnerNumberModel(samples[i],Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1));
                    }

                    spinners.add(spinner);
                    panel.add(spinner);
                }

                final int res = JOptionDialog.show(map, panel, JOptionPane.OK_CANCEL_OPTION);
                if(res == JOptionPane.OK_OPTION){
                    editedPixels.add(mouseGridPosition);
                    //update image
                    final CoverageReference ref = layer.getCoverageReference();
                    if(ref != null){
                        for(int i=0;i<samples.length;i++){
                            samples[i] = ((Number)spinners.get(i).getValue()).doubleValue();
                        }
                        raster.setPixel(mouseGridPosition.x, mouseGridPosition.y, samples);
                    }

                }

            }

        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(selectAction){
            if(selectRectangle == null){
                selectRectangle = new Rectangle(e.getX(),e.getY(),1,1);
            }

            selectRectangle.width = e.getX() - selectRectangle.x;
            selectRectangle.height = e.getY() - selectRectangle.y;
            decoration.getComponent().repaint();
        }else{
            super.mouseDragged(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(selectAction){
            selectAction = false;

            final Envelope visibleArea = getMap().getCanvas().getVisibleEnvelope();
            try {
                gcrp.clear();
                gcrp.setEnvelope(visibleArea);
                final CoverageReference ref = layer.getCoverageReference();
                final CoverageReader reader = ref.acquireReader();
                final GridCoverage2D cov = (GridCoverage2D) reader.read(ref.getImageIndex(), gcrp);
                ref.recycle(reader);
                setCoverage(cov,selectRectangle);
                writeParam = new GridCoverageWriteParam();
                final MathTransform grid_To_Crs = cov.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
                GeneralEnvelope env = new GeneralEnvelope(PredefinedCRS.CARTESIAN_2D);
                env.setEnvelope(gridSelectionSize.x, gridSelectionSize.y, gridSelectionSize.x + gridSelectionSize.width, gridSelectionSize.y + gridSelectionSize.height);
                env = CRS.transform(grid_To_Crs, env);

                final CoordinateReferenceSystem covCRS = cov.getCoordinateReferenceSystem();
                GeneralEnvelope vAInCovArea = new GeneralEnvelope(visibleArea);
                vAInCovArea = new GeneralEnvelope(ReferencingUtilities.transform2DCRS(vAInCovArea, CRSUtilities.getCRS2D(covCRS)));

                int minOrdinate = CoverageUtilities.getMinOrdinate(vAInCovArea.getCoordinateReferenceSystem());
                vAInCovArea.setRange(minOrdinate++, env.getMinimum(0), env.getMaximum(0));
                vAInCovArea.setRange(minOrdinate, env.getMinimum(1), env.getMaximum(1));
                writeParam.setEnvelope(vAInCovArea);
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                writeParam = null;
            }
            selectRectangle = null;
            decoration.getComponent().repaint();
        }
        super.mouseReleased(e);
    }

    /**
     * return the current editer area points.
     */
    private double[] getObjPoints(CoordinateReferenceSystem objCRS) throws FactoryException, TransformException{
        if(objPoints == null || lastObjCRS != objCRS){
            lastObjCRS = objCRS;
            objPoints = new double[dataPoints.length];
            final MathTransform dataToObj = CRS.findMathTransform(coverage.getCoordinateReferenceSystem(), objCRS);
            dataToObj.transform(dataPoints, 0, objPoints, 0, dataPoints.length/2);
        }
        return objPoints;
    }

    private void setCoverage(GridCoverage2D coverage, Rectangle selectionRectangle)
            throws FactoryException, NoninvertibleTransformException, TransformException {
        this.editedPixels.clear();
        this.objPoints = null;
        this.dataPoints = null;
        this.raster = null;
        this.img = null;
        this.coverage = coverage;
        if(coverage == null || selectionRectangle == null) return;
        this.img = coverage.getRenderedImage();
        this.raster = img.copyData(null);
        this.img = new BufferedImage(img.getColorModel(), raster, img.getColorModel().isAlphaPremultiplied(), null);

        final MathTransform gridTodata = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
        final MathTransform dispToObj = context.getDisplayToObjective();
        final MathTransform objToData = CRS.findMathTransform(context.getObjectiveCRS2D(),coverage.getCoordinateReferenceSystem());
        final MathTransform dataToGrid = gridTodata.inverse();

        final double[] coords = new double[8];
        coords[0] = selectionRectangle.x;                       coords[1] = selectionRectangle.y;
        coords[2] = selectionRectangle.x+selectRectangle.width;   coords[3] = selectionRectangle.y;
        coords[4] = selectionRectangle.x+selectRectangle.width;   coords[5] = selectionRectangle.y+selectRectangle.height;
        coords[6] = selectionRectangle.x;                       coords[7] = selectionRectangle.y+selectRectangle.height;
        dispToObj.transform(coords, 0, coords, 0, 4);
        objToData.transform(coords, 0, coords, 0, 4);
        dataToGrid.transform(coords, 0, coords, 0, 4);
        double minX = coords[0];
        double maxX = coords[0];
        double minY = coords[1];
        double maxY = coords[1];
        for(int i=0;i<8;i+=2){
            minX = Math.min(minX,coords[i]);
            maxX = Math.max(maxX,coords[i]);
            minY = Math.min(minY,coords[i+1]);
            maxY = Math.max(maxY,coords[i+1]);
        }

        final int imageHeight = img.getHeight();
        final int imageWidth = img.getWidth();

        final int startX = (int) minX;
        final int endX   = Math.min( (int)(maxX+0.5), imageWidth);
        final int startY = (int) minY;
        final int endY   = Math.min( (int)(maxY+0.5), imageHeight);

        final int height = endY-startY;
        final int width = endX-startX;
        if(height <=0 || width <=0){
            //invalid edition area
            this.raster = null;
            this.img = null;
            return;
        }
        gridSelectionSize.x = startX;
        gridSelectionSize.y = startY;
        gridSelectionSize.height = height;
        gridSelectionSize.width = width;
        dataPoints = new double[(width+1)*(height+1)*2];
        int i=0;
        for(int y=startY; y<=endY; y++){
            for(int x=startX; x<=endX; x++){
                dataPoints[i] = x;
                i++;
                dataPoints[i] = y;
                i++;
            }
        }

        try{
            gridTodata.transform(dataPoints, 0, dataPoints, 0, i/2);
        }catch(Exception ex){
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }

    }

    private Point getMouseGridPosition() throws FactoryException, TransformException{
        if(coverage == null) return null;
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

        if(gridSelectionSize.contains(gridX, gridY)){
            return new Point(gridX, gridY);
        }
        return null;
    }

    private void save() throws DataStoreException{
        if (layer == null || coverage == null) {
            return;
        }
        final CoverageReference ref = layer.getCoverageReference();
        final GridCoverageWriter writer = ref.acquireWriter();
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(img);
        gcb.setCoordinateReferenceSystem(coverage.getCoordinateReferenceSystem2D());
        gcb.setGridToCRS(coverage.getGridGeometry().getGridToCRS2D());
        writer.write(gcb.getGridCoverage2D(), writeParam);
        ref.recycle(writer);
        this.editedPixels.clear();
        map.getCanvas().repaint();
    }


    private class CoverageMapDecoration extends AbstractMapDecoration implements PropertyChangeListener {

        private final JPanel container = new GridComponent();

        private GeneralEnvelope layerEnvelope;
        private boolean selectEnable = true;

        protected CoverageMapDecoration(JMap2D map, GeneralEnvelope layerEnvelope) {
            map.getCanvas().addPropertyChangeListener(this);
            this.layerEnvelope = layerEnvelope;

            final Envelope visibleArea = getMap().getCanvas().getVisibleEnvelope();
            checkEditable(new GeneralEnvelope(visibleArea));
            initComponent();
        }

        /**
         * Check if the current canvas envelope is contained in layer envelope.
         *
         * @param canvasEnv
         */
        private void checkEditable (GeneralEnvelope canvasEnv) {
            if (layerEnvelope != null) {
                int canvasDims = canvasEnv.getDimension();
                int layerDim = layerEnvelope.getDimension();

                if (canvasDims == layerDim) {
                    try {
                        canvasEnv = new GeneralEnvelope(CRS.transform(canvasEnv, layerEnvelope.getCoordinateReferenceSystem()));
                        selectEnable = canvasEnv.intersects(layerEnvelope, true);
                    } catch (TransformException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                        selectEnable = false;
                    }
                } else {
                    selectEnable = false;
                }
                return;
            }
            selectEnable = true;
        }

        private void initComponent() {
            //clear
            container.removeAll();

            container.setLayout(new BorderLayout());
            container.setOpaque(false);
            container.setFocusable(false);

            final JPanel guiRight = new JPanel(new BorderLayout(10, 10));
            guiRight.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

            //NORTH

            //select area
            final JButton guiSelect = new JButton(MessageBundle.getString("select_area"));
            guiSelect.setIcon(ICON_SELECT);
            guiSelect.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectAction = true;
                }
            });
            guiSelect.setEnabled(selectEnable);


            //CENTER
            final JPanel guiTools = new JPanel(new BorderLayout(0, 0));
            guiTools.add(BorderLayout.NORTH,new JSeparator(SwingConstants.HORIZONTAL));
            if (!selectEnable) {
                final JPanel warningPanel = new JPanel(new BorderLayout(0, 0));
                final JLabel warningMessage = new JLabel();
                StringBuilder sb = new StringBuilder("<html><p width=\"120px\">");
                sb.append(MessageBundle.getString("edition.coverage.warrningdimension"));
                sb.append("</p></html>");
                warningMessage.setText(sb.toString());
                warningPanel.add(BorderLayout.NORTH, warningMessage);
                guiTools.add(BorderLayout.CENTER, warningPanel);
            }
            guiTools.add(BorderLayout.SOUTH,new JSeparator(SwingConstants.HORIZONTAL));


            //SOUTH
            final JPanel guiApplyPane = new JPanel(new GridLayout(0,1,2,2));

            //save edition
            final JButton guiSave = new JButton(MessageBundle.getString("save"));
            guiSave.setIcon(ICON_COMMIT);
            guiSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        save();
                    } catch (DataStoreException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(),ex);
                    }
                }
            });
            guiApplyPane.add(guiSave);

            //cancel edition
            final JButton guiCancel = new JButton(MessageBundle.getString("cancel"));
            guiCancel.setIcon(ICON_ROLLBACK);
            guiCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        setCoverage(null,null);
                        map.setHandler(new PanHandler(map,false));
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(),ex);
                    }
                }
            });
            guiApplyPane.add(guiCancel);

            guiRight.add(BorderLayout.NORTH, guiSelect);
            guiRight.add(BorderLayout.CENTER, guiTools);
            guiRight.add(BorderLayout.SOUTH, guiApplyPane);

            container.add(BorderLayout.EAST,guiRight);
        }

        @Override
        public void refresh() {
            initComponent();
            container.revalidate();
            container.repaint();
        }

        @Override
        public void setMap2D(final JMap2D map) {
            super.setMap2D(map);
            CoverageEditionDelegate.this.map = map;

            if(map != null && map.getCanvas() != null){
                context = new RenderingContext2D(map.getCanvas());
            }else{
                context = null;
            }
        }

        @Override
        public JComponent getComponent() {
            return container;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(J2DCanvas.ENVELOPE_KEY)) {
                final Envelope newEnv = (Envelope) evt.getNewValue();
                checkEditable(new GeneralEnvelope(newEnv));
                this.refresh();
            }
        }
    }

    private class GridComponent extends JPanel{

        @Override
        protected void paintComponent(final Graphics gg) {
            super.paintComponent(gg);
            final Graphics2D g = (Graphics2D) gg;

            //disable anti-aliasing
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            paintGrid(g);

            if (selectRectangle != null) {
                g.setColor(Color.ORANGE);
                g.setStroke(new BasicStroke(2));
                g.draw(selectRectangle);
            }

        }

        /**
         * Paint edit grid and mouse over pixel
         */
        private void paintGrid(final Graphics2D g){
             //check if the map has a Java2D canvas
            if(map == null) return;
            final J2DCanvas canvas = map.getCanvas();
            if(!(canvas != null)) return;

            canvas.prepareContext(context,(Graphics2D) g.create(), null);

            if(raster == null) return;

            final Rectangle area = context.getCanvasDisplayBounds();

            try{
                final MathTransform gridTodata = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
                final MathTransform dataToObj = CRS.findMathTransform(coverage.getCoordinateReferenceSystem(), context.getObjectiveCRS2D());
                final MathTransform objToDisp = context.getObjectiveToDisplay();
                final MathTransform gridToDisp = MathTransforms.concatenate(gridTodata, dataToObj, objToDisp);


                // paint grid --------------------------------------------------
                g.setColor(Color.GRAY);
                g.setStroke(new BasicStroke(1));

                final double[] pointObj = getObjPoints(context.getObjectiveCRS2D());
                final double[] pointdisp = Arrays.copyOf(pointObj, pointObj.length);
                objToDisp.transform(pointdisp, 0, pointdisp, 0, pointdisp.length/2);

                final int height = gridSelectionSize.height;
                final int width = gridSelectionSize.width + 1; //there is one extra point per line
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

                        g.drawLine((int)coords[2], (int)coords[3], (int)coords[4], (int)coords[5]);
                        if(x==0) g.drawLine((int)coords[6], (int)coords[7], (int)coords[0], (int)coords[1]);
                        g.drawLine((int)coords[4], (int)coords[5], (int)coords[6], (int)coords[7]);
                        if(y==0) g.drawLine((int)coords[0], (int)coords[1], (int)coords[2], (int)coords[3]);
                    }
                }

                //paint edited pixels ------------------------------------------
                g.setStroke(new BasicStroke(1));
                for(Point pt : editedPixels){
                    final int gridX = pt.x;
                    final int gridY = pt.y;
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

                    g.setColor(Color.YELLOW);
                    g.fill(path);
                    g.setColor(Color.BLACK);
                    g.draw(path);
                    g.drawLine((int)coords[0], (int)coords[1], (int)coords[4], (int)coords[5]);
                    g.drawLine((int)coords[2], (int)coords[3], (int)coords[6], (int)coords[7]);
                }

                //paint mouse position -----------------------------------------
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
