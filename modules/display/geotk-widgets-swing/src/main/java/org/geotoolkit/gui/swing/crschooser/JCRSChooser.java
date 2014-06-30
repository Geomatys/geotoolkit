/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2011, Johann Sorel
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
package org.geotoolkit.gui.swing.crschooser;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.painter.BackgroundPainter;
import org.geotoolkit.display2d.canvas.painter.BackgroundPainterGroup;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.display2d.ext.grid.DefaultGridTemplate;
import org.geotoolkit.display2d.ext.grid.GridPainter;
import org.geotoolkit.display2d.ext.grid.GridTemplate;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.JNavigationBar;
import org.geotoolkit.gui.swing.render2d.control.navigation.PanHandler;

import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.io.X364;
import org.apache.sis.io.wkt.Colors;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;

import org.opengis.util.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.Description;
import org.opengis.style.LineSymbolizer;

/**
 * CRSChooser component
 *
 * @author Johann Sorel
 * @module pending
 */
public class JCRSChooser extends javax.swing.JDialog {


    public static enum ACTION {
        APPROVE,
        CANCEL,
        CLOSE
    }
    private JCRSList liste;
    private ACTION exitmode = ACTION.CLOSE;
    private CoordinateReferenceSystem crs = null;

    public static JCRSChooser create(final Window parent, final boolean modal) {
        if(parent instanceof JFrame){
            return new JCRSChooser((JFrame)parent, modal);
        }else if(parent instanceof JDialog){
            return new JCRSChooser((JDialog)parent, modal);
        }else{
            return new JCRSChooser(modal);
        }
    }

    /** Creates new form JCRSChooser
     * @param parent
     * @param modal
     */
    public JCRSChooser(final JDialog parent, final boolean modal) {
        super(parent, modal);
        init();
    }

    /** Creates new form JCRSChooser
     * @param parent
     * @param modal
     */
    public JCRSChooser(final JFrame parent, final boolean modal) {
        super(parent, modal);
        init();
    }

    /** Creates new form JCRSChooser
     * @param parent
     * @param modal
     */
    public JCRSChooser(final Frame parent, final boolean modal) {
        super(parent, modal);
        init();
    }

    /** Creates new form JCRSChooser
     * @param parent
     * @param modal
     */
    public JCRSChooser(final boolean modal) {
        super();
        setModal(modal);
        init();
    }

