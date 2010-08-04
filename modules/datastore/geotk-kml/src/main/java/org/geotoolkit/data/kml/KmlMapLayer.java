/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractObject;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.BalloonStyle;
import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.LatLonAltBox;
import org.geotoolkit.data.kml.model.LatLonBox;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LineStyle;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.MultiGeometry;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.Polygon;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.model.StyleMap;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.map.DynamicMapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.MutableStyle;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class KmlMapLayer extends AbstractMapLayer implements DynamicMapLayer {

    private static final GeometryFactory GF = new GeometryFactory();
    private final Kml kml;
    private RenderingContext2D context2d;
    private Map<String,Style> styles = new HashMap<String, Style>();
    private Map<String,StyleMap> styleMaps = new HashMap<String, StyleMap>();
    private final Font FONT = new Font("KmlMapLayerFont", Font.ROMAN_BASELINE, 10);

    public KmlMapLayer(MutableStyle style, Kml kml) {
        super(style);
        this.kml = kml;
    }

    @Override
    public Envelope getBounds() {
        return null;
    }

    @Override
    public Object query(RenderingContext context) throws PortrayalException {
        return null;
    }

    @Override
    public Image getLegend() throws PortrayalException {
        return null;
    }

    @Override
    public void portray(RenderingContext context) throws PortrayalException {
        if (!(context instanceof RenderingContext2D)) {
            return;
        }

        context2d = (RenderingContext2D) context;
        try {
            this.portrayKml(this.kml);
        } catch (IOException ex) {
            Logger.getLogger(KmlMapLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param kml
     * @throws IOException
     */
    private void portrayKml(Kml kml) throws IOException{
        if (kml.getAbstractFeature() != null){
            this.portrayAbstractFeature(kml.getAbstractFeature());
        }
    }

    /**
     * 
     * @param abstractFeature
     * @throws IOException
     */
    private void portrayAbstractFeature(Feature abstractFeature) throws IOException{
        if (FeatureTypeUtilities.isDecendedFrom(abstractFeature.getType(), KmlModelConstants.TYPE_CONTAINER)){
            this.portrayAbstractContainer(abstractFeature);
        } else if (FeatureTypeUtilities.isDecendedFrom(abstractFeature.getType(), KmlModelConstants.TYPE_OVERLAY)){
            this.portrayAbstractOverlay(abstractFeature);
        } else if (abstractFeature.getType().equals(KmlModelConstants.TYPE_PLACEMARK)){
            this.portrayPlacemark(abstractFeature);
        }
    }

    /**
     *
     * @param abstractContainer
     * @throws IOException
     */
    private void portrayAbstractContainer(Feature abstractContainer) throws IOException {
        if (abstractContainer.getType().equals(KmlModelConstants.TYPE_FOLDER)){
            this.portrayFolder(abstractContainer);
        } else if (abstractContainer.getType().equals(KmlModelConstants.TYPE_DOCUMENT)){
            this.portrayDocument(abstractContainer);
        }
    }

    /**
     *
     * @param placemark
     * @throws IOException
     */
    private void portrayPlacemark(Feature placemark) throws IOException{
        this.portrayCommonAbstractFeature(placemark);

        context2d.switchToObjectiveCRS();
        Graphics2D graphic = context2d.getGraphics();

        // Apply styles
        Style s = this.retrieveStyle(placemark);

        // display geometries
        if (placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()) != null){
            AbstractGeometry geometry = (AbstractGeometry) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            if(geometry != null){
                this.portrayAbstractGeometry(geometry,s);
            }
        }

        Region region = ((Region) placemark.getProperty(KmlModelConstants.ATT_REGION.getName()).getValue());
        if(region != null){
            LatLonAltBox latLonAltBox = region.getLatLonAltBox();
            portrayBalloonStyle((latLonAltBox.getEast()+latLonAltBox.getWest())/2,
                    (latLonAltBox.getNorth()+latLonAltBox.getSouth())/2,
                    s, false);
            portrayLabelStyle((latLonAltBox.getEast()+latLonAltBox.getWest())/2,
                    (latLonAltBox.getNorth()+latLonAltBox.getSouth())/2,
                    s,
                    (String) placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        }
    }

    /**
     *
     * @param abstractFeature
     */
    private void portrayCommonAbstractFeature(Feature abstractFeature){
        Iterator i;
        if (abstractFeature.getProperty(KmlModelConstants.ATT_STYLE_SELECTOR.getName()) != null){
            i = abstractFeature.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();
            while(i.hasNext()){
                this.portrayAbstractStyleSelector((AbstractStyleSelector) ((Property) i.next()).getValue());
            }
        }
    }

    /**
     *
     * @param abstractGeometry
     * @param style
     * @throws IOException
     */
    private void portrayAbstractGeometry(AbstractGeometry abstractGeometry, Style style) throws IOException{
        if (abstractGeometry instanceof MultiGeometry){
            this.portrayMultiGeometry((MultiGeometry) abstractGeometry, style);
        } else if (abstractGeometry instanceof LineString){
            this.portrayLineString((LineString) abstractGeometry, style);
        } else if (abstractGeometry instanceof Polygon){
            this.portrayPolygon((Polygon) abstractGeometry, style);
        } else if (abstractGeometry instanceof Point){
            this.portrayPoint((Point) abstractGeometry, style);
        } else if (abstractGeometry instanceof LinearRing){
            this.portrayLinearRing((LinearRing) abstractGeometry, style);
        }
    }

    /**
     *
     * @param abstractObject
     */
    public void portrayCommonAbstractObject(AbstractObject abstractObject){
    }

    /**
     *
     * @param abstractGeometry
     */
    public void portrayCommonAbstractGeometry(AbstractGeometry abstractGeometry){

        this.portrayCommonAbstractObject(abstractGeometry);
    }

    /**
     *
     * @param lineString
     * @param style
     */
    private void portrayLineString(LineString lineString, Style style){
        this.portrayCommonAbstractGeometry(lineString);

        // MathTransform
        MathTransform transform = null;
        context2d.switchToDisplayCRS();
        Graphics2D graphic = context2d.getGraphics();
        com.vividsolutions.jts.geom.Geometry ls = null;

        try {
            transform = context2d.getMathTransform(DefaultGeographicCRS.WGS84, context2d.getDisplayCRS());
            ls = JTS.transform((com.vividsolutions.jts.geom.LineString) lineString, transform);
        } catch (MismatchedDimensionException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        } catch (TransformException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        } catch (FactoryException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        }

        LineStyle lineStyle = style.getLineStyle();
        if(lineStyle != null){
            graphic.setColor(lineStyle.getColor());
            graphic.setStroke(new BasicStroke((float) (lineStyle.getWidth())));
        }

        Shape shape = new JTSGeometryJ2D(ls);
        graphic.draw(shape);
    }

    /**
     *
     * @param multiGeometry
     * @param style
     * @throws IOException
     */
    private void portrayMultiGeometry(MultiGeometry multiGeometry, Style style) throws IOException {
        this.portrayCommonAbstractGeometry(multiGeometry);
        for (AbstractGeometry abstractGeometry : multiGeometry.getGeometries()){
            this.portrayAbstractGeometry(abstractGeometry, style);
        }
    }

    /**
     *
     * @param polygon
     * @param style
     */
    private void portrayPolygon(Polygon polygon, Style style) {
        this.portrayCommonAbstractGeometry(polygon);

        // MathTransform
        MathTransform transform = null;
        context2d.switchToDisplayCRS();
        Graphics2D graphic = context2d.getGraphics();
        com.vividsolutions.jts.geom.Geometry pol = null;

        try {
            transform = context2d.getMathTransform(DefaultGeographicCRS.WGS84, context2d.getDisplayCRS());
            pol = JTS.transform((com.vividsolutions.jts.geom.Polygon) polygon, transform);
        } catch (MismatchedDimensionException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        } catch (TransformException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        } catch (FactoryException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        }

        Shape shape = new JTSGeometryJ2D(pol);

        // Apply styles
        PolyStyle polyStyle = style.getPolyStyle();
        if (polyStyle != null){
            graphic.setColor(polyStyle.getColor());
            graphic.setStroke(new BasicStroke((float) 0.05));
            if(style.getPolyStyle().getFill()){
                graphic.fill(shape);
            }
        }

        graphic.draw(shape);
    }

    /**
     *
     * @param point
     * @param style
     * @throws IOException
     */
    private void portrayPoint(Point point, Style style) throws IOException {
        this.portrayCommonAbstractGeometry(point);

        // MathTransform
        MathTransform transform;
        try {
            transform = context2d.getMathTransform(DefaultGeographicCRS.WGS84, context2d.getDisplayCRS());
        } catch (FactoryException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        }

        context2d.switchToDisplayCRS();
        Graphics2D graphic = context2d.getGraphics();

        // Apply styles
        IconStyle iconStyle = style.getIconStyle();
        if (iconStyle != null){
            graphic.setColor(iconStyle.getColor());
            BasicLink icon = iconStyle.getIcon();
            File img = new File(icon.getHref());
            BufferedImage image = ImageIO.read(img);
            com.vividsolutions.jts.geom.Point p = (com.vividsolutions.jts.geom.Point) point;
            double[] tab = new double[]{p.getX(), p.getY()};
            try {
                transform.transform(tab, 0, tab, 0, 1);
            } catch (TransformException ex) {
                context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                return;
            }
            graphic.drawImage(image, (int) tab[0]+image.getWidth()/2,
                    (int) tab[1]+image.getHeight()/2, null);
        }
    }

    /**
     *
     * @param linearRing
     * @param style
     */
    private void portrayLinearRing(LinearRing linearRing, Style style) {
        this.portrayCommonAbstractGeometry(linearRing);

        // MathTransform
        MathTransform transform = null;
        context2d.switchToDisplayCRS();
        Graphics2D graphic = context2d.getGraphics();
        com.vividsolutions.jts.geom.Geometry lr = null;

        try {
            transform = context2d.getMathTransform(DefaultGeographicCRS.WGS84, context2d.getDisplayCRS());    
            lr = JTS.transform((com.vividsolutions.jts.geom.LinearRing) linearRing, transform);
        } catch (MismatchedDimensionException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        } catch (TransformException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        } catch (FactoryException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        }

        LineStyle lineStyle = style.getLineStyle();
        if(lineStyle != null){
            graphic.setColor(lineStyle.getColor());
            graphic.setStroke(new BasicStroke((float) (lineStyle.getWidth())));
        }

        Shape shape = new JTSGeometryJ2D(lr);
        graphic.draw(shape);
    }

    /**
     *
     * @param abstractContainer
     */
    private void portrayCommonAbstractContainer(Feature abstractContainer) {
        this.portrayCommonAbstractFeature(abstractContainer);
    }

    /**
     *
     * @param folder
     * @throws IOException
     */
    private void portrayFolder(Feature folder) throws IOException {
        Iterator i;
        this.portrayCommonAbstractContainer(folder);
        if(folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()) != null){
            i = folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();
            while(i.hasNext()){
                this.portrayAbstractFeature((Feature) ((Property) i.next()).getValue());
            }
        }
    }

    /**
     *
     * @param document
     * @throws IOException
     */
    private void portrayDocument(Feature document) throws IOException {
        Iterator i;
        this.portrayCommonAbstractContainer(document);
        if(document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()) != null){
            i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();
            while(i.hasNext()){
                this.portrayAbstractFeature((Feature) ((Property) i.next()).getValue());
            }
        }
    }

    /**
     *
     * @param abstractStyleSelector
     */
    private void portrayAbstractStyleSelector(AbstractStyleSelector abstractStyleSelector) {
        if (abstractStyleSelector instanceof Style){
            this.indexStyle((Style)abstractStyleSelector);
        } else if (abstractStyleSelector instanceof StyleMap){
            this.indexStyleMap((StyleMap)abstractStyleSelector);
        }
    }

    /**
     *
     * @param style
     */
    private void indexStyle(Style style) {
        if (style.getIdAttributes().getId() != null){
            this.styles.put("#"+style.getIdAttributes().getId(), style);
        }
    }

    /**
     *
     * @param styleMap
     */
    private void indexStyleMap(StyleMap styleMap) {
        if (styleMap.getIdAttributes().getId() != null){
            this.styleMaps.put("#"+styleMap.getIdAttributes().getId(), styleMap);
        }
//        for(Pair pair : styleMap.getPairs()){
//            pair.
//        }
    }

    /**
     *
     * @param abstractOverlay
     * @throws IOException
     */
    private void portrayAbstractOverlay(Feature abstractOverlay) throws IOException {
        if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY)){
            this.portrayGroundOverlay(abstractOverlay);
        } else if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_SCREEN_OVERLAY)){
            this.portrayScreenOverlay(abstractOverlay);
//        } else if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_PHOTO_OVERLAY)){
//            this.portrayPhotoOverlay(abstractOverlay);
        }
    }

    /**
     *
     * @param groundOverlay
     * @throws IOException
     */
    private void portrayGroundOverlay(Feature groundOverlay) throws IOException {
        this.portrayCommonAbstractOverlay(groundOverlay);
        context2d.switchToObjectiveCRS();

        // Display image
        File img = new File(
                ((Icon) groundOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_ICON.getName()).getValue())
                .getHref());
        BufferedImage image = ImageIO.read(img);
        Graphics2D graphic = context2d.getGraphics();
        LatLonBox latLonBox = (LatLonBox) groundOverlay.getProperty(KmlModelConstants.ATT_GROUND_OVERLAY_LAT_LON_BOX.getName()).getValue();
        int x = (int) latLonBox.getEast();
        int y = (int) latLonBox.getNorth();
        int width = (int) ((latLonBox.getEast() - latLonBox.getWest())*1000);
        int height = (int) ((latLonBox.getNorth() - latLonBox.getSouth())*1000);       
        graphic.drawImage(image, x, y, width, height, null);

        // Apply styles
        Style s = this.retrieveStyle(groundOverlay);
        portrayBalloonStyle(x+width, y+height, s, false);
    }

    /**
     *
     * @param abstractOverlay
     */
    private void portrayCommonAbstractOverlay(Feature abstractOverlay) {
        this.portrayCommonAbstractFeature(abstractOverlay);
    }

    /**
     *
     * @param screenOverlay
     * @throws IOException
     */
    private void portrayScreenOverlay(Feature screenOverlay) throws IOException {
        this.portrayCommonAbstractOverlay(screenOverlay);
        File img = new File(
                ((Icon) screenOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_ICON.getName()).getValue())
                .getHref());
        context2d.switchToDisplayCRS();

        BufferedImage image = ImageIO.read(img);
        Graphics2D graphic = context2d.getGraphics();
        Vec2 overlayXY = (Vec2) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_OVERLAYXY.getName()).getValue();
        Vec2 screenXY = (Vec2) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_SCREENXY.getName()).getValue();
        Vec2 size = (Vec2) screenOverlay.getProperty(KmlModelConstants.ATT_SCREEN_OVERLAY_SIZE.getName()).getValue();
       
        int width = (int) size.getX();
        int height = (int) size.getY();
        int x = (int) screenXY.getX();
        int y = context2d.getCanvasDisplayBounds().height - (int) screenXY.getY() - height;
        BufferedImage imageResult = image.getSubimage((int) overlayXY.getX(), (int) overlayXY.getY(),
                width, height);
        graphic.drawImage(imageResult, x, y, width, height, null);

        // Apply styles
        Style s = this.retrieveStyle(screenOverlay);
        portrayBalloonStyle(x+width, y, s, true);
    }

    /**
     *
     * @param x
     * @param y
     * @param style
     * @param font
     */
    private void portrayBalloonStyle(double x, double y, Style style, boolean fixedToScreen){
        
        // MathTransform
        MathTransform transform = null;
        if(!fixedToScreen){
            try {
                transform = context2d.getMathTransform(DefaultGeographicCRS.WGS84, context2d.getDisplayCRS());
            } catch (FactoryException ex) {
                context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                return;
            }
        }

        context2d.switchToDisplayCRS();
        Graphics2D graphic = context2d.getGraphics();

        graphic.setFont(FONT);
        FontMetrics fm = graphic.getFontMetrics();

        int linewidth = 40;
        BalloonStyle balloonStyle = style.getBalloonStyle();

        if(balloonStyle != null){
            int length = balloonStyle.getText().toString().length();
            int begin = 0, interligne = 0, balloonWidth = 0, balloonLines = 0;

            // set balloon width
            do{
                int end = Math.min(begin+linewidth, length);
                balloonWidth = Math.max(balloonWidth,
                        fm.stringWidth(balloonStyle.getText().toString().substring(begin, end)));
                begin+= linewidth;
                balloonLines++;
            }while (begin+linewidth < length);
            if(begin < length){
                balloonLines++;
            }

            // print balloon
            double[] tab = new double[]{x, y};

            if(!fixedToScreen){
                try {
                    transform.transform(tab, 0, tab, 0, 1);
                } catch (TransformException ex) {
                    context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                    return;
                }
            }
            Rectangle rectangle = new Rectangle((int)tab[0], (int) tab[1],
                    balloonWidth+2,
                    balloonLines*fm.getHeight()+2);
            graphic.setColor(balloonStyle.getBgColor());
            graphic.fill(rectangle);
            graphic.setColor(balloonStyle.getTextColor());

            // set balloonText
            begin = 0; interligne = fm.getHeight();
            tab = new double[]{x, y};
            if(!fixedToScreen){
                try {
                    transform.transform(tab, 0, tab, 0, 1);
                } catch (TransformException ex) {
                    context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                    return;
                }
            }
            while (begin+linewidth < length){
                graphic.drawString(
                        balloonStyle.getText().toString().substring(
                        begin, begin+linewidth),(int) tab[0]+1, (int) tab[1]+interligne);
                begin+= linewidth;
                interligne+= fm.getHeight();
            }
            if(begin < length){
                graphic.drawString(
                        balloonStyle.getText().toString().substring(
                        begin, length), (int) tab[0]+1, (int) tab[1]+interligne);
            }
        }
    }

    /**
     *
     * @param x
     * @param y
     * @param style
     * @param content
     */
    private void portrayLabelStyle(double x, double y, Style style, String content){
        // MathTransform
        MathTransform transform;
        try {
            transform = context2d.getMathTransform(DefaultGeographicCRS.WGS84, context2d.getDisplayCRS());
        } catch (FactoryException ex) {
            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        }
        Graphics2D graphic = context2d.getGraphics();
        LabelStyle labelStyle = style.getLabelStyle();
        if(labelStyle != null){
            graphic.setFont(new Font(FONT.getName(),
                    FONT.getStyle(), FONT.getSize() * (int) labelStyle.getScale()));
            graphic.setColor(labelStyle.getColor());
            double[] tab = new double[]{x, y};
            try {
                transform.transform(tab, 0, tab, 0, 1);
            } catch (TransformException ex) {
                context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                return;
            }
            graphic.drawString(content, (int) tab[0], (int) tab[1]);
        }
    }

    /**
     *
     * @param feature
     * @return
     */
    private Style retrieveStyle(Feature feature){
        final Style s;
        if(feature.getProperty(KmlModelConstants.ATT_STYLE_SELECTOR.getName())!= null
                && feature.getProperty(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).getValue() instanceof Style){
            s = (Style) feature.getProperty(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).getValue();
        } else {
            s = this.styles.get(((URI) feature.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue()).toString());
        }
        return s;
    }

}
