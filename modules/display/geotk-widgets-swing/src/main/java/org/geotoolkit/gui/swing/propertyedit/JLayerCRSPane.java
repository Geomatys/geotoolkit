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
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.io.X364;
import org.apache.sis.io.wkt.Colors;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.resources.Vocabulary;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.io.wkt.Warnings;

/**
 * Display a WKT of the layer coordinate reference system.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JLayerCRSPane extends AbstractPropertyPane {

    private MapLayer layer = null;
    private CoordinateReferenceSystem crs = null;
    private final JEditorPane wktArea = new JEditorPane();

    public JLayerCRSPane(){
        super("CRS", null, null, "CRS");
        setLayout( new BorderLayout());
        wktArea.setEditable(false);
        wktArea.setContentType("text/html");
        wktArea.setBackground(Color.WHITE);
        add(BorderLayout.CENTER, new JScrollPane(wktArea));
    }

    @Override
    public void setTarget(final Object target) {
        layer = (MapLayer) target;
        init();
    }

    @Override
    public boolean canHandle(Object target) {
        return target instanceof MapLayer;
    }

    @Override
    public void apply() {
        //nothing to apply
    }

    private void init() {
        if (layer instanceof FeatureMapLayer) {
            crs = FeatureExt.getCRS( ((FeatureMapLayer)layer).getCollection().getFeatureType());
        }else if(layer instanceof CoverageMapLayer){
            final CoverageReference ref = ((CoverageMapLayer)layer).getCoverageReference();
            try{
                final GridCoverageReader reader = ref.acquireReader();
                final GeneralGridGeometry gg = reader.getGridGeometry(ref.getImageIndex());
                crs = gg.getCoordinateReferenceSystem();
                ref.recycle(reader);
            }catch(Exception ex){
                //we tryed
            }
        }

        // last chance, try using bounding box envelope
        if(crs == null && layer!=null){
            crs = layer.getBounds().getCoordinateReferenceSystem();
        }

        setIdentifiedObject(crs);
    }

    @Override
    public void reset() {
        init();
    }


    private void setIdentifiedObject(final IdentifiedObject item) {
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
        buffer.append("<html>");
        String text, warning;
        try {
            text = formatter.format(item);
            Warnings w = formatter.getWarnings();
            warning = (w != null) ? w.toString() : null;
        } catch (RuntimeException e) {
            text = String.valueOf((item!=null)?item.getName():"");
            warning = e.getLocalizedMessage();
        }
        if (warning != null) {
            buffer.append("<p><b>").append(Vocabulary.format(Vocabulary.Keys.Warning))
                    .append(":</b> ").append(warning).append("</p><hr>\n");
        }
        buffer.append("<pre>");
        // '\u001A' is the SUBSTITUTE character. We use it as a temporary replacement for avoiding
        // confusion between WKT quotes and HTML quotes while we search for text to make italic.
        makeItalic(X364.toHTML(text.replace('"', '\u001A')), buffer, '\u001A');
        wktArea.setText(buffer.append("</pre></html>").toString());

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
