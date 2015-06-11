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

package org.geotoolkit.gui.swing.propertyedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.logging.Level;
import javax.measure.unit.Unit;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.io.X364;
import org.apache.sis.io.wkt.Colors;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.resources.Vocabulary;
import org.opengis.coverage.SampleDimensionType;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JLayerDataStructurePanel extends AbstractPropertyPane {

    private final JTextPane textPane = new JTextPane();
    private MapLayer layer;

    public JLayerDataStructurePanel() {
        super(MessageBundle.getString("dataStructure"), null, null, null);
        setLayout(new BorderLayout());
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        textPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        //CSS
        final StyleSheet styles = new StyleSheet();
        styles.addRule("body {padding:10px; width:250px; background-color:#ffffff; font-family:Monospaced;}");
        styles.addRule("h1 {font-size:14px; font-weight:bold; text-align:left;}");
        styles.addRule("table {margin-left: 15px; }");
        styles.addRule("tr {border-width: 1px; border-style:solid; border-color:black;}");
        styles.addRule("td {border-width: 1px; text-align: center; padding:5px;}");
        styles.addRule(".data {text-align:right;}");
        styles.addRule("#error {color:red;}");
        textPane.setStyledDocument(new HTMLDocument(styles));

        add(BorderLayout.CENTER,new JScrollPane(textPane));
    }

    public boolean canHandle(Object target) {
        return target instanceof CoverageMapLayer || target instanceof FeatureMapLayer;
    }

    public void setTarget(Object target) {
        this.layer = (MapLayer) target;
        textPane.setText("");

        final StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        if(layer instanceof FeatureMapLayer){
            final FeatureMapLayer fml = (FeatureMapLayer) layer;
            final FeatureType type = fml.getCollection().getFeatureType();

            String str = type.toString().replace("\n", "<br>");
            str = str.replaceAll(" ", "&nbsp;");

            sb.append(str);


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
                Logging.getLogger(JLayerDataStructurePanel.class).log(Level.INFO, ex.getMessage(),ex);
            }
        }

        sb.append("</body></html>");
        textPane.setText(sb.toString());
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
        JLayerCRSPane.makeItalic(X364.toHTML(text.replace('"', '\u001A')), buffer, '\u001A');
        return buffer.append("</pre>").toString();
    }

    public void apply() {
        //nothing to apply
    }

    public void reset() {
        //nothing to reset
    }



}
