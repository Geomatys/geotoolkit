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

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.gui.swing.tree.Trees;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CQL {

    private CQL() {}
    
    private static Object compileExpression(String cql) {
        try {
            //lexer splits input into tokens
            final ANTLRStringStream input = new ANTLRStringStream(cql);
            final TokenStream tokens = new CommonTokenStream(new CQLLexer(input));

            //parser generates abstract syntax tree
            final CQLParser parser = new CQLParser(tokens);
            final CQLParser.expression_return retexp = parser.expression();
            
            return retexp;
            
        } catch (RecognitionException e) {
            throw new IllegalStateException("Recognition exception is never thrown, only declared.");
        }
    }
    
    private static Object compileFilter(String cql) {
        try {
            //lexer splits input into tokens
            final ANTLRStringStream input = new ANTLRStringStream(cql);
            final TokenStream tokens = new CommonTokenStream(new CQLLexer(input));

            //parser generates abstract syntax tree
            final CQLParser parser = new CQLParser(tokens);
            final CQLParser.filter_return retfilter = parser.filter();
            
            return retfilter;
            
        } catch (RecognitionException e) {
            throw new IllegalStateException("Recognition exception is never thrown, only declared.");
        }
    }
    
    public static CommonTree compile(String cql) {
        final Object obj =compileExpression(cql);
        
        CommonTree tree = null;
        if(obj instanceof CQLParser.expression_return){
            tree = (CommonTree)((CQLParser.expression_return)obj).tree;
        }else if(obj instanceof CQLParser.filter_return){
            tree = (CommonTree)((CQLParser.filter_return)obj).tree;
        }
        
        return tree;
    }
    
    public static Expression parseExpression(String cql) throws CQLException{
        final Object obj = compileExpression(cql);
        
        CommonTree tree = null;
        Expression result = null;
        if(obj instanceof CQLParser.expression_return){
            tree = (CommonTree)((CQLParser.expression_return)obj).tree;
            result = convertExpression(tree, FactoryFinder.getFilterFactory(null));
        }
        
        return result;
    }
    
    public static Filter parseFilter(String cql) throws CQLException{
        final Object obj = compileFilter(cql);
        
        CommonTree tree = null;
        Filter result = null;
        if(obj instanceof CQLParser.filter_return){
            tree = (CommonTree)((CQLParser.filter_return)obj).tree;
            result = convertFilter(tree, FactoryFinder.getFilterFactory(null));
        }
        
        return result;
    }
    
    
    public static String write(Filter filter){
        final StringBuilder sb = new StringBuilder();
        filter.accept(FilterToCQLVisitor.INSTANCE,sb);
        return sb.toString();
    }
    
    public static String write(Expression exp){
        final StringBuilder sb = new StringBuilder();
        exp.accept(FilterToCQLVisitor.INSTANCE,sb);
        return sb.toString();
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
    
    /**
     * Convert the given tree in an Expression.
     */
    private static Expression convertExpression(CommonTree tree, FilterFactory ff) throws CQLException{
        
        if(!(tree.token != null && tree.token.getTokenIndex() >= 0)){
            throw new CQLException("Unreconized expression : type="+tree.getType()+" text=" + tree.getText());
        }
        
        final int type = tree.getType();
        if(CQLParser.PROPERTY_NAME_1 == type){
            //strip start and end "
            final String text = tree.getText();
            return ff.property(text.substring(1, text.length()-1));
        }else if(CQLParser.PROPERTY_NAME_2 == type){
            return ff.property(tree.getText());
        }else if(CQLParser.INT == type){
            return ff.literal(Integer.valueOf(tree.getText()));
        }else if(CQLParser.FLOAT == type){
            return ff.literal(Double.valueOf(tree.getText()));
        }else if(CQLParser.TEXT == type){
            //strip start and end '
            final String text = tree.getText();
            return ff.literal(text.substring(1, text.length()-1));
        }else if(CQLParser.OPERATOR == type){
            final String text = tree.getText();
            final Expression left = convertExpression((CommonTree)tree.getChild(0), ff);
            final Expression right = convertExpression((CommonTree)tree.getChild(1), ff);
            if("+".equals(text)){
                return ff.add(left,right);
            }else if("-".equals(text)){
                return ff.subtract(left,right);
            }else if("*".equals(text)){
                return ff.multiply(left,right);
            }else if("/".equals(text)){
                return ff.divide(left,right);
            }
        }else if(CQLParser.FUNCTION_NAME == type){
            String functionName = tree.getText();
            //remove the (
            functionName = functionName.substring(0, functionName.length()-1);
            final List<Expression> exps = new ArrayList<Expression>();
            for(Object child : tree.getChildren()){
                exps.add(convertExpression((CommonTree)child, ff));
            }
            return ff.function(functionName, exps.toArray(new Expression[exps.size()]));
        }
        
        throw new CQLException("Unreconized expression : type="+tree.getType()+" text=" + tree.getText());
    }
    
    /**
     * Convert the given tree in a Filter.
     */
    private static Filter convertFilter(CommonTree tree, FilterFactory ff) throws CQLException{
        
        if(!(tree.token != null && tree.token.getTokenIndex() >= 0)){
            throw new CQLException("Unreconized filter : type="+tree.getType()+" text=" + tree.getText());
        }
        
        final int type = tree.getType();
        if(CQLParser.COMPARE == type){
            final String text = tree.getText();
            final Expression left = convertExpression((CommonTree)tree.getChild(0), ff);
            final Expression right = convertExpression((CommonTree)tree.getChild(1), ff);
            
            if("=".equals(text)){
                return ff.equals(left, right);
            }else if("<>".equals(text)){
                return ff.notEqual(left, right);
            }else if(">".equals(text)){
                return ff.greater(left,right);
            }else if("<".equals(text)){
                return ff.less(left,right);
            }else if(">=".equals(text)){
                return ff.greaterOrEqual(left,right);
            }else if("<=".equals(text)){
                return ff.lessOrEqual(left,right);
            }else if("<=".equals(text)){
                return ff.lessOrEqual(left,right);
            }else if("like".equalsIgnoreCase(text)){
                return ff.like(left, right.evaluate(null, String.class));
            }
        }else if(CQLParser.ISNULL == type){
            final Expression exp = convertExpression((CommonTree)tree.getChild(0), ff);
            return ff.isNull(exp);
        }else if(CQLParser.BETWEEN == type){
            final Expression exp1 = convertExpression((CommonTree)tree.getChild(0), ff);
            final Expression exp2 = convertExpression((CommonTree)tree.getChild(1), ff);
            final Expression exp3 = convertExpression((CommonTree)tree.getChild(2), ff);
            return ff.between(exp1, exp2, exp3);
        }else if(CQLParser.IN == type){
            final Expression val = convertExpression((CommonTree)tree.getChild(0), ff);            
            final int nbchild = tree.getChildCount();
            final List<Expression> exps = new ArrayList<Expression>();
            for(int i=1; i<nbchild; i++){
                exps.add(convertExpression((CommonTree)tree.getChild(i), ff));
            }
            
            final int size = exps.size();
            if(size == 0){
                return Filter.EXCLUDE;
            }else if(size == 1){
                return ff.equals(val, exps.get(0));
            }else{
                final List<Filter> filters = new ArrayList<Filter>();
                for(Expression exp : exps){
                    filters.add(ff.equals(val, exp));
                }
                return ff.or(filters);
            }            
        }else if(CQLParser.AND == type){
            final List<Filter> filters = new ArrayList<Filter>();
            final int nbchild = tree.getChildCount();
            for(int i=0; i<nbchild; i++){
                filters.add(convertFilter((CommonTree)tree.getChild(i), ff));
            }
            return ff.and(filters);
        }else if(CQLParser.OR == type){
            final List<Filter> filters = new ArrayList<Filter>();
            final int nbchild = tree.getChildCount();
            for(int i=0; i<nbchild; i++){
                filters.add(convertFilter((CommonTree)tree.getChild(i), ff));
            }
            return ff.or(filters);
        }
        
        throw new CQLException("Unreconized filter : type="+tree.getType()+" text=" + tree.getText());
        
    }
    
}
