/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.javafx.layer;

import java.awt.Color;
import java.util.List;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.measure.unit.Unit;
import org.apache.sis.io.wkt.Colors;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.io.X364;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.opengis.coverage.SampleDimensionType;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.content.AttributeGroup;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.SampleDimension;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLayerStructure extends FXPropertyPane {
    
    private static final String CSS;
    static {
        CSS = "<style type=\"text/css\">"
        + "body {padding:10px; width:250px; background-color:#ffffff; font-family:monospace;}\n"
        + "h1 {font-size:14px; font-weight:bold; text-align:left;}\n"
        + "table{margin-left: 15px;}\n"
        + "td {border-width: 1px; border-style:solid; border-color:black;text-align: center; padding:5px;}\n"
        + ".data {text-align:right;}\n"
        + "#error {color:red;}\n"
        + "</style>";
    }
    
    private final WebView webPane = new WebView();
    private MapLayer layer;

    public FXLayerStructure() {
        webPane.setPrefSize(600, 400);
        webPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Override
    public String getTitle() {
        return GeotkFX.getString(FXLayerStructure.class, "title");
    }
    
    public boolean canHandle(Object target) {
        return target instanceof CoverageMapLayer || target instanceof FeatureMapLayer;
    }

    public boolean init(Object target) {
        if(!(target instanceof CoverageMapLayer || target instanceof FeatureMapLayer)){
            return false;
        }
        
        this.layer = (MapLayer) target;
        final WebEngine webEngine = webPane.getEngine();
        webEngine.loadContent("<html></html>");

        final StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset=\"UTF-16\">");
        sb.append(CSS);
        sb.append("</head><body>");
        if(layer instanceof FeatureMapLayer){
            final FeatureMapLayer fml = (FeatureMapLayer) layer;
            final FeatureType type = fml.getCollection().getFeatureType();

            String str = type.toString().replace("&", "&amp;");
            str = str.replace("<", "&lt;");
            str = str.replace(">", "&gt;");

            sb.append("<pre>");
            sb.append(str);
            sb.append("</pre>");
            setCenter(webPane);


        }else if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final CoverageReference ref = cml.getCoverageReference();
            try {
                final GridCoverageReader reader = ref.acquireReader();
                final GeneralGridGeometry gridgeom = reader.getGridGeometry(0);
                final List<GridSampleDimension> dimensions = reader.getSampleDimensions(0);
                ref.recycle(reader);

                // GRID GEOMETRY PART //////////////////////////////////////////
                sb.append("<h1>").append("Grid geometry").append("</h1><br/>");

                final CoordinateReferenceSystem crs = gridgeom.getCoordinateReferenceSystem();
                final Envelope geoEnv = gridgeom.getEnvelope();
                final GridEnvelope gridEnv = gridgeom.getExtent();
                final MathTransform gridToCrs = gridgeom.getGridToCRS();

                final double[] coordGrid = new double[gridEnv.getDimension()];
                final double[] coordGeo = new double[gridEnv.getDimension()];

                sb.append("<table><tbody>");
                sb.append("<tr><td>");
                sb.append("Axis");
                sb.append("</td><td>");
                sb.append("Grid min");
                sb.append("</td><td>");
                sb.append("Grid max");
                sb.append("</td><td>");
                sb.append("Geo min");
                sb.append("</td><td>");
                sb.append("Geo max");
                sb.append("</td></tr>");
                for(int i=0;i<geoEnv.getDimension();i++){

                    //convert the grid coord to crs coord
                    for(int k=0;k<coordGrid.length;k++){
                        coordGrid[k] = gridEnv.getLow(k);
                    }
                    gridToCrs.transform(coordGrid, 0, coordGeo, 0, 1);
                    final double geoMin = coordGeo[i];
                    coordGrid[i] = gridEnv.getHigh(i);
                    gridToCrs.transform(coordGrid, 0, coordGeo, 0, 1);
                    final double geoMax = coordGeo[i];

                    final Unit unit = crs.getCoordinateSystem().getAxis(i).getUnit();
                    final String unitStr = (unit!=null) ? unit.toString() : "";
                    sb.append("<tr><td>");
                    sb.append(i);
                    sb.append("</td><td>");
                    sb.append(gridEnv.getLow(i));
                    sb.append("</td><td>");
                    sb.append(gridEnv.getHigh(i));
                    sb.append("</td><td>");
                    sb.append(geoMin).append(' ').append(unitStr);
                    sb.append("</td><td>");
                    sb.append(geoMax).append(' ').append(unitStr);
                    sb.append("</td></tr>");
                }
                sb.append("</tbody></table><br/>");
                sb.append("<b>Grid to CRS transform : <b><br/>");
                sb.append(formatWKT(gridToCrs));
                sb.append("<br/>");


                // SAMPLE DIMENSIONS PART //////////////////////////////////////
                sb.append("<h1>").append("Sample dimensions").append("</h1><br/>");



                if(dimensions!=null){
                    for(GridSampleDimension dim : dimensions){
                        final SampleDimensionType st = dim.getSampleDimensionType();
                        final MathTransform1D sampletoGeo = dim.getSampleToGeophysics();
                        final Unit unit = dim.getUnits();
                        final InternationalString desc = dim.getDescription();

                        sb.append("<b>").append(desc).append("</b><br/>");
                        sb.append("Unit : ").append(unit).append("<br/>");
                        sb.append("Sample to geophysic transform :<br/>");
                        sb.append(formatWKT(sampletoGeo));
                        sb.append("<br/>");

                        final List<Category> categories = dim.getCategories();
                        if(categories!=null && !categories.isEmpty()){
                            sb.append("<br/><table><tbody>");
                            sb.append("<tr><td>");
                            sb.append("Name");
                            sb.append("</td><td>");
                            sb.append("Range");
                            sb.append("</td><td>");
                            sb.append("Quantitative");
                            sb.append("</td></tr>");
                            for(Category cat : categories){
                                final InternationalString name = cat.getName();
                                final NumberRange range = cat.getRange();
                                final MathTransform1D trs = cat.getSampleToGeophysics();
                                final boolean isQuant = cat.isQuantitative();
                                final Color[] colors = cat.getColors();
                                sb.append("<tr><td>");
                                sb.append(name);
                                sb.append("</td><td>");
                                sb.append(range);
                                sb.append("</td><td>");
                                sb.append(isQuant);
                                sb.append("</td></tr>");
                            }
                            sb.append("</tbody></table><br/>");
                        }
                    }
                }

                //this imply ready the file, may be long, we have to calculate a reduced area
//                final GridCoverage coverage = reader.read(0, null);
//                final RenderedImage image = (RenderedImage) coverage.getRenderableImage(0, 0);
//                final SampleModel sm = image.getSampleModel();
//                sm.getNumBands();
//                sm.getDataType();
//                sm.getTransferType();
//                final ColorModel cm = image.getColorModel();

            } catch (Exception ex) {
                ex.printStackTrace();
            }




            final CoverageDescription desc = ref.getMetadata();
            if(desc!=null && !desc.getAttributeGroups().isEmpty()){
                final TabPane tabs = new TabPane();
                final Tab tabprops = new Tab("Propriétés");
                tabprops.setContent(webPane);
                tabs.getTabs().add(tabprops);

                final Tab tabbands = new Tab("Bands");
                tabs.getTabs().add(tabbands);

                final VBox vbox = new VBox();
                final ScrollPane scroll = new ScrollPane(vbox);
                scroll.setFitToWidth(true);
                scroll.setFitToHeight(true);
                scroll.setPrefSize(200, 200);
                tabbands.setContent(scroll);

                final AttributeGroup attg = desc.getAttributeGroups().iterator().next();
                for(RangeDimension rd : attg.getAttributes()){
                    if(rd instanceof SampleDimension){
                        final FXCoverageBand fxcb = new FXCoverageBand();
                        fxcb.init((SampleDimension) rd);
                        vbox.getChildren().add(fxcb);
                    }
                }

                setCenter(tabs);
            }else{
                setCenter(webPane);
            }

        }

        sb.append("</body></html>");
        webEngine.loadContent(sb.toString());
        return true;
    }

    private static String formatWKT(Object item){
        final WKTFormat formatter = new WKTFormat();
        formatter.setColors(Colors.DEFAULT);

        final StringBuilder buffer = new StringBuilder();
        /*
         * Set the Well Known Text (WKT) panel using the following steps:
         *
         *  1) Write the warning if there is one.
         *  2) Replace the X3.64 escape sequences by HTML colors.
         *  3) Turn quoted WKT names ("foo") in italic characters.
         */
        buffer.setLength(0);
        String text, warning;
        try {
            text = formatter.format(item);
            warning = formatter.getWarning();
        } catch (RuntimeException e) {
            text = String.valueOf((item instanceof IdentifiedObject)?((IdentifiedObject)item).getName():"");
            warning = e.getLocalizedMessage();
        }
        if (warning != null) {
            buffer.append("<p><b>").append(Vocabulary.format(Vocabulary.Keys.WARNING))
                    .append(":</b> ").append(warning).append("</p><hr>\n");
        }
        buffer.append("<pre>");
        // '\u001A' is the SUBSTITUTE character. We use it as a temporary replacement for avoiding
        // confusion between WKT quotes and HTML quotes while we search for text to make italic.
        makeItalic(X364.toHTML(text.replace('"', '\u001A')), buffer, '\u001A');
        return buffer.append("</pre>").toString();
    }

    /**
     * Copies the given text in the given buffer, while putting the quoted text in italic.
     * The quote character is given by the {@code quote} argument and will be replaced by
     * the usual {@code "} character.
     */
    static void makeItalic(final String text, final StringBuilder buffer, final char quote) {
        boolean isQuoting = false;
        int last = 0;
        for (int i=text.indexOf(quote); i>=0; i=text.indexOf(quote, last)) {
            buffer.append(text.substring(last, i)).append(isQuoting ? "</cite>\"" : "\"<cite>");
            isQuoting = !isQuoting;
            last = i+1;
        }
        buffer.append(text.substring(last));
    }
    
}
