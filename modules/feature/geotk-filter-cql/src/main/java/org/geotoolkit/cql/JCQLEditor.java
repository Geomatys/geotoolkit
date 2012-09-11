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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

/**
 * Swing filter/expression editor.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class JCQLEditor extends JPanel implements KeyListener{

    private final JTextPane guiText = new JTextPane();
    private final JLabel guiError = new JLabel();
    
    final Style styleDefault;
    final Style styleComment;
    final Style styleLiteral;
    final Style styleParenthese;
    final Style styleOperator;
    final Style styleBinary;
    final Style stylePropertyName;
    final Style styleError;
    
    public JCQLEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER,new JScrollPane(guiText));
        add(BorderLayout.SOUTH,guiError);
        guiText.addKeyListener(this);
        
        styleDefault = guiText.addStyle("default", null);
        StyleConstants.setForeground(styleDefault, Color.BLACK); 
        
        styleComment = guiText.addStyle("comment", null);
        StyleConstants.setForeground(styleComment, Color.GRAY);
        
        styleLiteral = guiText.addStyle("literal", null);
        StyleConstants.setForeground(styleLiteral, new Color(0, 150, 0));
        
        styleParenthese = guiText.addStyle("parenthese", null);
        StyleConstants.setForeground(styleParenthese, new Color(0, 100, 0));
        
        styleOperator = guiText.addStyle("operator", null);
        StyleConstants.setForeground(styleOperator, Color.BLACK);
        StyleConstants.setBold(styleOperator, true);
        
        styleBinary = guiText.addStyle("binary", null);
        StyleConstants.setForeground(styleBinary, Color.BLACK);
        StyleConstants.setBold(styleBinary, true);
        
        stylePropertyName = guiText.addStyle("property", null);
        StyleConstants.setForeground(stylePropertyName, Color.BLUE);
        StyleConstants.setBold(stylePropertyName, true);
        
        styleError = guiText.addStyle("error", null);
        StyleConstants.setForeground(styleError, Color.RED);
        StyleConstants.setBold(styleError, true);
        
    }

    public void setText(String cql){
        guiText.setText(cql);
        updateHightLight();
    }
    
    public String getText(){
        return guiText.getText();
    }
    
    public Filter getFilter() throws CQLException{
        return CQL.parseFilter(guiText.getText());
    }
    
    public Expression getExpression() throws CQLException{
        return CQL.parseExpression(guiText.getText());
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        updateHightLight();
    }
    
    private void updateHightLight(){
        final StyledDocument doc = (StyledDocument) guiText.getDocument();
        final String txt = guiText.getText();

        final CommonTree tree = CQL.compile(txt);
        doc.setCharacterAttributes(0, txt.length(), styleError, true);
        syntaxHighLight(tree, doc, new AtomicInteger());
    }
    
    private void syntaxHighLight(CommonTree tree, StyledDocument doc, AtomicInteger position){
        
        if(tree.token != null && tree.token.getTokenIndex() >= 0){
            // if index<0 = missing token
            final CommonToken ct = (CommonToken) tree.token;
            final int offset = ct.getStartIndex();
            final int length = ct.getStopIndex()-ct.getStartIndex() +1;
            position.addAndGet(length);
            
            switch(tree.token.getType()){
                
                // EXpressions -------------------------------------------------
                case CQLLexer.TEXT : 
                case CQLLexer.INT : 
                case CQLLexer.FLOAT : 
                case CQLLexer.UNARY : 
                case CQLLexer.DATE : 
                case CQLLexer.DURATION : 
                case CQLLexer.DURATION_P : 
                case CQLLexer.DURATION_T : 
                case CQLLexer.POINT : 
                case CQLLexer.LINESTRING : 
                case CQLLexer.POLYGON : 
                case CQLLexer.MPOINT : 
                case CQLLexer.MLINESTRING : 
                case CQLLexer.MPOLYGON : 
                case CQLLexer.COORD : 
                case CQLLexer.COORDS : 
                    doc.setCharacterAttributes(offset, length, styleLiteral, true);
                    break;
                case CQLLexer.PROPERTY_NAME :
                case CQLLexer.NAME :
                    doc.setCharacterAttributes(offset, length, stylePropertyName, true);
                    break;
                case CQLLexer.RPAREN : 
                case CQLLexer.LPAREN : 
                    doc.setCharacterAttributes(offset, length, styleParenthese, true);
                    break;
                    
                case CQLLexer.EXP_ADD :
                case CQLLexer.EXP_MUL :
                    doc.setCharacterAttributes(offset, length, styleOperator, true);
                    break;
                    
                    
                case CQLLexer.COMPARE :
                case CQLLexer.LIKE :
                case CQLLexer.IS :
                case CQLLexer.BETWEEN :
                case CQLLexer.IN :
                case CQLLexer.FIL_LOG :
                    doc.setCharacterAttributes(offset, length, styleOperator, true);
                    break;
                case CQLLexer.AND :
                case CQLLexer.OR :
                case CQLLexer.NOT :
                    doc.setCharacterAttributes(offset, length, styleBinary, true);
                    break;
                case CQLLexer.BBOX :
                case CQLLexer.BEYOND :
                case CQLLexer.CONTAINS :
                case CQLLexer.CROSS :
                case CQLLexer.DISJOINT :
                case CQLLexer.DWITHIN :
                case CQLLexer.EQUALS :
                case CQLLexer.INTERSECT :
                case CQLLexer.OVERLAP :
                case CQLLexer.TOUCH :
                case CQLLexer.WITHIN :
                    doc.setCharacterAttributes(offset, length, styleBinary, true);
                    break;
                default : 
                    doc.setCharacterAttributes(offset, length, styleError, true);
                    break;
            }
        }
        
        final List children = tree.getChildren();        
        if(children != null){
            for(Object child : children){
                syntaxHighLight((CommonTree)child, doc, position);
            }
        }
    }
    
}
