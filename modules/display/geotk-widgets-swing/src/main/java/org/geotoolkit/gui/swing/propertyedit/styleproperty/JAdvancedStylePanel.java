/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Johann Sorel
 *    (C) 2013, Geomatys
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
package org.geotoolkit.gui.swing.propertyedit.styleproperty;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBException;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JStyleTree;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.util.logging.Logging;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;
import org.opengis.util.FactoryException;

/**
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JAdvancedStylePanel extends StyleElementEditor implements PropertyPane {

    private MapLayer layer = null;
    private Object style = null;
    private StyleElementEditor editor = null;
    private final TreeSelectionListener listener = new TreeSelectionListener() {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            final TreePath path = e.getNewLeadSelectionPath();

            //we validate the previous edition pane
            applyEditor(e.getOldLeadSelectionPath());

            pan_info.removeAll();
            
            if (path != null) {
                final Object val = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                editor = StyleElementEditor.findEditor(val);
                if(editor != null){
                    editor.setLayer(getLayer());
                    editor.parse(val);
                    pan_info.add(editor);
                }                
            }
            
            pan_info.revalidate();
            pan_info.repaint();
        }
    };

    /** Creates new form JAdvancedStylePanel */
    public JAdvancedStylePanel() {
        super(Object.class);
        initComponents();
        tree.addTreeSelectionListener(listener);
        guiXml.setEditorKit(new XMLEditorKit());
        
        
        
        guiTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(guiTabs.getSelectedIndex()==1){
                    //switched to xml pane, update text
                    final StyleXmlIO tool = new StyleXmlIO();
                    try {
                        final StringWriter writer = new StringWriter();
                        tool.writeStyle(writer, (Style)style, Specification.StyledLayerDescriptor.V_1_1_0);
                        guiXml.setText(writer.getBuffer().toString());
                    } catch (JAXBException ex) {
                        LOGGER.log(Level.WARNING,ex.getMessage(),ex);
                    }
                }
            }
        });
        
    }

    private void applyEditor(final TreePath oldPath){
        if(editor == null) return;

        //create implies a call to apply if a style element is present
        final Object obj = editor.create();
        editor.parse(obj);
        
        if(obj instanceof Symbolizer){
            //in case of a symbolizer we must update it.
            if(oldPath != null && oldPath.getLastPathComponent() != null){
                final Symbolizer symbol = (Symbolizer) ((DefaultMutableTreeNode)oldPath.getLastPathComponent()).getUserObject();

                if(!symbol.equals(obj)){
                    //new symbol created is different, update in the rule
                    final MutableRule rule = (MutableRule) ((DefaultMutableTreeNode)oldPath.getParentPath().getLastPathComponent()).getUserObject();

                    final int index = rule.symbolizers().indexOf(symbol);
                    if(index >= 0){
                        rule.symbolizers().set(index, (Symbolizer) obj);
                    }
                }
            }
        }
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        guiTabs = new JTabbedPane();
        jSplitPane1 = new JSplitPane();
        jsp2 = new JScrollPane();
        tree = new JStyleTree();
        jScrollPane1 = new JScrollPane();
        pan_info = new JPanel();
        guiXmlPane = new JPanel();
        jPanel1 = new JPanel();
        guiApply = new JButton();
        jScrollPane2 = new JScrollPane();
        guiXml = new JTextPane();

        setLayout(new BorderLayout());

        jSplitPane1.setDividerLocation(220);
        jSplitPane1.setDividerSize(4);

        jsp2.setViewportView(tree);

        jSplitPane1.setLeftComponent(jsp2);

        pan_info.setLayout(new GridLayout(1, 1));
        jScrollPane1.setViewportView(pan_info);

        jSplitPane1.setRightComponent(jScrollPane1);

        guiTabs.addTab(MessageBundle.getString("xmlGraphic"), jSplitPane1); // NOI18N

        guiXmlPane.setLayout(new BorderLayout());

        jPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));

        guiApply.setText(MessageBundle.getString("apply")); // NOI18N
        guiApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiApplyActionPerformed(evt);
            }
        });
        jPanel1.add(guiApply);

        guiXmlPane.add(jPanel1, BorderLayout.SOUTH);

        jScrollPane2.setViewportView(guiXml);

        guiXmlPane.add(jScrollPane2, BorderLayout.CENTER);

        guiTabs.addTab(MessageBundle.getString("xmlview"), guiXmlPane); // NOI18N

        add(guiTabs, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void guiApplyActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiApplyActionPerformed
        if(layer != null){
            final StyleXmlIO tool = new StyleXmlIO();
            try {
                final MutableStyle style = tool.readStyle(new StringReader(guiXml.getText()), 
                        Specification.SymbologyEncoding.V_1_1_0);

                layer.setStyle(style);
                setTarget(layer);
            } catch (JAXBException ex) {
                Logging.getLogger(JSLDImportExportPanel.class).log(Level.FINEST,ex.getMessage(),ex);
            } catch (FactoryException ex) {
                Logging.getLogger(JSLDImportExportPanel.class).log(Level.FINEST,ex.getMessage(),ex);
            }
        }
    }//GEN-LAST:event_guiApplyActionPerformed
    
    @Override
    public boolean canHandle(Object target) {
        return target instanceof MapLayer;
    }
    
    @Override
    public void apply() {

        applyEditor(tree.getSelectionModel().getSelectionPath());

        style = tree.getStyleElement();

        if (layer != null && style instanceof MutableStyle && style != layer.getStyle()) {
            layer.setStyle((MutableStyle)style);
        }
    }

    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
    }

    @Override
    public MapLayer getLayer() {
        return layer;
    }

    @Override
    public void parse(final Object style) {
        this.style = style;
        parse();
    }

    @Override
    public Object create() {
        style = tree.getStyleElement();
        apply();
        return style;
    }

    private void parse() {
        tree.setStyleElement(style);
    }

    @Override
    public ImageIcon getIcon() {
        return IconBundle.getIcon("16_advanced_style");
    }
    
    @Override
    public Image getPreview() {
        return null;
    }

    @Override
    public String getTitle() {
        return MessageBundle.getString("sldeditor");
    }

    @Override
    public void setTarget(final Object layer) {

        if (layer instanceof MapLayer) {
            setLayer((MapLayer) layer);
            parse(this.layer.getStyle());
        }
    }

    @Override
    public void reset() {
        parse();
    }

    @Override
    public String getToolTip() {
        return "";
    }

    @Override
    public Component getComponent() {
        return this;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    JButton guiApply;
    JTabbedPane guiTabs;
    JTextPane guiXml;
    JPanel guiXmlPane;
    JPanel jPanel1;
    JScrollPane jScrollPane1;
    JScrollPane jScrollPane2;
    JSplitPane jSplitPane1;
    JScrollPane jsp2;
    JPanel pan_info;
    JStyleTree tree;
    // End of variables declaration//GEN-END:variables


    private static final class XMLEditorKit extends StyledEditorKit {

        private final ViewFactory xmlViewFactory = new ViewFactory() {
            @Override
            public View create(Element elem) {
                return new XMLView(elem);
            }
        };
                
        @Override
        public ViewFactory getViewFactory() {
            return xmlViewFactory;
        }
    }
        
    /*
    * Copyright 2006-2008 Kees de Kooter
    *
    * Licensed under the Apache License, Version 2.0 (the "License");
    * you may not use this file except in compliance with the License.
    * You may obtain a copy of the License at
    *
    *      http://www.apache.org/licenses/LICENSE-2.0
    *
    * Unless required by applicable law or agreed to in writing, software
    * distributed under the License is distributed on an "AS IS" BASIS,
    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    * See the License for the specific language governing permissions and
    * limitations under the License.
    */
    public static final class XMLView extends PlainView {
 
        private static final Map<Pattern, Color> PATTERNS = new HashMap<Pattern, Color>();
        static {
            PATTERNS.put(Pattern.compile("(\\<!\\[CDATA\\[).*"),    new Color(0, 190, 0)); // cdata start
            PATTERNS.put(Pattern.compile(".*(]]>)"),                new Color(0, 190, 0)); //cdata end
            PATTERNS.put(Pattern.compile("(</?[a-z]*)\\s?>?"),      new Color(100, 100, 255)); // tag start
            PATTERNS.put(Pattern.compile("\\s(\\w*)\\="),           new Color(127, 0, 127)); // attribute name
            PATTERNS.put(Pattern.compile("(/>)"),                   new Color(100, 100,255)); // tag end
            PATTERNS.put(Pattern.compile("[a-z-]*\\=(\"[^\"]*\")"), new Color(0,0, 190)); // attribute value
            PATTERNS.put(Pattern.compile("(<!--.*-->)"),            new Color(0, 190, 0)); // comment
        }
        
        public XMLView(Element element) {
            super(element);
        }

        @Override
        protected int drawUnselectedText(final Graphics g, int x, int y, final int p0, final int p1) throws BadLocationException {

            final Document doc = getDocument();
            final String text = doc.getText(p0, p1-p0);
            final Segment segment = getLineBuffer();

            final SortedMap<Integer, Integer> startMap = new TreeMap<Integer, Integer>();
            final SortedMap<Integer, Color> colorMap = new TreeMap<Integer, Color>();

            // Match all regexes on this snippet, store positions
            for (Map.Entry<Pattern, Color> entry : PATTERNS.entrySet()) {

                final Matcher matcher = entry.getKey().matcher(text);

                while (matcher.find()) {
                    startMap.put(matcher.start(1), matcher.end());
                    colorMap.put(matcher.start(1), entry.getValue());
                }
            }

            // TODO: check the map for overlapping parts

            int i = 0;

            // Colour the parts
            for (Map.Entry<Integer, Integer> entry : startMap.entrySet()) {
                int start = entry.getKey();
                int end = entry.getValue();

                if (i < start) {
                    g.setColor(Color.black);
                    doc.getText(p0 + i, start - i, segment);
                    x = Utilities.drawTabbedText(segment, x, y, g, this, i);
                }

                g.setColor(colorMap.get(start));
                i = end;
                doc.getText(p0 + start, i - start, segment);
                x = Utilities.drawTabbedText(segment, x, y, g, this, start);
            }

            // Paint possible remaining text black
            if (i < text.length()) {
                g.setColor(Color.black);
                doc.getText(p0 + i, text.length() - i, segment);
                x = Utilities.drawTabbedText(segment, x, y, g, this, i);
            }

            return x;
        }

    }
    
    
}
