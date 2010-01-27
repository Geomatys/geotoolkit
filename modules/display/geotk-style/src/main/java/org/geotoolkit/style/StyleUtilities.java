/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010 Geomatys
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

package org.geotoolkit.style;

import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.lang.Static;

import org.opengis.style.Symbolizer;

/**
 * Convinient methods to transform a style in tree.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@Static
public final class StyleUtilities {

    private StyleUtilities(){}

    public static MutableTreeNode asTreeNode(MutableStyle element){
        return new StyleNode(element);
    }

    public static MutableTreeNode asTreeNode(MutableFeatureTypeStyle element){
        return new FTSNode(element);
    }

    public static MutableTreeNode asTreeNode(MutableRule element){
        return new RuleNode(element);
    }

    public static MutableTreeNode asTreeNode(Symbolizer element){
        return new SymbolNode(element);
    }

    private static class StyleNode extends DefaultMutableTreeNode{

        private MutableStyle element;

        public StyleNode(MutableStyle element){
            super(element);
            this.element = element;
            //todo must add listener mecanism
            for(MutableFeatureTypeStyle fts : element.featureTypeStyles()){
                add(asTreeNode(fts));
            }

        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Style : ").append(element.getClass().getSimpleName()).append(' ');
            sb.append(element.getName()).append(element.getDescription());
            return sb.toString();
        }

    }

    private static class FTSNode extends DefaultMutableTreeNode{

        private MutableFeatureTypeStyle element;

        public FTSNode(MutableFeatureTypeStyle element){
            super(element);
            this.element = element;
            //todo must add listener mecanism
            for(MutableRule rule : element.rules()){
                add(asTreeNode(rule));
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("FTS : ").append(element.getClass().getSimpleName()).append(' ');
            sb.append(element.getName()).append(element.getDescription());
            return sb.toString();
        }

    }

    private static class RuleNode extends DefaultMutableTreeNode{

        private MutableRule element;

        public RuleNode(MutableRule element){
            super(element);
            this.element = element;
            //todo must add listener mecanism
            for(Symbolizer symbol : element.symbolizers()){
                add(asTreeNode(symbol));
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Rule : ").append(element.getClass().getSimpleName()).append(' ');
            sb.append(element.getName()).append(element.getDescription());
            return sb.toString();
        }

    }

    private static class SymbolNode extends DefaultMutableTreeNode{

        private Symbolizer element;

        public SymbolNode(Symbolizer element){
            super(element);
            this.element = element;
            //todo must add listener mecanism
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Symbol : ").append(element.getClass().getSimpleName()).append(' ');
            sb.append(element.getName()).append(element.getDescription());
            return sb.toString();
        }

    }

}
