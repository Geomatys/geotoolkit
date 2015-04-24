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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

import org.geotoolkit.coverage.grid.GridCoverage2D;
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
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.display2d.style.labeling.DefaultLabelLayer;
import org.geotoolkit.display2d.style.labeling.DefaultPointLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.Canvas;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.Feature;
import org.opengis.geometry.Envelope;
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
            Logging.getLogger("org.geotoolkit.data.kml.map").log(Level.SEVERE, "Error initializing KML graphic builder", ex);
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
        if (layer instanceof KmlMapLayer && canvas instanceof AbstractCanvas2D) {
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
        final List<Image> images = new ArrayList<>();

        try {
            images.add(legendFeature(kmllayer.kml.getAbstractFeature(),cache));
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


    private static Image legendFeature(Feature feature, KmlCache cache) throws IOException {
        Image image = null;
        if (KmlModelConstants.TYPE_CONTAINER.isAssignableFrom(feature.getType())) {
            image = legendContainer(feature,cache);
        } else if (KmlModelConstants.TYPE_OVERLAY.isAssignableFrom(feature.getType())) {
            image = legendOverlay(feature,cache);
        } else if (feature.getType().equals(KmlModelConstants.TYPE_PLACEMARK)) {
            image = legendPlacemark(feature,cache);
        } else if (feature.getType().equals(KmlModelConstants.TYPE_NETWORK_LINK)) {
            //return legendNetworkLink(feature);
        }
        return image;
    }

    private static Image legendContainer(Feature container, KmlCache cache) throws IOException {
        Image image = null;
        if (container.getType().equals(KmlModelConstants.TYPE_FOLDER)) {
            image = legendFolder(container,cache);
        } else if (container.getType().equals(KmlModelConstants.TYPE_DOCUMENT)) {
            image = legendDocument(container,cache);
        }
        return image;
    }

    private static Image legendPlacemark(Feature placemark, KmlCache cache) throws IOException {
        int nameWidth = 0;

        legendCommonFeature(placemark,cache);

        final String featureName = (String) placemark.getPropertyValue(KmlConstants.TAG_NAME);
        if (featureName != null) {
            nameWidth = FONT_METRICS.stringWidth(featureName);
        }

        // Apply styles
        final Style s = retrieveStyle(placemark,cache);
        final BufferedImage image = new BufferedImage(
                LEGEND_WIDTH_EXT + nameWidth,
                LEGEND_HEIGHT_EXT,
                BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphic = (Graphics2D) image.getGraphics();
        graphic.setFont(FONT);

        final AbstractGeometry geometry = (AbstractGeometry) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);

        if (s != null && s.getIconStyle() != null) {
            final IconStyle iconStyle = s.getIconStyle();
            final BasicLink bl = iconStyle.getIcon();
            if (bl != null) {
                if (bl.getHref() != null) {
                    final URL img = new URL(bl.getHref());
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

    private static void legendCommonFeature(Feature feature, KmlCache cache) {
        for (final Object value : (Iterable<?>) feature.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)) {
            indexStyleSelector((AbstractStyleSelector) value, cache);
        }
    }

    private static void legendCommonContainer(Feature container, KmlCache cache) {
        legendCommonFeature(container,cache);
    }

    private static Image legendFolder(Feature folder, KmlCache cache) throws IOException {
        int width = 0, height = ICON_FOLDER.getHeight(), y = ICON_FOLDER.getHeight();
        final List<Image> images = new ArrayList<>();
        int nameWidth = 0;

        legendCommonContainer(folder,cache);

        final String featureName = (String) folder.getPropertyValue(KmlConstants.TAG_NAME);
        if (featureName != null) {
            nameWidth = FONT_METRICS.stringWidth(featureName);
        }

        Iterator<?> i = ((Iterable<?>) folder.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        while (i.hasNext()) {
            images.add(legendFeature((Feature) i.next(), cache));
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

    private static Image legendDocument(Feature document, KmlCache cache) throws IOException {
        int width = 0, height = 0, y = 0;
        List<Image> images = new ArrayList<>();

        legendCommonContainer(document,cache);

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        while (i.hasNext()) {
            images.add(legendFeature((Feature) i.next(), cache));
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

    private static Image legendOverlay(Feature overlay, KmlCache cache) throws IOException {
        Image image = null;
        if (overlay.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY)) {
            image = legendGroundOverlay(overlay,cache);
        } else if (overlay.getType().equals(KmlModelConstants.TYPE_SCREEN_OVERLAY)) {
            image = legendScreenOverlay(overlay,cache);
//        } else if (overlay.getType().equals(KmlModelConstants.TYPE_PHOTO_OVERLAY)){
//            portrayPhotoOverlay(overlay);
        }
        return image;
    }

    private static Image legendGroundOverlay(Feature groundOverlay, KmlCache cache) throws IOException {
        int nameWidth = 0;

        final String featureName = (String) groundOverlay.getPropertyValue(KmlConstants.TAG_NAME);
        if (featureName != null) {
            nameWidth = FONT_METRICS.stringWidth(featureName);
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

    private static Image legendScreenOverlay(Feature screenOverlay, KmlCache cache) throws IOException {
        int nameWidth = 0;

        final String featureName = (String) screenOverlay.getPropertyValue(KmlConstants.TAG_NAME);
        if (featureName != null) {
            nameWidth = FONT_METRICS.stringWidth(featureName);
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
    private static Style retrieveStyle(Feature feature, KmlCache cache) {
        Style styleSelector = null;
        final Iterator<?> i = ((Iterable<?>) feature.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
        if (i.hasNext()) {
            final Object value = i.next();
            if (value instanceof Style) {
                styleSelector = (Style) value;
            } else if (value instanceof StyleMap) {
                styleSelector = retrieveStyle((StyleMap) styleSelector, StyleState.NORMAL,cache);
            }
        } else {
            final Object value = feature.getPropertyValue(KmlConstants.TAG_STYLE_URL);
            if (value != null) {
                final String uri = value.toString();
                styleSelector = cache.styles.get(uri);
                if (styleSelector == null) {
                    final StyleMap styleMap = cache.styleMaps.get(uri);
                    styleSelector = retrieveStyle(styleMap, StyleState.NORMAL,cache);
                }
            }
        }
        return styleSelector;
    }

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
    private static void indexStyleSelector(AbstractStyleSelector styleSelector, KmlCache cache) {
        if (styleSelector instanceof Style) {
            indexStyle((Style) styleSelector, cache);
        } else if (styleSelector instanceof StyleMap) {
            indexStyleMap((StyleMap) styleSelector, cache);
        }
    }

    private static void indexStyle(Style style, KmlCache cache) {
        if (style.getIdAttributes().getId() != null) {
            cache.styles.put("#" + style.getIdAttributes().getId(), style);
        }
    }

    private static void indexStyleMap(StyleMap styleMap, KmlCache cache) {
        if (styleMap.getIdAttributes().getId() != null) {
            cache.styleMaps.put("#" + styleMap.getIdAttributes().getId(), styleMap);
        }
    }

    private static class KmlCache{
        final Kml kml;
        final Map<String, Style> styles = new HashMap<>();
        final Map<String, StyleMap> styleMaps = new HashMap<>();

        KmlCache(Kml kml){
            this.kml = kml;
        }
    }

    private static class KMLGraphic extends GraphicJ2D {
        private final KmlCache cache;
        private RenderingContext2D context2d;

        private KMLGraphic(J2DCanvas canvas, KmlMapLayer layer) {
            super(canvas);
            cache = new KmlCache(layer.kml);
        }

        @Override
        public void paint(RenderingContext2D context2D) {
            this.context2d = context2D;
            cache.styles.clear();
            cache.styleMaps.clear();
            try {
                portrayKml(cache.kml);
                context2D.getLabelRenderer(true).portrayLabels();
            } catch (TransformException | IOException ex) {
                Logging.getLogger("org.geotoolkit.data.kml.map").log(Level.SEVERE, null, ex);
            }
            cache.styles.clear();
            cache.styleMaps.clear();
        }

        @Override
        public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
            return graphics;
        }

        private void portrayKml(Kml kml) throws IOException {
            if (kml.getAbstractFeature() != null) {
                portrayFeature(kml.getAbstractFeature());
            }
        }

        private void portrayFeature(Feature feature) throws IOException {
            if (KmlModelConstants.TYPE_CONTAINER.isAssignableFrom(feature.getType())) {
                portrayAbstractContainer(feature);
            } else if (KmlModelConstants.TYPE_OVERLAY.isAssignableFrom(feature.getType())) {
                portrayOverlay(feature);
            } else if (feature.getType().equals(KmlModelConstants.TYPE_PLACEMARK)) {
                portrayPlacemark(feature);
            }
        }

        private void portrayCommonFeature(Feature feature) {
            Iterator<?> i = ((Iterable<?>) feature.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
            while (i.hasNext()) {
                indexStyleSelector((AbstractStyleSelector) i.next(), cache);
            }
        }

        private void portrayPlacemark(Feature placemark) throws IOException {
            portrayCommonFeature(placemark);

            context2d.switchToObjectiveCRS();

            // Apply styles
            final Style s = retrieveStyle(placemark,cache);
            com.vividsolutions.jts.geom.Point centroid = null;

            // display geometries
            final AbstractGeometry geometry = (AbstractGeometry) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
            if (geometry != null) {
                portrayGeometry(geometry, s);
                if (geometry instanceof Geometry) {
                    centroid = ((Geometry) geometry).getCentroid();
                } else if (geometry instanceof MultiGeometry) {
                    centroid = ((MultiGeometry) geometry).getCentroid();
                }
            }

            double x = Double.NaN;
            double y = Double.NaN;

            if (centroid != null) {
                x = centroid.getX();
                y = centroid.getY();
            } else {
                final Region region = ((Region) placemark.getPropertyValue(KmlConstants.TAG_REGION));
                if (region != null) {
                    final LatLonAltBox latLonAltBox = region.getLatLonAltBox();
                    x = (latLonAltBox.getEast() + latLonAltBox.getWest()) / 2;
                    y = (latLonAltBox.getNorth() + latLonAltBox.getSouth()) / 2;
                }
            }

            if (x != Double.NaN && y != Double.NaN) {
                portrayBalloonStyle(x, y, s, false, placemark);
                portrayLabelStyle(x, y, s,
                        (String) placemark.getPropertyValue(KmlConstants.TAG_NAME),
                        centroid);
                if (false) {
                    portrayFlag(x, y); // portray flag at Placemark center (set off)
                }
            }
        }

        private void portrayAbstractContainer(Feature container) throws IOException {
            if (container.getType().equals(KmlModelConstants.TYPE_FOLDER)) {
                portrayFolder(container);
            } else if (container.getType().equals(KmlModelConstants.TYPE_DOCUMENT)) {
                portrayDocument(container);
            }
        }

        private void portrayCommonContainer(Feature container) {
            portrayCommonFeature(container);
        }

        private void portrayFolder(Feature folder) throws IOException {
            portrayCommonContainer(folder);
            Iterator<?> i = ((Iterable<?>) folder.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
            while (i.hasNext()) {
                portrayFeature((Feature) i.next());
            }
        }

        private void portrayDocument(Feature document) throws IOException {
            portrayCommonContainer(document);
            Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
            while (i.hasNext()) {
                portrayFeature((Feature) i.next());
            }
        }

        private void portrayOverlay(Feature overlay) throws IOException {
            if (overlay.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY)) {
                portrayGroundOverlay(overlay);
            } else if (overlay.getType().equals(KmlModelConstants.TYPE_SCREEN_OVERLAY)) {
                portrayScreenOverlay(overlay);
//        } else if (overlay.getType().equals(KmlModelConstants.TYPE_PHOTO_OVERLAY)){
//            portrayPhotoOverlay(overlay);
            }
        }

        private void portrayCommonOverlay(Feature overlay) {
            portrayCommonFeature(overlay);
        }

        private void portrayGroundOverlay(Feature groundOverlay) throws IOException {
            portrayCommonOverlay(groundOverlay);

            context2d.switchToDisplayCRS();
            final Graphics2D graphic = context2d.getGraphics();

            // Display image
            final Icon icon = (Icon) groundOverlay.getPropertyValue(KmlConstants.TAG_ICON);
            final URL iconURL = new URL(icon.getHref());
            final BufferedImage image = ImageIO.read(iconURL);
            final LatLonBox latLonBox = (LatLonBox) groundOverlay.getPropertyValue(KmlConstants.TAG_LAT_LON_BOX);

            final double north = latLonBox.getNorth();
            final double east = latLonBox.getEast();
            final double south = latLonBox.getSouth();
            final double west = latLonBox.getWest();

            final GeneralEnvelope envelope = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
            envelope.setRange(0, west, east);
            envelope.setRange(1, south, north);
            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setEnvelope(envelope);
            gcb.setRenderedImage(image);
            final GridCoverage2D coverage = gcb.getGridCoverage2D();
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

        private void portrayScreenOverlay(Feature screenOverlay) throws IOException {
            portrayCommonOverlay(screenOverlay);

            Icon icon = (Icon) screenOverlay.getPropertyValue(KmlConstants.TAG_ICON);
            final URL img = new URL(icon.getHref());
            context2d.switchToDisplayCRS();

            final BufferedImage image = ImageIO.read(img);
            final Graphics2D graphic = context2d.getGraphics();
            final Vec2 overlayXY = (Vec2) screenOverlay.getPropertyValue(KmlConstants.TAG_OVERLAY_XY);
            final Vec2 screenXY = (Vec2) screenOverlay.getPropertyValue(KmlConstants.TAG_SCREEN_XY);
            final Vec2 size = (Vec2) screenOverlay.getPropertyValue(KmlConstants.TAG_SIZE);

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

        private void portrayGeometry(AbstractGeometry geometry, Style style) throws IOException {
            if (geometry instanceof MultiGeometry) {
                portrayMultiGeometry((MultiGeometry) geometry, style);
            } else if (geometry instanceof LineString) {
                portrayLineString((LineString) geometry, style);
            } else if (geometry instanceof Polygon) {
                portrayPolygon((Polygon) geometry, style);
            } else if (geometry instanceof Point) {
                portrayPoint((Point) geometry, style);
            } else if (geometry instanceof LinearRing) {
                portrayLinearRing((LinearRing) geometry, style);
            }
        }

        private void portrayLineString(LineString lineString, Style style) {

            // MathTransform
            MathTransform transform = null;
            context2d.switchToDisplayCRS();
            final Graphics2D graphic = context2d.getGraphics();
            com.vividsolutions.jts.geom.Geometry ls = null;

            try {
                transform = context2d.getMathTransform(CommonCRS.WGS84.normalizedGeographic(), context2d.getDisplayCRS());
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

        private void portrayMultiGeometry(MultiGeometry multiGeometry, Style style) throws IOException {
            for (AbstractGeometry geometry : multiGeometry.getGeometries()) {
                portrayGeometry(geometry, style);
            }
        }

        private void portrayPolygon(Polygon polygon, Style style) {

            // MathTransform
            MathTransform transform = null;
            context2d.switchToDisplayCRS();
            final Graphics2D graphic = context2d.getGraphics();
            com.vividsolutions.jts.geom.Geometry pol = null;

            try {
                transform = context2d.getMathTransform(CommonCRS.WGS84.normalizedGeographic(), context2d.getDisplayCRS());
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

        private void portrayPoint(Point point, Style style) throws IOException {

            // MathTransform
            MathTransform transform;
            try {
                transform = context2d.getMathTransform(CommonCRS.WGS84.normalizedGeographic(), context2d.getDisplayCRS());
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
                    final URL img = new URL(icon.getHref());
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

        private void portrayLinearRing(LinearRing linearRing, Style style) {

            // MathTransform
            MathTransform transform = null;
            context2d.switchToDisplayCRS();
            final Graphics2D graphic = context2d.getGraphics();
            com.vividsolutions.jts.geom.Geometry lr = null;

            try {
                transform = context2d.getMathTransform(CommonCRS.WGS84.normalizedGeographic(), context2d.getDisplayCRS());
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

        private void portrayBalloonStyle(double x, double y, Style style,
                boolean fixedToScreen, Feature informationResource)
        {
            // MathTransform
            MathTransform transform = null;

            //Fixed to screen for ScrenOverlays
            if (!fixedToScreen) {
                try {
                    transform = context2d.getMathTransform(
                            CommonCRS.WGS84.normalizedGeographic(), context2d.getDisplayCRS());
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
                        String content = retrieveBalloonInformations(balloonStyle.getText().toString(), informationResource);
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

        private void portrayLabelStyle(double x, double y, Style style, String content, Geometry geom) {
            if (content == null) {
                return;
            }

            // MathTransform
            MathTransform transform;

            try {
                transform = context2d.getMathTransform(CommonCRS.WGS84.normalizedGeographic(), context2d.getDisplayCRS());
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

                    final StatelessContextParams params = new StatelessContextParams(null, null);
                    params.update(context2d);
                    final ProjectedGeometry projectedGeometry = new ProjectedGeometry(params);
                    projectedGeometry.setDataGeometry(geom, null);

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

        private void portrayFlag(double x, double y) throws IOException {
            MathTransform transform;
            final Graphics2D graphic = context2d.getGraphics();
            try {
                transform = context2d.getMathTransform(CommonCRS.WGS84.normalizedGeographic(), context2d.getDisplayCRS());
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

        private String retrieveBalloonInformations(String toInspect, Feature informationResource) {
            Object value = informationResource.getPropertyValue(KmlConstants.TAG_NAME);
            if (value != null) {
                toInspect = toInspect.replaceAll("\\$\\[" + KmlConstants.TAG_NAME + "\\]", value.toString());
            }
            value = informationResource.getPropertyValue(KmlConstants.TAG_DESCRIPTION);
            if (value != null) {
                toInspect = toInspect.replaceAll("\\$\\[" + KmlConstants.TAG_DESCRIPTION + "\\]", value.toString());
            }
            return toInspect;
        }

        @Override
        public Object getUserObject() {
            return null;
        }

        @Override
        public Envelope getEnvelope() {
            return null;
        }
    }
}
