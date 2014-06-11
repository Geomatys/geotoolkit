/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

/**
 * Swing CQL text pane. highlights syntax.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class JCQLTextPane extends JPanel implements KeyListener{

    private final JTextPane guiText = new JTextPane();
    private final JLabel guiError = new JLabel();
    
    final Style styleDefault;
    final Style styleComment;
    final Style styleFunction;
    final Style styleLiteral;
    final Style styleParenthese;
    final Style styleOperator;
    final Style styleBinary;
    final Style stylePropertyName;
    final Style styleError;
    
    public JCQLTextPane() {
        super(new BorderLayout());
        
        guiText.setBackground(Color.WHITE);
        
        add(BorderLayout.CENTER,new JScrollPane(guiText));
        add(BorderLayout.SOUTH,guiError);
        guiText.addKeyListener(this);
        
        styleDefault = guiText.addStyle("default", null);
        StyleConstants.setForeground(styleDefault, Color.BLACK); 
        
        styleComment = guiText.addStyle("comment", null);
        StyleConstants.setForeground(styleComment, Color.GRAY);
        
        styleLiteral = guiText.addStyle("literal", null);
        StyleConstants.setForeground(styleLiteral, new Color(0, 150, 0));
        
        styleFunction = guiText.addStyle("function", null);
        StyleConstants.setForeground(styleFunction, Color.MAGENTA);
        
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
    
    /**
     * Insert text at current caret position
     * @param text 
     */
    public void insertText(String text){
        final int position = guiText.getCaretPosition();
        final String cql = guiText.getText();
        final StringBuilder sb = new StringBuilder();
        sb.append(cql.substring(0,position));
        sb.append(text);
        sb.append(cql.substring(position));
        
        guiText.setText(sb.toString());
        guiText.setCaretPosition(position+text.length());
        updateHightLight();
    }
    
    public void addText(String text){
        guiText.setText(guiText.getText()+text);
        updateHightLight();
    }
    
    public String getText(){
        return guiText.getText();
    }
    
    public void setFilter(Filter filter){
        setText(CQL.write(filter));
    }
    
    public void setExpression(Expression exp){
        setText(CQL.write(exp));
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

        final ParseTree tree = CQL.compile(txt);
        doc.setCharacterAttributes(0, txt.length(), styleError, true);
        syntaxHighLight(tree, doc, new AtomicInteger());
    }
    
    private void syntaxHighLight(ParseTree tree, StyledDocument doc, AtomicInteger position){
        
        if(tree instanceof ParserRuleContext){
            final ParserRuleContext prc = (ParserRuleContext) tree;
            if(prc.exception!=null){
                //error nodes
                final Token tokenStart = prc.getStart();
                Token tokenEnd = prc.getStop();
                if(tokenEnd==null) tokenEnd = tokenStart;
                final int offset = tokenStart.getStartIndex();
                final int length = tokenEnd.getStopIndex()-tokenStart.getStartIndex() +1;
                doc.setCharacterAttributes(offset, length, styleError, true);
                return;
            }
        }
        
        if(tree instanceof TerminalNode){
            final TerminalNode tn = (TerminalNode) tree;
            // if index<0 = missing token
            final Token token = tn.getSymbol();
            final int offset = token.getStartIndex();
            final int length = token.getStopIndex()-token.getStartIndex() +1;
            position.addAndGet(length);
            
            switch(token.getType()){
                
                case CQLLexer.COMMA : 
                case CQLLexer.UNARY : 
                case CQLLexer.MULT : 
                    doc.setCharacterAttributes(offset, length, styleDefault, true);
                    break;
                    
                // EXpressions -------------------------------------------------
                case CQLLexer.TEXT : 
                case CQLLexer.INT : 
                case CQLLexer.FLOAT : 
                case CQLLexer.DATE : 
                case CQLLexer.DURATION_P : 
                case CQLLexer.DURATION_T : 
                case CQLLexer.POINT : 
                case CQLLexer.LINESTRING : 
                case CQLLexer.POLYGON : 
                case CQLLexer.MPOINT : 
                case CQLLexer.MLINESTRING : 
                case CQLLexer.MPOLYGON : 
                    doc.setCharacterAttributes(offset, length, styleLiteral, true);
                    break;
                case CQLLexer.PROPERTY_NAME :
                    doc.setCharacterAttributes(offset, length, stylePropertyName, true);
                    break;
                case CQLLexer.NAME :
                    if(tree.getChildCount()==0){
                        //property name
                        doc.setCharacterAttributes(offset, length, stylePropertyName, true);
                    }else{
                        //function name
                        doc.setCharacterAttributes(offset, length, styleFunction, true);
                    }
                    break;
                case CQLLexer.RPAREN : 
                case CQLLexer.LPAREN : 
                    doc.setCharacterAttributes(offset, length, styleParenthese, true);
                    break;                    
                    
                case CQLLexer.COMPARE :
                case CQLLexer.LIKE :
                case CQLLexer.IS :
                case CQLLexer.BETWEEN :
                case CQLLexer.IN :
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
                case CQLLexer.CROSSES :
                case CQLLexer.DISJOINT :
                case CQLLexer.DWITHIN :
                case CQLLexer.EQUALS :
                case CQLLexer.INTERSECTS :
                case CQLLexer.OVERLAPS :
                case CQLLexer.TOUCHES :
                case CQLLexer.WITHIN :
                    doc.setCharacterAttributes(offset, length, styleBinary, true);
                    break;
                default : 
                    doc.setCharacterAttributes(offset, length, styleError, true);
                    break;
            }
        }
        
        final int nbChild = tree.getChildCount();        
        for(int i=0;i<nbChild;i++){
            syntaxHighLight(tree.getChild(i), doc, position);
        }
    }
    
}
