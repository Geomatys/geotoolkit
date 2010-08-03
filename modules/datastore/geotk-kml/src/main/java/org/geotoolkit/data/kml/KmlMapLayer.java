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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
import org.geotoolkit.data.kml.model.LatLonBox;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LineStyle;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.Model;
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
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.map.DynamicMapLayer;
import org.geotoolkit.style.MutableStyle;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class KmlMapLayer extends AbstractMapLayer implements DynamicMapLayer {

    private Kml kml;
    private RenderingContext2D context2d;
    private Map<String,Style> styles = new HashMap<String, Style>();
    private static final Font FONT = new Font("Fonte coco", Font.ROMAN_BASELINE, 1);
    //private static final FontMetrics FONT_METRICS = new X11FontMetrics(FONT);

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

    private void portrayKml(Kml kml) throws IOException{
        if (kml.getAbstractFeature() != null){
            this.portrayAbstractFeature(kml.getAbstractFeature());
        }
//        this.portrayStandardExtensionLevel(
//                kml.extensions(),
//                Names.KML);
    }

    private void portrayAbstractFeature(Feature abstractFeature) throws IOException{
        if (FeatureTypeUtilities.isDecendedFrom(abstractFeature.getType(), KmlModelConstants.TYPE_CONTAINER)){
            this.portrayAbstractContainer(abstractFeature);
        } else if (FeatureTypeUtilities.isDecendedFrom(abstractFeature.getType(), KmlModelConstants.TYPE_OVERLAY)){
            this.portrayAbstractOverlay(abstractFeature);
        } else if (abstractFeature.getType().equals(KmlModelConstants.TYPE_PLACEMARK)){
            this.portrayPlacemark(abstractFeature);
        }
//        else {
//            for(StaxStreamWriter candidate : this.extensionWriters){
//                if(((KmlExtensionWriter) candidate).canHandleComplex(URI_KML,null, abstractFeature)){
//                    ((KmlExtensionWriter) candidate).writeComplexExtensionElement(URI_KML, null, abstractFeature);
//                }
//            }
//        }
    }

    private void portrayAbstractContainer(Feature abstractContainer) throws IOException {
        if (abstractContainer.getType().equals(KmlModelConstants.TYPE_FOLDER)){
            this.portrayFolder(abstractContainer);
        } else if (abstractContainer.getType().equals(KmlModelConstants.TYPE_DOCUMENT)){
            this.portrayDocument(abstractContainer);
        }
    }

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
            portrayBalloonStyle(
                    (int) region.getLatLonAltBox().getEast(),
                    (int) region.getLatLonAltBox().getNorth(),
                    s);
            portrayLabelStyle(
                    (int) region.getLatLonAltBox().getEast(),
                    (int) region.getLatLonAltBox().getNorth(),
                    s,
                    (String) placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        }

//        this.writeStandardExtensionLevel(
//                (Extensions) placemark.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
//                Names.PLACEMARK);
    }

    private void portrayCommonAbstractFeature(Feature abstractFeature){
        Iterator i;
        if (abstractFeature.getProperty(KmlModelConstants.ATT_STYLE_SELECTOR.getName()) != null){
            i = abstractFeature.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();
            while(i.hasNext()){
                this.portrayAbstractStyleSelector((AbstractStyleSelector) ((Property) i.next()).getValue());
            }
        }

//        this.writeStandardExtensionLevel(
//                (Extensions) abstractFeature.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
//                Names.FEATURE);
    }

    private void portrayAbstractGeometry(AbstractGeometry abstractGeometry, Style style) throws IOException{
        if (abstractGeometry instanceof MultiGeometry){
            this.portrayMultiGeometry((MultiGeometry) abstractGeometry, style);
        } else if (abstractGeometry instanceof LineString){
            this.portrayLineString((LineString) abstractGeometry, style);
        }
        else if (abstractGeometry instanceof Polygon){
            this.portrayPolygon((Polygon) abstractGeometry, style);
        } else if (abstractGeometry instanceof Point){
            this.portrayPoint((Point) abstractGeometry, style);
        } else if (abstractGeometry instanceof LinearRing){
            this.portrayLinearRing((LinearRing) abstractGeometry, style);
        } else if (abstractGeometry instanceof Model){
            this.portrayModel((Model) abstractGeometry);
//        } else {
//            for(StaxStreamWriter candidate : this.extensionWriters){
//                if(((KmlExtensionWriter) candidate).canHandleComplex(URI_KML, null, abstractGeometry)){
//                    ((KmlExtensionWriter) candidate).writeComplexExtensionElement(URI_KML, null, abstractGeometry);
//                }
//            }
        }
    }

    public void portrayCommonAbstractObject(AbstractObject abstractObject){

//        this.writeSimpleExtensionsScheduler(Names.OBJECT,
//                abstractObject.extensions().simples(Names.OBJECT));
    }

    public void portrayCommonAbstractGeometry(AbstractGeometry abstractGeometry){

        this.portrayCommonAbstractObject(abstractGeometry);
//        this.writeStandardExtensionLevel(
//                abstractGeometry.extensions(),
//                Names.GEOMETRY);
    }

    private void portrayLineString(LineString lineString, Style style){
        this.portrayCommonAbstractGeometry(lineString);

        context2d.switchToObjectiveCRS();
        Graphics2D graphic = context2d.getGraphics();

        // Apply styles
        LineStyle lineStyle = style.getLineStyle();
        if(lineStyle != null){
            graphic.setColor(style.getLineStyle().getColor());
            graphic.setStroke(new BasicStroke((float) (style.getLineStyle().getWidth() * 0.01)));
        }

        Shape shape = new JTSGeometryJ2D((com.vividsolutions.jts.geom.Geometry) lineString);
        graphic.draw(shape);

//        this.writeStandardExtensionLevel(
//                lineString.extensions(),
//                Names.LINE_STRING);
    }

    private void portrayMultiGeometry(MultiGeometry multiGeometry, Style style) throws IOException {
        this.portrayCommonAbstractGeometry(multiGeometry);
        for (AbstractGeometry abstractGeometry : multiGeometry.getGeometries()){
            this.portrayAbstractGeometry(abstractGeometry, style);
        }
//        this.writeStandardExtensionLevel(
//                multiGeometry.extensions(),
//                Names.MULTI_GEOMETRY);
    }

    private void portrayPolygon(Polygon polygon, Style style) {
        this.portrayCommonAbstractGeometry(polygon);

        context2d.switchToObjectiveCRS();
        Graphics2D graphic = context2d.getGraphics();

        Shape shape = new JTSGeometryJ2D((com.vividsolutions.jts.geom.Geometry) polygon);

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
//        this.writeStandardExtensionLevel(
//                polygon.extensions(),
//                Names.POLYGON);
    }

    private void portrayPoint(Point point, Style style) throws IOException {
        this.portrayCommonAbstractGeometry(point);

        context2d.switchToObjectiveCRS();
        Graphics2D graphic = context2d.getGraphics();
        graphic.setColor(Color.BLACK);
        graphic.setStroke(new BasicStroke((float) 1));

        // Apply styles
        IconStyle iconStyle = style.getIconStyle();
        if (iconStyle != null){
            graphic.setColor(iconStyle.getColor());
            BasicLink icon = iconStyle.getIcon();
            File img = new File(icon.getHref());
            BufferedImage image = ImageIO.read(img);
            graphic.drawImage(image,(int) point.getCoordinateSequence().getCoordinate(0).x,
                    (int) point.getCoordinateSequence().getCoordinate(0).y, null);
        }

        // Display image

        Shape shape = new JTSGeometryJ2D((com.vividsolutions.jts.geom.Geometry) point);
        graphic.fill(shape);

//        this.writeStandardExtensionLevel(
//                point.extensions(),
//                Names.POINT);
    }

    private void portrayLinearRing(LinearRing linearRing, Style style) {
        this.portrayCommonAbstractGeometry(linearRing);

        context2d.switchToObjectiveCRS();
        Graphics2D graphic = context2d.getGraphics();
        Shape shape = new JTSGeometryJ2D((com.vividsolutions.jts.geom.Geometry) linearRing);

        LineStyle lineStyle = style.getLineStyle();
        if(lineStyle != null){
            graphic.setColor(lineStyle.getColor());
            graphic.setStroke(new BasicStroke((float) (lineStyle.getWidth() * 0.01)));
        }

        graphic.draw(shape);
        
//        this.writeStandardExtensionLevel(
//                linearRing.extensions(),
//                Names.LINEAR_RING);
    }

    private void portrayModel(Model model) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void portrayCommonAbstractContainer(Feature abstractContainer) {
        this.portrayCommonAbstractFeature(abstractContainer);
//        this.writeStandardExtensionLevel(
//                (Extensions) abstractContainer.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
//                Names.CONTAINER);
    }

    private void portrayFolder(Feature folder) throws IOException {
        Iterator i;
        this.portrayCommonAbstractContainer(folder);
        if(folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()) != null){
            i = folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();
            while(i.hasNext()){
                this.portrayAbstractFeature((Feature) ((Property) i.next()).getValue());
            }
        }