    private void init(){
        initComponents();

        final JLabel lbl = new JLabel(MessageBundle.getString("loading"));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        lbl.setHorizontalTextPosition(SwingConstants.CENTER);
        pan_list.add(BorderLayout.CENTER,lbl);

        new Thread(){
            @Override
            public void run() {
                liste = new JCRSList();

                liste.addListSelectionListener(new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        IdentifiedObject item;
                        try {
                            item = liste.getSelectedItem();
                        } catch (FactoryException ex) {
                            String message = ex.getLocalizedMessage();
                            if (message == null) {
                                message = Classes.getShortClassName(ex);
                            }
                            setErrorMessage(message);
                            return;
                        }
                        setIdentifiedObject(item);
                    }
                });

                pan_list.removeAll();
                pan_list.add(BorderLayout.CENTER, liste);
                pan_list.revalidate();
                pan_list.repaint();
                if(crs != null){
                    liste.setCRS(crs);
                }
            }
        }.start();

        wktArea.setEditable(false);
        wktArea.setContentType("text/html");

        guiMap.getCanvas().setRenderingHint(GO2Hints.KEY_GENERALIZE, false);
        guiMap.getCanvas().setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_ON);
        guiMap.getContainer().setContext(MapBuilder.createContext());
        guiNav.setMap(guiMap);
        guiMap.setHandler(new PanHandler(guiMap,false));

        GridTemplate gridTemplate = new DefaultGridTemplate(
                        DefaultGeographicCRS.WGS84,
                        new BasicStroke(1.2f),
                        new Color(120,120,120,200),
                        new BasicStroke(1,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3, new float[]{5,5}, 0),
                        new Color(120,120,120,60),
                        new Font("serial", Font.BOLD, 10),Color.GRAY,0,Color.WHITE,
                        new Font("serial", Font.ITALIC, 8),Color.GRAY,0,Color.WHITE);

        BackgroundPainter bgWhite = new SolidColorPainter(Color.WHITE);
        guiMap.getCanvas().setBackgroundPainter(BackgroundPainterGroup.wrap(bgWhite ,new GridPainter(gridTemplate)));
        guiForceLongitudeFirst.setSelected(true);

    }


    public void setCRS(final CoordinateReferenceSystem crs) {
        this.crs = crs;
        if (crs != null) {
            String epsg = crs.getName().toString();
            gui_jtf_crs.setText(epsg);
            setIdentifiedObject(crs);
        }
    }

    public CoordinateReferenceSystem getCRS() {
        if(liste != null){
            CoordinateReferenceSystem crs = liste.getCRS();
            if(guiForceLongitudeFirst.isSelected()){
                try {
                    crs = ReferencingUtilities.setLongitudeFirst(crs);
                } catch (FactoryException ex) {
                    Logger.getLogger(JCRSChooser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return crs;
        }else{
            return crs;
        }
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
            warning = formatter.getWarning();
        } catch (RuntimeException e) {
            text = String.valueOf((item!=null)?item.getName():"");
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
        wktArea.setText(buffer.append("</pre></html>").toString());


        //update map area
        final MapContext ctx = guiMap.getContainer().getContext();
        ctx.layers().clear();

        if(item instanceof CoordinateReferenceSystem){
            final Envelope env = CRS.getEnvelope((CoordinateReferenceSystem)item);

            if(env != null){
                final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                ftb.setName("validity");
                ftb.add("geom", Polygon.class,env.getCoordinateReferenceSystem());
                final FeatureType type = ftb.buildFeatureType();
                final GeometryFactory GF = new GeometryFactory();
                final FilterFactory FF = FactoryFinder.getFilterFactory(null);
                final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                                   new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

                final LinearRing ring = GF.createLinearRing(new Coordinate[]{
                                    new Coordinate(env.getMinimum(0), env.getMinimum(1)),
                                    new Coordinate(env.getMinimum(0), env.getMaximum(1)),
                                    new Coordinate(env.getMaximum(0), env.getMaximum(1)),
                                    new Coordinate(env.getMaximum(0), env.getMinimum(1)),
                                    new Coordinate(env.getMinimum(0), env.getMinimum(1))});
                final Polygon polygon = GF.createPolygon(ring, new LinearRing[0]);
                final Feature feature = FeatureUtilities.defaultFeature(type, "0");
                feature.getProperty("geom").setValue(polygon);
                final FeatureCollection col = FeatureStoreUtilities.collection(feature);

                //general informations
                final String name = "mySymbol";
                final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
                final String geometry = null; //use the default geometry of the feature
                final Unit unit = NonSI.PIXEL;
                final Expression offset = StyleConstants.LITERAL_ZERO_FLOAT;
                //the visual element
                final Expression color = SF.literal(Color.BLUE);
                final Expression width = FF.literal(2);
                final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
                final org.opengis.style.Stroke stroke = SF.stroke(color,width,opacity);
                final LineSymbolizer symbolizer = SF.lineSymbolizer(name,geometry,desc,unit,stroke,offset);
                final MutableStyle style = SF.style(symbolizer);

                final MapLayer layer = MapBuilder.createFeatureLayer(col, style);
                ctx.layers().add(layer);
                try {
                    guiMap.getCanvas().setVisibleArea(env);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    /**
     * Copies the given text in the given buffer, while putting the quoted text in italic.
     * The quote character is given by the {@code quote} argument and will be replaced by
     * the usual {@code "} character.
     */
    private static void makeItalic(final String text, final StringBuilder buffer, final char quote) {
        boolean isQuoting = false;
        int last = 0;
        for (int i=text.indexOf(quote); i>=0; i=text.indexOf(quote, last)) {
            buffer.append(text.substring(last, i)).append(isQuoting ? "</cite>\"" : "\"<cite>");
            isQuoting = !isQuoting;
            last = i+1;
        }
        buffer.append(text.substring(last));
    }

    /**
     * Sets an error message to display instead of the current identified object.
     *
     * @param message The error message.
     */
    private void setErrorMessage(final String message) {
        wktArea.setText("<html>"+Vocabulary.format(Vocabulary.Keys.ERROR_1, message)+"</html>");
    }

    public ACTION showDialog() {
        exitmode = ACTION.CLOSE;
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        return exitmode;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new JTabbedPane();
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        gui_jtf_crs = new JTextField();
        pan_list = new JPanel();
        guiForceLongitudeFirst = new JCheckBox();
        jPanel2 = new JPanel();
        jScrollPane2 = new JScrollPane();
        wktArea = new JEditorPane();
        jPanel3 = new JPanel();
        guiMap = new JMap2D();
        guiNav = new JNavigationBar();
        but_valider = new JButton();
        but_fermer = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(MessageBundle.getString("crschooser_title")); 
        jLabel1.setText(MessageBundle.getString("crschooser_crs")); 
        gui_jtf_crs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                gui_jtf_crsActionPerformed(evt);
            }
        });
        gui_jtf_crs.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                gui_jtf_crsKeyTyped(evt);
            }
        });

        pan_list.setLayout(new BorderLayout());

        guiForceLongitudeFirst.setText(MessageBundle.getString("force_longitude_first")); 
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(pan_list, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                    .addComponent(gui_jtf_crs, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .addComponent(jLabel1, Alignment.LEADING)
                    .addComponent(guiForceLongitudeFirst, Alignment.LEADING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(gui_jtf_crs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiForceLongitudeFirst)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(pan_list, GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(MessageBundle.getString("crschooser_list"), jPanel1); 
        jScrollPane2.setViewportView(wktArea);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(MessageBundle.getString("crschooser_wkt"), jPanel2); 
        jPanel3.setLayout(new BorderLayout());
        jPanel3.add(guiMap, BorderLayout.CENTER);

        guiNav.setFloatable(false);
        guiNav.setOrientation(1);
        guiNav.setRollover(true);
        jPanel3.add(guiNav, BorderLayout.EAST);

        jTabbedPane1.addTab(MessageBundle.getString("area_validity"), jPanel3); 
        but_valider.setText(MessageBundle.getString("crschooser_apply"));         but_valider.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                but_valideractionAjouter(evt);
            }
        });

        but_fermer.setText(MessageBundle.getString("crschooser_cancel"));         but_fermer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                but_fermeractionFermer(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(but_valider)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(but_fermer)
                .addContainerGap())
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(but_fermer)
                    .addComponent(but_valider))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void gui_jtf_crsActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_gui_jtf_crsActionPerformed
        liste.searchCRS(gui_jtf_crs.getText());
    }//GEN-LAST:event_gui_jtf_crsActionPerformed

    private void gui_jtf_crsKeyTyped(final KeyEvent evt) {//GEN-FIRST:event_gui_jtf_crsKeyTyped
        liste.searchCRS(gui_jtf_crs.getText());
    }//GEN-LAST:event_gui_jtf_crsKeyTyped

    private void but_valideractionAjouter(final ActionEvent evt) {//GEN-FIRST:event_but_valideractionAjouter
        exitmode = ACTION.APPROVE;
        dispose();
    }//GEN-LAST:event_but_valideractionAjouter

    private void but_fermeractionFermer(final ActionEvent evt) {//GEN-FIRST:event_but_fermeractionFermer
        exitmode = ACTION.CANCEL;
        dispose();
    }//GEN-LAST:event_but_fermeractionFermer
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton but_fermer;
    private JButton but_valider;
    private JCheckBox guiForceLongitudeFirst;
    private JMap2D guiMap;
    private JNavigationBar guiNav;
    private JTextField gui_jtf_crs;
    private JLabel jLabel1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JScrollPane jScrollPane2;
    private JTabbedPane jTabbedPane1;
    private JPanel pan_list;
    private JEditorPane wktArea;
    // End of variables declaration//GEN-END:variables
}
