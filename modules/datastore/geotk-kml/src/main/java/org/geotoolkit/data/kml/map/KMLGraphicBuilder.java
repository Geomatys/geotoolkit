/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.map;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.data.kml.model.AbstractGeometry;
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
import org.geotoolkit.data.kml.model.Pair;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.Polygon;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.model.StyleMap;
import org.geotoolkit.data.kml.model.StyleState;
import org.geotoolkit.data.kml.model.Vec2;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xsd.Cdata;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.geotoolkit.display2d.primitive.DefaultProjectedGeometry;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.display2d.style.labeling.DefaultLabelLayer;
import org.geotoolkit.display2d.style.labeling.DefaultPointLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.util.logging.Logging;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.primitive.Graphic;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Render KML layer in default geotoolkit rendering engine.
 *
 * @author Samuel Andrés
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class KMLGraphicBuilder implements GraphicBuilder<GraphicJ2D> {

    private static final int LEGEND_OFFSET = 10;
    private static final int LEGEND_HEIGHT_EXT = 30;
    private static final int LEGEND_WIDTH_EXT = 30;
    private static final int LEGEND_HEIGHT_INT = 22;
    private static final int LEGEND_WIDTH_INT = 22;
    private static BufferedImage ICON_FOLDER;
    private static BufferedImage ICON_PLACEMARK;
    private static BufferedImage ICON_PLACEMARK_LINE_STRING;
    private static BufferedImage ICON_PLACEMARK_LINEAR_RING;
    private static BufferedImage ICON_PLACEMARK_POLYGON;
    private static BufferedImage ICON_OVERLAY;
    private static final Font FONT = new Font("KmlMapLayerFont", Font.ROMAN_BASELINE, 12);
    private static FontMetrics FONT_METRICS;
    /**
     * One instance for all KML map layers. Object is concurrent.
     */
    static final KMLGraphicBuilder INSTANCE;

    static {
        KMLGraphicBuilder builder = null;
        try {
            builder = new KMLGraphicBuilder();
        } catch (IOException ex) {
            Logging.getLogger(KMLGraphicBuilder.class).log(Level.SEVERE, "Error initializing KML graphic builder", ex);
        }
        INSTANCE = builder;
    }

    private KMLGraphicBuilder() throws IOException {
        ICON_FOLDER = ImageIO.read(KmlMapLayer.class.getResourceAsStream("folder.png"));
        ICON_PLACEMARK = ImageIO.read(KmlMapLayer.class.getResourceAsStream("flag.png"));
        ICON_PLACEMARK_LINE_STRING = ImageIO.read(KmlMapLayer.class.getResourceAsStream("lineString.png"));
        ICON_PLACEMARK_LINEAR_RING = ImageIO.read(KmlMapLayer.class.getResourceAsStream("linearRing.png"));
        ICON_PLACEMARK_POLYGON = ImageIO.read(KmlMapLayer.class.getResourceAsStream("polygon.png"));
        ICON_OVERLAY = ImageIO.read(KmlMapLayer.class.getResourceAsStream("overlay.png"));

        // Font metrics initialization.
        final Graphics2D g = (Graphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
        g.setFont(FONT);
        FONT_METRICS = g.getFontMetrics();
    }

    @Override
    public Collection<GraphicJ2D> createGraphics(MapLayer layer, Canvas canvas) {
        if (layer instanceof KmlMapLayer && canvas instanceof ReferencedCanvas2D) {
            return Collections.singleton((GraphicJ2D) new KMLGraphic((J2DCanvas) canvas, (KmlMapLayer) layer));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }

    @Override
    public Image getLegend(MapLayer layer) throws PortrayalException {
        final KmlMapLayer kmllayer = (KmlMapLayer) layer;
        final KmlCache cache = new KmlCache(kmllayer.kml);

        int width = 0, height = 0, y = 0;
        final List<Image> images = new ArrayList<Image>();

        try {
            images.add(legendAbstractFeature(kmllayer.kml.getAbstractFeature(),cache));
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        }

        for (Image img : images) {
            width = Math.max(width, img.getWidth(null));
            height += img.getHeight(null);
        }

        final BufferedImage image = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphic = (Graphics2D) image.getGraphics();

        for (Image img : images) {
            graphic.drawImage(img, 0, y, null);
            y += img.getHeight(null);
        }
        return image;
    }


    /**
     *
     * @param abstractFeature
     * @return
     * @throws IOException
     */
    private static Image legendAbstractFeature(Feature abstractFeature, KmlCache cache)
            throws IOException {

        Image image = null;
        if (FeatureTypeUtilities.isDecendedFrom(
                abstractFeature.getType(), KmlModelConstants.TYPE_CONTAINER)) {
            image = legendAbstractContainer(abstractFeature,cache);
        } else if (FeatureTypeUtilities.isDecendedFrom(
                abstractFeature.getType(), KmlModelConstants.TYPE_OVERLAY)) {
            image = legendAbstractOverlay(abstractFeature,cache);
        } else if (abstractFeature.getType().equals(KmlModelConstants.TYPE_PLACEMARK)) {
            image = legendPlacemark(abstractFeature,cache);
        } else if (abstractFeature.getType().equals(KmlModelConstants.TYPE_NETWORK_LINK)) {
            //return this.legendNetworkLink(abstractFeature);
        }
        return image;
    }

    /**
     *
     * @param abstractContainer
     * @return
     * @throws IOException
     */
    private static Image legendAbstractContainer(Feature abstractContainer, KmlCache cache)
            throws IOException {

        Image image = null;
        if (abstractContainer.getType().equals(KmlModelConstants.TYPE_FOLDER)) {
            image = legendFolder(abstractContainer,cache);
        } else if (abstractContainer.getType().equals(KmlModelConstants.TYPE_DOCUMENT)) {
            image = legendDocument(abstractContainer,cache);
        }
        return image;
    }

    /**
     *
     * @param placemark
     * @return
     * @throws IOException
     */
    private static Image legendPlacemark(Feature placemark, KmlCache cache)
            throws IOException {

        String featureName = null;
        int nameWidth = 0;

        legendCommonAbstractFeature(placemark,cache);

        if (placemark.getProperty(KmlModelConstants.ATT_NAME.getName()) != null) {
            featureName = (String) placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue();
            if (featureName != null) {
                nameWidth = FONT_METRICS.stringWidth(featureName);
            }
        }

        // Apply styles
        final Style s = retrieveStyle(placemark,cache);
        final BufferedImage image = new BufferedImage(
                LEGEND_WIDTH_EXT + nameWidth,
                LEGEND_HEIGHT_EXT,
                BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphic = (Graphics2D) image.getGraphics();
        graphic.setFont(FONT);

        final AbstractGeometry geometry =
                (AbstractGeometry) placemark.getProperty(
                KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();

        if (s != null && s.getIconStyle() != null) {
            final IconStyle iconStyle = s.getIconStyle();
            final BasicLink bl = iconStyle.getIcon();
            if (bl != null) {
                if (bl.getHref() != null) {
                    final File img = new File(bl.getHref());
                    final BufferedImage buff = ImageIO.read(img);
                    graphic.drawImage(buff, 0, 4, LEGEND_WIDTH_INT, LEGEND_HEIGHT_INT, null);
                }
            }
        } else if (geometry instanceof LineString) {
            graphic.drawImage(ICON_PLACEMARK_LINE_STRING, 0, 4, null);
        } else if (geometry instanceof LinearRing) {
            graphic.drawImage(ICON_PLACEMARK_LINEAR_RING, 0, 4, null);
        } else if (geometry instanceof Polygon) {
            graphic.drawImage(ICON_PLACEMARK_POLYGON, 0, 4, null);
        } else {
            graphic.drawImage(ICON_PLACEMARK, 0, 4, null);
        }


        if (featureName != null) {
            graphic.setColor(Color.BLACK);
            graphic.drawString(featureName, LEGEND_WIDTH_EXT, LEGEND_HEIGHT_INT);
        }

        return image;
    }

    /**
     *
     * @param abstractFeature
     */
    private static void legendCommonAbstractFeature(Feature abstractFeature, KmlCache cache) {
        Iterator i;
        if (abstractFeature.getProperty(KmlModelConstants.ATT_STYLE_SELECTOR.getName()) != null) {
            i = abstractFeature.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();
            while (i.hasNext()) {
                indexAbstractStyleSelector((AbstractStyleSelector) ((Property) i.next()).getValue(),cache);
            }
        }
    }

    /**
     *
     * @param abstractContainer
     */
    private static void legendCommonAbstractContainer(Feature abstractContainer, KmlCache cache) {
        legendCommonAbstractFeature(abstractContainer,cache);
    }

    /**
     *
     * @param folder
     * @return
     * @throws IOException
     */
    private static Image legendFolder(Feature folder, KmlCache cache)
            throws IOException {

        int width = 0, height = ICON_FOLDER.getHeight(), y = ICON_FOLDER.getHeight();
        final List<Image> images = new ArrayList<Image>();
        Iterator i;
        String featureName = null;
        int nameWidth = 0;

        legendCommonAbstractContainer(folder,cache);

        if (folder.getProperty(KmlModelConstants.ATT_NAME.getName()) != null) {
            featureName = (String) folder.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue();
            if (featureName != null) {
                nameWidth = FONT_METRICS.stringWidth(featureName);
            }
        }

        if (folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()) != null) {
            i = folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();
            while (i.hasNext()) {
                images.add(legendAbstractFeature((Feature) ((Property) i.next()).getValue(),cache));
            }
        }

        for (Image img : images) {
            width = Math.max(width, img.getWidth(null));
            height += img.getHeight(null);
        }
        width = Math.max(width + LEGEND_OFFSET, LEGEND_WIDTH_EXT + nameWidth);

        final BufferedImage image = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphic = (Graphics2D) image.getGraphics();
        graphic.drawImage(ICON_FOLDER, 0, 4, null);

        for (Image img : images) {
            graphic.drawImage(img, LEGEND_OFFSET, y, null);
            y += img.getHeight(null);
        }

        if (featureName != null) {
            graphic.setColor(Color.BLACK);
            graphic.drawString(featureName, LEGEND_WIDTH_EXT, LEGEND_HEIGHT_INT);
        }
        return image;
    }

    /**
     *
     * @param document
     * @return
     * @throws IOException
     */
    private static Image legendDocument(Feature document, KmlCache cache)
            throws IOException {

        int width = 0, height = 0, y = 0;
        List<Image> images = new ArrayList<Image>();
        Iterator i;

        legendCommonAbstractContainer(document,cache);

        if (document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()) != null) {
            i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();
            while (i.hasNext()) {
                images.add(legendAbstractFeature((Feature) ((Property) i.next()).getValue(),cache));
            }
        }

        for (Image img : images) {
            width = Math.max(width, img.getWidth(null));
            height += img.getHeight(null);
        }

        final BufferedImage image = new BufferedImage(width + LEGEND_OFFSET, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphic = (Graphics2D) image.getGraphics();

        for (Image img : images) {
            graphic.drawImage(img, LEGEND_OFFSET, y, null);
            y += img.getHeight(null);
        }
        return image;
    }

    /**
     *
     * @param abstractOverlay
     * @return
     * @throws IOException
     */
    private static Image legendAbstractOverlay(Feature abstractOverlay, KmlCache cache)
            throws IOException {

        Image image = null;
        if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY)) {
            image = legendGroundOverlay(abstractOverlay,cache);
        } else if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_SCREEN_OVERLAY)) {
            image = legendScreenOverlay(abstractOverlay,cache);
//        } else if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_PHOTO_OVERLAY)){
//            this.portrayPhotoOverlay(abstractOverlay);
        }
        return image;
    }

    /**
     *
     * @param groundOverlay
     * @return
     * @throws IOException
     */
    private static Image legendGroundOverlay(Feature groundOverlay, KmlCache cache)
            throws IOException {

        String featureName = null;
        int nameWidth = 0;

        if (groundOverlay.getProperty(KmlModelConstants.ATT_NAME.getName()) != null) {
            featureName = (String) groundOverlay.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue();
            if (featureName != null) {
                nameWidth = FONT_METRICS.stringWidth(featureName);
            }
        }

        final BufferedImage image = new BufferedImage(
                LEGEND_WIDTH_EXT + nameWidth,
                LEGEND_HEIGHT_EXT,
                BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphic = (Graphics2D) image.getGraphics();
        graphic.drawImage(ICON_OVERLAY, 0, 4, null);

        if (featureName != null) {
            graphic.setColor(Color.BLACK);
            graphic.drawString(featureName, LEGEND_WIDTH_EXT, LEGEND_HEIGHT_INT);
        }
        return image;
    }

    /**
     *
     * @param screenOverlay
     * @return
     * @throws IOException
     */
    private static Image legendScreenOverlay(Feature screenOverlay, KmlCache cache)
            throws IOException {

        String featureName = null;
        int nameWidth = 0;

        if (screenOverlay.getProperty(KmlModelConstants.ATT_NAME.getName()) != null) {
            featureName = (String) screenOverlay.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue();
            if (featureName != null) {
                nameWidth = FONT_METRICS.stringWidth(featureName);
            }
        }

        final BufferedImage image = new BufferedImage(
                LEGEND_WIDTH_EXT + nameWidth,
                LEGEND_HEIGHT_EXT,
                BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphic = (Graphics2D) image.getGraphics();
        graphic.drawImage(ICON_OVERLAY, 0, 4, null);

        if (featureName != null) {
            graphic.setColor(Color.BLACK);
            graphic.drawString(featureName, LEGEND_WIDTH_EXT, LEGEND_HEIGHT_INT);
        }
        return image;
    }

    /*
     * -------------------------------------------------------------------------
     * RETRIEVE STYLES METHODS
     * -------------------------------------------------------------------------
     */
    /**
     *
     * @param feature
     * @return
     */
    private static Style retrieveStyle(Feature feature, KmlCache cache) {

        Style styleSelector = null;

        if (feature.getProperty(KmlModelConstants.ATT_STYLE_SELECTOR.getName()) != null) {
            if (feature.getProperty(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).getValue() instanceof Style) {
                styleSelector = (Style) feature.getProperty(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).getValue();
            } else if (styleSelector instanceof StyleMap) {
                final StyleMap styleMap = (StyleMap) styleSelector;
                styleSelector = retrieveStyle(styleMap, StyleState.NORMAL,cache);
            }
        } else if (feature.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()) != null) {
            if (feature.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue() != null) {
                styleSelector = cache.styles.get(((URI) feature.getProperty(
                        KmlModelConstants.ATT_STYLE_URL.getName()).getValue()).toString());
                if (styleSelector == null) {
                    final StyleMap styleMap = cache.styleMaps.get(((URI) feature.getProperty(
                            KmlModelConstants.ATT_STYLE_URL.getName()).getValue()).toString());
                    styleSelector = retrieveStyle(styleMap, StyleState.NORMAL,cache);
                }
            }
        }
        return styleSelector;
    }

    /**
     *
     * @param styleMap
     * @param styleState
     * @return
     */
    private static Style retrieveStyle(StyleMap styleMap, StyleState styleState, KmlCache cache) {
        Style s = null;
        if (styleMap != null) {
            for (Pair pair : styleMap.getPairs()) {
                if (styleState.equals(pair.getKey())) {
                    final AbstractStyleSelector styleSelector = pair.getAbstractStyleSelector();
                    if (styleSelector instanceof StyleMap) {
                        s = retrieveStyle((StyleMap) styleSelector, styleState,cache);
                    } else if (styleSelector != null) {
                        s = (Style) styleSelector;
                        break;
                    }

                    if (s == null) {
                        s = cache.styles.get(pair.getStyleUrl().toString());
                        if (s == null
                                && cache.styleMaps.get(pair.getStyleUrl().toString()) != null) {
                            s = retrieveStyle(cache.styleMaps.get(pair.getStyleUrl().toString()), styleState,cache);
                        }
                    }
                }
            }
        }
        return s;
    }

    /*
     * -------------------------------------------------------------------------
     * INDEX STYLES METHODS
     * -------------------------------------------------------------------------
     */
    /**
     *
     * @param abstractStyleSelector
     */
    private static void indexAbstractStyleSelector(AbstractStyleSelector abstractStyleSelector, KmlCache cache) {

        if (abstractStyleSelector instanceof Style) {
            indexStyle((Style) abstractStyleSelector, cache);
        } else if (abstractStyleSelector instanceof StyleMap) {
            indexStyleMap((StyleMap) abstractStyleSelector, cache);
        }
    }

    /**
     *
     * @param style
     */
    private static void indexStyle(Style style, KmlCache cache) {

        if (style.getIdAttributes().getId() != null) {
            cache.styles.put("#" + style.getIdAttributes().getId(), style);
        }
    }

    /**
     *
     * @param styleMap
     */
    private static void indexStyleMap(StyleMap styleMap, KmlCache cache) {

        if (styleMap.getIdAttributes().getId() != null) {
            cache.styleMaps.put("#" + styleMap.getIdAttributes().getId(), styleMap);
        }
    }


    private static class KmlCache{
        final Kml kml;
        final Map<String, Style> styles = new HashMap<String, Style>();
        final Map<String, StyleMap> styleMaps = new HashMap<String, StyleMap>();

        KmlCache(Kml kml){
            this.kml = kml;
        }
    }

    private static class KMLGraphic extends AbstractGraphicJ2D {

        private final KmlCache cache;
        private RenderingContext2D context2d;

        private KMLGraphic(J2DCanvas canvas, KmlMapLayer layer) {
            super(canvas, canvas.getObjectiveCRS());
            cache = new KmlCache(layer.kml);
        }

        @Override
        public void paint(RenderingContext2D context2D) {
            this.context2d = context2D;
            cache.styles.clear();
            cache.styleMaps.clear();

            try {
                this.portrayKml(cache.kml);
                context2D.getLabelRenderer(true).portrayLabels();
            } catch (TransformException ex) {
                Logger.getLogger(KmlMapLayer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(KmlMapLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            cache.styles.clear();
            cache.styleMaps.clear();
        }

        @Override
        public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
            return graphics;
        }

        /**
         *
         * @param kml
         * @throws IOException
         */
        private void portrayKml(Kml kml)
                throws IOException {

            if (kml.getAbstractFeature() != null) {
                this.portrayAbstractFeature(kml.getAbstractFeature());
            }
        }

        /**
         *
         * @param abstractFeature
         * @throws IOException
         */
        private void portrayAbstractFeature(Feature abstractFeature)
                throws IOException {

            if (FeatureTypeUtilities.isDecendedFrom(
                    abstractFeature.getType(), KmlModelConstants.TYPE_CONTAINER)) {
                this.portrayAbstractContainer(abstractFeature);
            } else if (FeatureTypeUtilities.isDecendedFrom(
                    abstractFeature.getType(), KmlModelConstants.TYPE_OVERLAY)) {
                this.portrayAbstractOverlay(abstractFeature);
            } else if (abstractFeature.getType().equals(KmlModelConstants.TYPE_PLACEMARK)) {
                this.portrayPlacemark(abstractFeature);
            }
        }

        /**
         *
         * @param abstractFeature
         */
        private void portrayCommonAbstractFeature(Feature abstractFeature) {

            Iterator i;
            if (abstractFeature.getProperty(
                    KmlModelConstants.ATT_STYLE_SELECTOR.getName()) != null) {
                i = abstractFeature.getProperties(
                        KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();
                while (i.hasNext()) {
                    indexAbstractStyleSelector(
                            (AbstractStyleSelector) ((Property) i.next()).getValue(),cache);
                }
            }
        }

        /**
         *
         * @param placemark
         * @throws IOException
         */
        private void portrayPlacemark(Feature placemark)
                throws IOException {

            this.portrayCommonAbstractFeature(placemark);

            context2d.switchToObjectiveCRS();

            // Apply styles
            final Style s = retrieveStyle(placemark,cache);
            com.vividsolutions.jts.geom.Point centroid = null;

            // display geometries
            if (placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()) != null) {
                final AbstractGeometry geometry = (AbstractGeometry) placemark.getProperty(
                        KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
                if (geometry != null) {
                    this.portrayAbstractGeometry(geometry, s);
                    if (geometry instanceof Geometry) {
                        centroid = ((Geometry) geometry).getCentroid();
                    } else if (geometry instanceof MultiGeometry) {
                        centroid = ((MultiGeometry) geometry).getCentroid();
                    }
                }
            }

            double x = Double.NaN;
            double y = Double.NaN;

            if (centroid != null) {
                x = centroid.getX();
                y = centroid.getY();
            } else {
                final Region region = ((Region) placemark.getProperty(KmlModelConstants.ATT_REGION.getName()).getValue());
                if (region != null) {
                    final LatLonAltBox latLonAltBox = region.getLatLonAltBox();
                    x = (latLonAltBox.getEast() + latLonAltBox.getWest()) / 2;
                    y = (latLonAltBox.getNorth() + latLonAltBox.getSouth()) / 2;
                }
            }

            if (x != Double.NaN && y != Double.NaN) {
                portrayBalloonStyle(x, y, s, false, placemark);
                portrayLabelStyle(x, y, s,
                        (String) placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue(),
                        centroid);
                if (false) {
                    portrayFlag(x, y); // portray flag at Placemark center (set off)
                }
            }

        }

        /**
         *
         * @param abstractContainer
         * @throws IOException
         */
        private void portrayAbstractContainer(Feature abstractContainer)
                throws IOException {

            if (abstractContainer.getType().equals(KmlModelConstants.TYPE_FOLDER)) {
                this.portrayFolder(abstractContainer);
            } else if (abstractContainer.getType().equals(KmlModelConstants.TYPE_DOCUMENT)) {
                this.portrayDocument(abstractContainer);
            }
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
        private void portrayFolder(Feature folder)
                throws IOException {

            Iterator i;
            this.portrayCommonAbstractContainer(folder);
            if (folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()) != null) {
                i = folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();
                while (i.hasNext()) {
                    this.portrayAbstractFeature((Feature) ((Property) i.next()).getValue());
                }
            }
        }

        /**
         *
         * @param document
         * @throws IOException
         */
        private void portrayDocument(Feature document)
                throws IOException {

            Iterator i;
            this.portrayCommonAbstractContainer(document);
            if (document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()) != null) {
                i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();
                while (i.hasNext()) {
                    this.portrayAbstractFeature((Feature) ((Property) i.next()).getValue());
                }
            }
        }

        /**
         *
         * @param abstractOverlay
         * @throws IOException
         */
        private void portrayAbstractOverlay(Feature abstractOverlay)
                throws IOException {

            if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY)) {
                this.portrayGroundOverlay(abstractOverlay);
            } else if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_SCREEN_OVERLAY)) {
                this.portrayScreenOverlay(abstractOverlay);
//        } else if (abstractOverlay.getType().equals(KmlModelConstants.TYPE_PHOTO_OVERLAY)){
//            this.portrayPhotoOverlay(abstractOverlay);
            }
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
         * @param groundOverlay
         * @throws IOException
         */
        private void portrayGroundOverlay(Feature groundOverlay)
                throws IOException {

            this.portrayCommonAbstractOverlay(groundOverlay);

            context2d.switchToDisplayCRS();
            final Graphics2D graphic = context2d.getGraphics();

            // Display image
            final BufferedImage image = ImageIO.read(new File(
                    ((Icon) groundOverlay.getProperty(
                    KmlModelConstants.ATT_OVERLAY_ICON.getName()).getValue()).getHref()));
            final LatLonBox latLonBox = (LatLonBox) groundOverlay.getProperty(
                    KmlModelConstants.ATT_GROUND_OVERLAY_LAT_LON_BOX.getName()).getValue();

            final double north = latLonBox.getNorth();
            final double east = latLonBox.getEast();
            final double south = latLonBox.getSouth();
            final double west = latLonBox.getWest();

            final GeneralEnvelope envelope = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
            envelope.setRange(0, west, east);
            envelope.setRange(1, south, north);
            final GridCoverage2D coverage = new GridCoverageFactory().create("cov", image, envelope);
            final GridCoverage2D resampled = (GridCoverage2D) Operations.DEFAULT.resample(coverage, context2d.getObjectiveCRS2D());

            context2d.switchToObjectiveCRS();

            final RenderedImage renderedImg = resampled.getRenderedImage();
            final MathTransform2D trs2D = resampled.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
            if (trs2D instanceof AffineTransform) {
                graphic.drawRenderedImage(renderedImg, (AffineTransform) trs2D);
            }

            // Apply styles
            final Style localStyle = retrieveStyle(groundOverlay,cache);
            portrayBalloonStyle((east + west) / 2, (north + south) / 2, localStyle, false, groundOverlay);
        }

        /**
         *
         * @param screenOverlay
         * @throws IOException
         */
        private void portrayScreenOverlay(Feature screenOverlay)
                throws IOException {

            this.portrayCommonAbstractOverlay(screenOverlay);

            final File img = new File(
                    ((Icon) screenOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_ICON.getName()).getValue()).getHref());
            context2d.switchToDisplayCRS();

            final BufferedImage image = ImageIO.read(img);
            final Graphics2D graphic = context2d.getGraphics();
            final Vec2 overlayXY = (Vec2) screenOverlay.getProperty(
                    KmlModelConstants.ATT_SCREEN_OVERLAY_OVERLAYXY.getName()).getValue();
            final Vec2 screenXY = (Vec2) screenOverlay.getProperty(
                    KmlModelConstants.ATT_SCREEN_OVERLAY_SCREENXY.getName()).getValue();
            final Vec2 size = (Vec2) screenOverlay.getProperty(
                    KmlModelConstants.ATT_SCREEN_OVERLAY_SIZE.getName()).getValue();

            final int width = (int) size.getX();
            final int height = (int) size.getY();
            final double coeffX = image.getWidth() / size.getX();
            final double coeffY = image.getHeight() / size.getY();
            final int x = (int) screenXY.getX() - (int) (overlayXY.getX() / coeffX);
            final int y = context2d.getCanvasDisplayBounds().height
                    - (int) screenXY.getY() - height - (int) (overlayXY.getY() / coeffY);

            graphic.drawImage(image, x, y, width, height, null);

            // Apply styles
            final Style localStyle = retrieveStyle(screenOverlay,cache);
            portrayBalloonStyle(x + width, y, localStyle, true, screenOverlay);
        }

        /**
         *
         * @param abstractGeometry
         * @param style
         * @throws IOException
         */
        private void portrayAbstractGeometry(AbstractGeometry abstractGeometry, Style style)
                throws IOException {

            if (abstractGeometry instanceof MultiGeometry) {
                this.portrayMultiGeometry((MultiGeometry) abstractGeometry, style);
            } else if (abstractGeometry instanceof LineString) {
                this.portrayLineString((LineString) abstractGeometry, style);
            } else if (abstractGeometry instanceof Polygon) {
                this.portrayPolygon((Polygon) abstractGeometry, style);
            } else if (abstractGeometry instanceof Point) {
                this.portrayPoint((Point) abstractGeometry, style);
            } else if (abstractGeometry instanceof LinearRing) {
                this.portrayLinearRing((LinearRing) abstractGeometry, style);
            }
        }

        /**
         *
         * @param lineString
         * @param style
         */
        private void portrayLineString(LineString lineString, Style style) {

            // MathTransform
            MathTransform transform = null;
            context2d.switchToDisplayCRS();
            final Graphics2D graphic = context2d.getGraphics();
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

            final LineStyle lineStyle = style.getLineStyle();
            if (lineStyle != null) {
                graphic.setColor(lineStyle.getColor());
                graphic.setStroke(new BasicStroke((float) (lineStyle.getWidth())));
            }

            final Shape shape = new JTSGeometryJ2D(ls);
            graphic.draw(shape);
        }

        /**
         *
         * @param multiGeometry
         * @param style
         * @throws IOException
         */
        private void portrayMultiGeometry(MultiGeometry multiGeometry, Style style)
                throws IOException {

            for (AbstractGeometry abstractGeometry : multiGeometry.getGeometries()) {
                this.portrayAbstractGeometry(abstractGeometry, style);
            }
        }

        /**
         *
         * @param polygon
         * @param style
         */
        private void portrayPolygon(Polygon polygon, Style style) {

            // MathTransform
            MathTransform transform = null;
            context2d.switchToDisplayCRS();
            final Graphics2D graphic = context2d.getGraphics();
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

            final Shape shape = new JTSGeometryJ2D(pol);

            // Apply styles
            if (style != null) {
                final PolyStyle polyStyle = style.getPolyStyle();
                if (polyStyle != null) {
                    graphic.setColor(polyStyle.getColor());
                    graphic.setStroke(new BasicStroke((float) 0.05));
                    if (style.getPolyStyle().getFill()) {
                        graphic.fill(shape);
                    }
                    if (polyStyle.getOutline() && style.getLineStyle() != null) {
                        final LineStyle lineStyle = style.getLineStyle();
                        graphic.setColor(lineStyle.getColor());
                        graphic.draw(shape);
                    }
                }
            }
        }

        /**
         *
         * @param point
         * @param style
         * @throws IOException
         */
        private void portrayPoint(Point point, Style style)
                throws IOException {

            // MathTransform
            MathTransform transform;
            try {
                transform = context2d.getMathTransform(DefaultGeographicCRS.WGS84, context2d.getDisplayCRS());
            } catch (FactoryException ex) {
                context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                return;
            }

            context2d.switchToDisplayCRS();
            final Graphics2D graphic = context2d.getGraphics();

            // Apply styles
            if (style != null) {
                final IconStyle iconStyle = style.getIconStyle();
                if (iconStyle != null) {
                    graphic.setColor(iconStyle.getColor());
                    final BasicLink icon = iconStyle.getIcon();
                    final File img = new File(icon.getHref());
                    final BufferedImage image = ImageIO.read(img);
                    com.vividsolutions.jts.geom.Point p = (com.vividsolutions.jts.geom.Point) point;
                    final double[] tab = new double[]{p.getX(), p.getY()};
                    try {
                        transform.transform(tab, 0, tab, 0, 1);
                    } catch (TransformException ex) {
                        context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                        return;
                    }
                    graphic.drawImage(image, (int) tab[0], (int) tab[1], null);
                }
            }
        }

        /**
         *
         * @param linearRing
         * @param style
         */
        private void portrayLinearRing(LinearRing linearRing, Style style) {

            // MathTransform
            MathTransform transform = null;
            context2d.switchToDisplayCRS();
            final Graphics2D graphic = context2d.getGraphics();
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

            if (style != null) {
                final LineStyle lineStyle = style.getLineStyle();
                if (lineStyle != null) {
                    graphic.setColor(lineStyle.getColor());
                    graphic.setStroke(new BasicStroke((float) (lineStyle.getWidth())));
                }
            }

            final Shape shape = new JTSGeometryJ2D(lr);
            graphic.draw(shape);
        }

        /**
         *
         * @param x
         * @param y
         * @param style
         * @param fixedToScreen
         * @param informationResource
         */
        private void portrayBalloonStyle(
                double x, double y, Style style,
                boolean fixedToScreen, Feature informationResource) {

            // MathTransform
            MathTransform transform = null;

            //Fixed to screen for ScrenOverlays
            if (!fixedToScreen) {
                try {
                    transform = context2d.getMathTransform(
                            DefaultGeographicCRS.WGS84, context2d.getDisplayCRS());
                } catch (FactoryException ex) {
                    context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                    return;
                }
            }

            context2d.switchToDisplayCRS();
            final Graphics2D graphic = context2d.getGraphics();

            final int linewidth = FONT_METRICS.getHeight();

            if (style != null) {
                final BalloonStyle balloonStyle = style.getBalloonStyle();
                if (balloonStyle != null) {
                    final int length = balloonStyle.getText().toString().length();
                    int begin = 0, interligne = 0, balloonWidth = 0, balloonLines = 0;
                    final boolean cdata = balloonStyle.getText() instanceof Cdata;
                    JLabel jep = null;

                    // balloon general dimensions
                    do {
                        final int end = Math.min(begin + linewidth, length);
                        balloonWidth = Math.max(balloonWidth,
                                FONT_METRICS.stringWidth(balloonStyle.getText().toString().substring(begin, end)));
                        begin += linewidth;
                        balloonLines++;
                    } while (begin + linewidth < length);

                    if (begin < length) {
                        balloonLines++;
                    }

                    final double[] tab = new double[]{x, y};

                    // Fixed to screen for ScrenOverlays
                    if (!fixedToScreen) {
                        try {
                            transform.transform(tab, 0, tab, 0, 1);
                        } catch (TransformException ex) {
                            context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                            return;
                        }
                    }

                    // acurate dimensions / position
                    int left, right, top, bottom, vExc, hExc, coeff;

                    if (cdata) {
                        String content = this.retrieveBalloonInformations(
                                balloonStyle.getText().toString(), informationResource);
                        if ("<html>".equals(content.substring(0, 5))) {
                            jep = new JLabel(content);
                        } else {
                            jep = new JLabel("<html>" + content + "</html>");
                        }
                        jep.setOpaque(false);
                        Dimension preferredDim = jep.getPreferredSize();
                        jep.setSize(preferredDim);
                        coeff = (preferredDim.height + preferredDim.width) / 4;
                        left = (int) tab[0] - (preferredDim.width + 2) / 2;
                        right = left + preferredDim.width + 1;
                        top = (int) tab[1] - (preferredDim.height + 2) - coeff;
                        bottom = (int) tab[1] - coeff;
                        right = left + preferredDim.width + 1;
                        vExc = (preferredDim.width + 2) / 10;
                        hExc = (preferredDim.height + 2) / 7;
                    } else {
                        coeff = ((balloonLines + 1) * FONT_METRICS.getHeight() + balloonWidth) / 4;
                        left = (int) tab[0] - (balloonWidth + 2) / 2;
                        right = (int) tab[0] + (balloonWidth + 2) / 2;
                        top = (int) tab[1] - ((balloonLines + 1) * FONT_METRICS.getHeight() + 2) - coeff;
                        bottom = (int) tab[1] - coeff;
                        vExc = (balloonWidth + 2) / 10;
                        hExc = (balloonLines * FONT_METRICS.getHeight() + 2) / 7;
                    }

                    // Print balloon structure
                    final Path2D p = new java.awt.geom.Path2D.Double();
                    p.moveTo(left, top);

                    // top and right sides
                    p.curveTo(left + (right - left) / 9, top - vExc,
                            left + 8 * (right - left) / 9, top - vExc, right, top);
                    p.curveTo(right + hExc, bottom + 5 * (top - bottom) / 6,
                            right + hExc, bottom + (top - bottom) / 6, right, bottom);

                    // bottom
                    p.curveTo(left + 8 * (right - left) / 9, bottom + vExc,
                            left + 7 * (right - left) / 9, bottom,
                            left + 2 * (right - left) / 3, bottom + vExc);
                    p.lineTo((int) tab[0], (int) tab[1]);
                    p.lineTo(left + (right - left) / 3, bottom + vExc);
                    p.curveTo(left + 2 * (right - left) / 9, bottom,
                            left + (right - left) / 9, bottom + vExc, left, bottom);

                    // left side
                    p.curveTo(left - hExc, bottom + (top - bottom) / 6, left - hExc,
                            bottom + 5 * (top - bottom) / 6, left, top);
                    p.closePath();

                    // balloon color
                    graphic.setColor(balloonStyle.getBgColor());
                    graphic.fill(p);
                    graphic.setColor(new Color(40, 40, 40));
                    graphic.draw(p);

                    // print balloon text
                    graphic.setColor(balloonStyle.getTextColor());

                    if (cdata) {
                        graphic.translate(left, top);
                        jep.paint(graphic);
                        graphic.translate(-left, -top);
                    } else {
                        begin = 0;
                        interligne = FONT_METRICS.getHeight();
                        while (begin + linewidth < length) {
                            graphic.drawString(
                                    balloonStyle.getText().toString().substring(
                                    begin, begin + linewidth), left + 1, top + interligne);
                            begin += linewidth;
                            interligne += FONT_METRICS.getHeight();
                        }
                        if (begin < length) {
                            graphic.drawString(
                                    balloonStyle.getText().toString().substring(
                                    begin, length), left + 1, top + interligne);
                        }
                    }
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
        private void portrayLabelStyle(double x, double y,
                Style style, String content, Geometry geom) {

            if (content == null) {
                return;
            }

            // MathTransform
            MathTransform transform;

            try {
                transform = context2d.getMathTransform(DefaultGeographicCRS.WGS84, context2d.getDisplayCRS());
            } catch (FactoryException ex) {
                context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                return;
            }

            if (style != null) {
                final LabelStyle labelStyle = style.getLabelStyle();
                if (labelStyle != null) {
                    //                graphic.setFont(new Font(FONT.getName(),
                    //                        FONT.getStyle(), FONT.getSize() * (int) labelStyle.getScale()));
                    //                graphic.setColor(labelStyle.getColor());
                    //                double[] tab = new double[]{x, y};
                    //                try {
                    //                    transform.transform(tab, 0, tab, 0, 1);
                    //                } catch (TransformException ex) {
                    //                    context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                    //                    return;
                    //                }

                    final DefaultProjectedGeometry projectedGeometry = new DefaultProjectedGeometry(geom);
                    projectedGeometry.setObjToDisplay(transform);
                    final LabelLayer labelLayer = new DefaultLabelLayer(false, false);
                    //                Paint textPaint;
                    //                textPaint.
                    final float displayFactor = 0.1f;//0.1f pour corriger l'erreur d'affichage (devrait être fixé normalement à 1)
                    labelLayer.labels().add(
                            new DefaultPointLabelDescriptor(content,
                            new Font(FONT.getName(),
                            FONT.getStyle(), FONT.getSize() * (int) labelStyle.getScale()),
                            labelStyle.getColor(), 0, null, 0.5f, 0.5f,
                            (float) x * displayFactor, (float) y * displayFactor,
                            0, context2d.getObjectiveCRS(), projectedGeometry));
                    context2d.getLabelRenderer(true).append(labelLayer);
                    //graphic.drawString(content, (int) tab[0], (int) tab[1]);
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
        private void portrayFlag(double x, double y)
                throws IOException {

            MathTransform transform;
            final Graphics2D graphic = context2d.getGraphics();

            try {
                transform = context2d.getMathTransform(DefaultGeographicCRS.WGS84, context2d.getDisplayCRS());
            } catch (FactoryException ex) {
                context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                return;
            }

            final BufferedImage image = ICON_PLACEMARK;
            double[] tab = new double[]{x, y};
            try {
                transform.transform(tab, 0, tab, 0, 1);
            } catch (TransformException ex) {
                context2d.getMonitor().exceptionOccured(ex, Level.WARNING);
                return;
            }
            graphic.drawImage(image, (int) tab[0], (int) tab[1] - LEGEND_HEIGHT_INT,
                    LEGEND_WIDTH_INT, LEGEND_HEIGHT_INT, null);
        }

        /**
         *
         * @param toInspect
         * @param informationResource
         * @return
         */
        private String retrieveBalloonInformations(String toInspect, Feature informationResource) {

            Property property = informationResource.getProperty(KmlModelConstants.ATT_NAME.getName());
            if (property != null && property.getValue() != null) {
                toInspect = toInspect.replaceAll("\\$\\[" + KmlConstants.TAG_NAME + "\\]",
                        property.getValue().toString());
            }

            property = informationResource.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName());
            if (property != null && property.getValue() != null) {
                toInspect = toInspect.replaceAll("\\$\\[" + KmlConstants.TAG_DESCRIPTION + "\\]",
                        property.getValue().toString());
            }
            return toInspect;
        }
        
    }
}