//        this.writeStandardExtensionLevel(
//                (Extensions) folder.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
//                Names.FOLDER);
    }

    private void portrayDocument(Feature document) throws IOException {
        Iterator i;
        this.portrayCommonAbstractContainer(document);
        if(document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()) != null){
            i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();
            while(i.hasNext()){
                this.portrayAbstractFeature((Feature) ((Property) i.next()).getValue());
            }
        }
//        this.writeStandardExtensionLevel(
//                (Extensions) document.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
//                Names.DOCUMENT);
    }

    private void portrayAbstractStyleSelector(AbstractStyleSelector abstractStyleSelector) {
        if (abstractStyleSelector instanceof Style){
            this.portrayStyle((Style)abstractStyleSelector);
        } else if (abstractStyleSelector instanceof StyleMap){
            this.portrayStyleMap((StyleMap)abstractStyleSelector);
        }
    }

    private void portrayStyle(Style style) {
        if (style.getIdAttributes().getId() != null){
            this.styles.put("#"+style.getIdAttributes().getId(), style);
        }
//        this.writeStandardExtensionLevel(
//                style.extensions(),
//                Names.STYLE);
    }

    private void portrayStyleMap(StyleMap styleMap) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void portrayAbstractOverlay(Feature abstractOverlay) throws IOException {
        if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY)){
            this.portrayGroundOverlay(abstractOverlay);
        } else if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_SCREEN_OVERLAY)){
            this.portrayScreenOverlay(abstractOverlay);
//        } else if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_PHOTO_OVERLAY)){
//            this.portrayPhotoOverlay(abstractOverlay);
        }
    }

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
        portrayBalloonStyle(x+width, y+height,s);
        
