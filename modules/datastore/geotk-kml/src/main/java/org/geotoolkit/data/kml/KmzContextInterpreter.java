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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Color;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.EnumAltitudeMode;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.LatLonBox;
import org.geotoolkit.data.kml.model.LineStyle;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.util.FileUtilities;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class KmzContextInterpreter {

    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));
    private static final File FILES_DIR = new File("/tmp/files");
    private static final KmlFactory KML_FACTORY = DefaultKmlFactory.getInstance();
    private static AtomicInteger increment = new AtomicInteger();
    private static final List<Entry<Rule, URI>> IDENTIFICATORS_MAP = new ArrayList<Entry<Rule, URI>>();

    public KmzContextInterpreter() {
        FILES_DIR.mkdir();
        FILES_DIR.deleteOnExit();
    }

    public void writeKmz(MapContext context, File kmzOutput)
            throws Exception {

        final Kml kml = KML_FACTORY.createKml();
        final Feature folder = KML_FACTORY.createFolder();
        final Collection<Property> folderProperties = folder.getProperties();
        kml.setAbstractFeature(folder);

        // Creating KML file
        final File docKml = new File("/tmp", "doc.kml");
        docKml.deleteOnExit();

        for (final MapLayer layer : context.layers()) {
            this.writeStyle(layer.getStyle(), folder);
            if (layer instanceof CoverageMapLayer) {
                folderProperties.add(
                        FF.createAttribute(
                        this.writeCoverageMapLayer((CoverageMapLayer) layer),
                        KmlModelConstants.ATT_FOLDER_FEATURES, null));
            } else if (layer instanceof FeatureMapLayer) {
                folderProperties.add(
                        FF.createAttribute(
                        this.writeFeatureMapLayer((FeatureMapLayer) layer),
                        KmlModelConstants.ATT_FOLDER_FEATURES, null));
            }
        }

        // Writing KML file
        final KmlWriter writer = new KmlWriter();
        writer.setOutput(docKml);
        writer.write(kml);
        writer.dispose();

        // Creating KMZ
        FileUtilities.zip(kmzOutput, ZipOutputStream.DEFLATED, 9, null, FILES_DIR, docKml);
    }

    /**
     * <p>Retrieves a style identificator.</p>
     *
     * @return
     */
    private String getIdentificator() {
        return "id" + increment.incrementAndGet();
    }

    //--------------------------------------------------------------------------
    // STYLES CONVERSION METHODS
    //--------------------------------------------------------------------------

    /**
     * <p>This method writes KML styles elements mapping a layer style.</p>
     *
     * <p style="color: red; font-weight: bold; font-style: italic;">BE CAREFUL :
     * SLD Styles reference thei associated features BUT KML specification is different
     * because each feature references its own style.</p>
     *
     * @param style
     * @param container
     * @return
     * @throws URISyntaxException
     */
    private Feature writeStyle(MutableStyle style, Feature container) 
            throws URISyntaxException {

        final List<MutableFeatureTypeStyle> featureTypeStyles = style.featureTypeStyles();
        for (int i = 0, num = featureTypeStyles.size(); i < num; i++) {
            container = this.writeFeatureTypeStyle(featureTypeStyles.get(i), container);
        }
        return container;
    }

    /**
     * <p>This method writes a feature type style.</p>
     *
     * @param featureTypeStyle
     * @param container
     * @return
     * @throws URISyntaxException
     */
    private Feature writeFeatureTypeStyle(
            MutableFeatureTypeStyle featureTypeStyle, Feature container)
            throws URISyntaxException {

        final Collection<Property> containerProperties = container.getProperties();
        final List<MutableRule> rules = featureTypeStyle.rules();

        for (int i = 0, num = rules.size(); i < num; i++) {
            containerProperties.add(
                    FF.createAttribute(
                    this.writeRule(rules.get(i)),
                    KmlModelConstants.ATT_STYLE_SELECTOR, null));
        }
        return container;
    }

    /**
     * <p>This method retrieves a KML StyleSelector element mapping SLD Rule.</p>
     *
     * @param rule
     * @return
     * @throws URISyntaxException
     */
    private AbstractStyleSelector writeRule(MutableRule rule)
            throws URISyntaxException {

        final Style styleSelector = KML_FACTORY.createStyle();
        final List<Symbolizer> symbolizers = rule.symbolizers();

        for (int i = 0, num = symbolizers.size(); i < num; i++) {
            this.writeSymbolizer(symbolizers.get(i), styleSelector);
        }

        // Links rule filter with Style URI
        final String id = this.getIdentificator();
        styleSelector.setIdAttributes(KML_FACTORY.createIdAttributes(id, null));
        IDENTIFICATORS_MAP.add(new SimpleEntry<Rule, URI>(rule, new URI("#"+id)));
        return styleSelector;
    }

    /**
     * <p>This method writes KML color styles mapping SLD Symbolizers.</p>
     *
     * <p>Color styles are writtent into KML Style selector.</p>
     *
     * @param symbolizer
     * @param styleSelector
     * @return
     */
    private AbstractStyleSelector writeSymbolizer(
            Symbolizer symbolizer, Style styleSelector) {

        if (symbolizer instanceof ExtensionSymbolizer) {
        }

        // LineSymbolizer mapping
        else if (symbolizer instanceof LineSymbolizer) {
            final LineSymbolizer lineSymbolizer = (LineSymbolizer) symbolizer;
            final LineStyle lineStyle = (LineStyle) ((styleSelector.getLineStyle() == null)
                    ? KML_FACTORY.createLineStyle() : styleSelector.getLineStyle());
            lineStyle.setWidth((Double) this.writeExpression(
                    lineSymbolizer.getStroke().getWidth(), Double.class, null));
            lineStyle.setColor((Color) this.writeExpression(
                    lineSymbolizer.getStroke().getColor(), Color.class, null));
            styleSelector.setLineStyle(lineStyle);
        }

        // PointSymbolizezr mapping
        else if (symbolizer instanceof PointSymbolizer) {
//            PointSymbolizer pointSymbolizer = (PointSymbolizer) symbolizer;
//            IconStyle iconStyle = KML_FACTORY.createIconStyle();
//            GraphicalSymbol gs = ((GraphicalSymbol) pointSymbolizer.getGraphic().graphicalSymbols().get(0));
//            gs.
        }

        // PolygonSymbolizer mapping
        else if (symbolizer instanceof PolygonSymbolizer) {
            final PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
            final PolyStyle polyStyle = KML_FACTORY.createPolyStyle();

            // Fill
            if(polygonSymbolizer.getFill() == null){
                polyStyle.setFill(false);
            } else {
                polyStyle.setFill(true);
                polyStyle.setColor((Color) this.writeExpression(
                        polygonSymbolizer.getFill().getColor(), Color.class, null));
            }

            // Outline
            if(polygonSymbolizer.getStroke() == null){
                polyStyle.setOutline(false);
            } else if(styleSelector.getLineStyle() == null) {
                polyStyle.setOutline(true);
                final LineStyle lineStyle = KML_FACTORY.createLineStyle();
                lineStyle.setColor((Color) this.writeExpression(
                        polygonSymbolizer.getStroke().getColor(), Color.class, null));
                lineStyle.setWidth((Double) this.writeExpression(
                        polygonSymbolizer.getStroke().getWidth(), Double.class, null));
                styleSelector.setLineStyle(lineStyle);
            }
            styleSelector.setPolyStyle(polyStyle);
        } else if (symbolizer instanceof RasterSymbolizer) {
        } else if (symbolizer instanceof TextSymbolizer) {
            final TextSymbolizer textSymbolizer = (TextSymbolizer) symbolizer;
            final LabelStyle labelStyle = KML_FACTORY.createLabelStyle();
            if(textSymbolizer.getFont() != null){
                textSymbolizer.getFont().getSize();
            }
            if(textSymbolizer.getFill() != null){
                labelStyle.setColor((Color) this.writeExpression(
                        textSymbolizer.getFill().getColor(), Color.class, null));
            }
            styleSelector.setLabelStyle(labelStyle);
        }
        return styleSelector;
    }

    /**
     * <p>Writes a static expression.</p>
     *
     * @param expression
     * @param type
     * @param object
     * @return
     */
    private Object writeExpression(Expression expression, Class type, Object object) {

        if (GO2Utilities.isStatic(Expression.NIL)) {
            return expression.evaluate(object, type);
        }
        return null;
    }

    //--------------------------------------------------------------------------
    // FEATURES TRANSFORMATIONS
    //--------------------------------------------------------------------------

    /**
     * <p>This method transforms a FeatureMapLAyer in KML Folder.</p>
     *
     * @param featureMapLayer
     * @return
     * @throws URISyntaxException
     */
    private Feature writeFeatureMapLayer(FeatureMapLayer featureMapLayer) 
            throws URISyntaxException {

        final Feature folder = KML_FACTORY.createFolder();
        final Collection<Property> folderProperties = folder.getProperties();
        for (final Feature f : featureMapLayer.getCollection()) {
            folderProperties.add(
                    FF.createAttribute(
                    writeFeature(f),
                    KmlModelConstants.ATT_FOLDER_FEATURES, null));
        }
        return folder;
    }

    /**
     * <p>This method transforms a feature into KML feature (Placemak if original
     * features contents a geometry, or Folder otherwise).</p>
     *
     * @param feature
     * @return
     * @throws URISyntaxException
     */
    private Feature writeFeature(Feature feature)
            throws URISyntaxException {

        final Collection<Property> featureProperties = feature.getProperties();
        Feature kmlFeature = null;
        Collection<Property> kmlFeatureProperties = null;

        for (final Property property : featureProperties) {
            final Object val = property.getValue();
            if (val instanceof Feature) {
                kmlFeature = KML_FACTORY.createFolder();
                kmlFeatureProperties = kmlFeature.getProperties();
                kmlFeatureProperties.add(
                        FF.createAttribute(
                        this.writeFeature((Feature) val),
                        KmlModelConstants.ATT_FOLDER_FEATURES, null));
            } else if (val instanceof Geometry) {
                kmlFeature = KML_FACTORY.createPlacemark();
                kmlFeatureProperties = kmlFeature.getProperties();
                kmlFeatureProperties.add(
                        FF.createAttribute(
                        this.writeGeometry((Geometry) val),
                        KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));
            } else {
                //System.out.println("PAS FEATURE.");
            }
        }

        // Search feature style URI
        for(Entry<Rule, URI> e : IDENTIFICATORS_MAP){
            final Rule rule = e.getKey();
            if(rule.getFilter().evaluate(feature)){
                kmlFeatureProperties.add(
                    FF.createAttribute(
                    e.getValue(),
                    KmlModelConstants.ATT_STYLE_URL, null));
                for(Symbolizer s : rule.symbolizers()){
                    if(s instanceof TextSymbolizer){
                        final TextSymbolizer t = (TextSymbolizer) s;
                        if(t.getLabel() != null){
                            kmlFeatureProperties.add(
                                    FF.createAttribute(
                                    this.writeExpression(t.getLabel(), String.class, feature),
                                    KmlModelConstants.ATT_NAME, null));
                        }
                    }
                }
                break;
            }
        }
        return kmlFeature;
    }

    /**
     * <p>This method transforms a JTS Geometry into KML Geometry.</p>
     *
     * @param geometry
     * @return
     */
    private AbstractGeometry writeGeometry(Geometry geometry) {
        
        final AbstractGeometry resultat;
        
        if (geometry instanceof GeometryCollection) {
            final List<AbstractGeometry> liste = new ArrayList<AbstractGeometry>();
            if (geometry instanceof MultiPolygon) {
                final MultiPolygon multipolygon = (MultiPolygon) geometry;
                for (int i = 0, num = multipolygon.getNumGeometries(); i < num; i++) {
                    liste.add(this.writeGeometry(multipolygon.getGeometryN(i)));
                }
            }
            resultat = KML_FACTORY.createMultiGeometry();
            ((org.geotoolkit.data.kml.model.MultiGeometry) resultat).setGeometries(liste);
        } else if (geometry instanceof Polygon) {
            final Polygon polygon = (Polygon) geometry;
            final Boundary externBound = KML_FACTORY.createBoundary(
                    (org.geotoolkit.data.kml.model.LinearRing) writeGeometry(polygon.getExteriorRing()), null, null);
            final List<Boundary> internBounds = new ArrayList<Boundary>();
            for (int i = 0, num = polygon.getNumInteriorRing(); i < num; i++) {
                internBounds.add(KML_FACTORY.createBoundary((org.geotoolkit.data.kml.model.LinearRing) this.writeGeometry(polygon.getInteriorRingN(i)), null, null));
            }
            resultat = KML_FACTORY.createPolygon(externBound, internBounds);
        } else if (geometry instanceof LineString) {
            if (geometry instanceof LinearRing) {
                resultat = KML_FACTORY.createLinearRing(((LinearRing) geometry).getCoordinateSequence());
            } else {
                resultat = KML_FACTORY.createLineString(((LineString) geometry).getCoordinateSequence());
            }
        } else {
            resultat = null;
        }
        return resultat;
    }

    /**
     * <p>This method transforms a CoverageMapLayer into KML GroundOverlay.</p>
     *
     * @param coverageMapLayer
     * @return
     * @throws Exception
     */
    private Feature writeCoverageMapLayer(CoverageMapLayer coverageMapLayer) 
            throws Exception {

        final Feature groundOverlay = KML_FACTORY.createGroundOverlay();
        final Collection<Property> groundOverlayProperties = groundOverlay.getProperties();
        final CoordinateReferenceSystem targetCrs = DefaultGeographicCRS.WGS84;

        final GridCoverage2D coverage =
                (GridCoverage2D) coverageMapLayer.getCoverageReader().read(0, null);
        //coverage.show();

        final GridCoverage2D targetCoverage =
                (GridCoverage2D) Operations.DEFAULT.resample(coverage, targetCrs);
        //targetCoverage.show();

        // Creating image file and Writting referenced image into.
        final File img = new File(FILES_DIR, targetCoverage.getName().toString());
        img.deleteOnExit();
        ImageIO.write(targetCoverage.getRenderedImage(), "png", img);

        final Icon image = KML_FACTORY.createIcon(KML_FACTORY.createLink());
        image.setHref(FILES_DIR.getName() + File.separator + targetCoverage.getName());
        groundOverlayProperties.add(FF.createAttribute(
                targetCoverage.getName().toString(), KmlModelConstants.ATT_NAME, null));
        groundOverlayProperties.add(FF.createAttribute(
                image, KmlModelConstants.ATT_OVERLAY_ICON, null));
        groundOverlay.getProperty(KmlModelConstants.
                ATT_GROUND_OVERLAY_ALTITUDE.getName()).setValue(1.0);
        groundOverlay.getProperty(KmlModelConstants.
                ATT_GROUND_OVERLAY_ALTITUDE_MODE.getName()).setValue(EnumAltitudeMode.CLAMP_TO_GROUND);

        final LatLonBox latLonBox = KML_FACTORY.createLatLonBox();
        latLonBox.setNorth(targetCoverage.getEnvelope2D().getMaxY());
        latLonBox.setSouth(targetCoverage.getEnvelope2D().getMinY());
        latLonBox.setEast(targetCoverage.getEnvelope2D().getMaxX());
        latLonBox.setWest(targetCoverage.getEnvelope2D().getMinX());

        groundOverlayProperties.add(FF.createAttribute(
                latLonBox, KmlModelConstants.ATT_GROUND_OVERLAY_LAT_LON_BOX, null));

        return groundOverlay;
    }
}
