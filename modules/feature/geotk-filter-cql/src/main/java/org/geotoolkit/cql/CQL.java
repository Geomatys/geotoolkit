/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.cql;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.geotoolkit.gui.swing.tree.Trees;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CQL {

    private CQL() {}
    
    public static CommonTree compile(String expression) {
        try {
            //lexer splits input into tokens
            final ANTLRStringStream input = new ANTLRStringStream(expression);
            final TokenStream tokens = new CommonTokenStream(new cqlLexer(input));

            //parser generates abstract syntax tree
            final cqlParser parser = new cqlParser(tokens);
            cqlParser.result_return ret = parser.result();

            //acquire parse result
            final CommonTree ast = (CommonTree) ret.tree;
            return ast;
        } catch (RecognitionException e) {
            throw new IllegalStateException("Recognition exception is never thrown, only declared.");
        }
    }
    
    public static Filter read(String cql){
        return null;
    }
    
    public static String write(Filter filter){
        return null;
    }
    
    public static String write(Expression exp){
        return null;
    }
    
    /**
     * Generate a nice looking tree representation of the tree.
     */
    public static String toString(CommonTree tree){
        if(tree == null) return null;        
        final DefaultMutableTreeNode node = explore(tree);
        return Trees.toString(node);
    }
    
    /**
     * Create a TreeNode for the given tree. method is recursive.
     */
    private static DefaultMutableTreeNode explore(CommonTree tree){
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(tree);
        
        final List children = tree.getChildren();
        
        if(children != null){
            for(Object child : children){
                final DefaultMutableTreeNode n = explore((CommonTree)child);
                node.add(n);
            }
        }
        return node;
    }
    
}