//        this.writeStandardExtensionLevel(
//                (Extensions) groundOverlay.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
//                Names.GROUND_OVERLAY);
    }

    private void portrayCommonAbstractOverlay(Feature abstractOverlay) {
        this.portrayCommonAbstractFeature(abstractOverlay);
//        this.writeStandardExtensionLevel(
//                (Extensions) abstractOverlay.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue(),
//                Names.OVERLAY);
    }

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
        portrayBalloonStyle(x+width, y+height,s);
    }

    private void portrayBalloonStyle(int x, int y, Style style){

        Graphics2D graphic = context2d.getGraphics();
        int linewidth = 40, lineheight = 1;
        BalloonStyle balloonStyle = style.getBalloonStyle();
        if(balloonStyle != null){
                int length = balloonStyle.getText().toString().length();
                Rectangle rectangle = new Rectangle(x-1, y-2*lineheight,
                        3*length/linewidth+2,
                        (int) 1.5*lineheight*(length/linewidth)+4);
                graphic.setColor(balloonStyle.getBgColor());
                graphic.fill(rectangle);
                graphic.setColor(balloonStyle.getTextColor());
                graphic.setFont(new Font(FONT.getName(), FONT.getStyle(), 1));

            int begin = 0, interligne = 0;
            while (begin+linewidth < balloonStyle.getText().toString().length()){
                graphic.drawString(balloonStyle.getText().toString().substring(begin, begin+linewidth), x, y+interligne);
                begin+= linewidth;
                interligne+= lineheight;
            }
            if(begin < length){
                graphic.drawString(balloonStyle.getText().toString().substring(begin, length), x, y+interligne);
            }
        }
    }

    private void portrayLabelStyle(int x, int y, Style style, String content){

        Graphics2D graphic = context2d.getGraphics();
        LabelStyle labelStyle = style.getLabelStyle();
        if(labelStyle != null){
            graphic.setFont(new Font(graphic.getFont().getName(), graphic.getFont().getStyle(), (int) labelStyle.getScale()));
            graphic.setColor(labelStyle.getColor());
            graphic.drawString(content, x, y);
        }
    }

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
