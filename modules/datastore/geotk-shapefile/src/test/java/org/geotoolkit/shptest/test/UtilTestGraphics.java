package org.geotoolkit.shptest.test;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class you can use in test cases to ensure a renderer is doing what you expected.
 * <p>
 * Originally made to check up on SLD settings; adding to support data in case it is of general
 * utility. Please be advised it is very hard to write cross platform tests for things
 *  
 * @author Jody Garnett
 * @module pending
 */
public class UtilTestGraphics extends Graphics2D{
    public Map<Key, ?> hints;
    public Shape clip;
    public Shape draw;
    public GlyphVector glyphs;
    public float y;
    public float x;
    public Object image;
    public ImageObserver observer;
    public AffineTransform transform;
    public BufferedImageOp op;
    public String string;
    public Color background;
    public boolean fill;
    public Composite composite;
    public Paint paint;
    public Font font;
    
    @Override
    public void addRenderingHints(final Map<?, ?> toAdd) {
        //hints.putAll( toAdd );
    }

    @Override
    public void clip(final Shape s) {
        clip = s;
    }

    @Override
    public void draw(final Shape s) {
        draw = s;
    }

    @Override
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
        this.x = x;
        this.y = y;
        glyphs = g;
    }

    @Override
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        this.image = img;
        this.transform = xform;
        this.observer = obs;
        return true;
    }

    @Override
    public void drawImage(final BufferedImage img, final BufferedImageOp op, final int x, final int y) {
        this.image = img;
        this.x = x;
        this.y = y;
        this.op = op;
    }

    @Override
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
        this.image = img;
        this.transform = xform;
    }

    @Override
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
        this.image = img;
        this.transform = xform;
    }

    @Override
    public void drawString(final String str, final int x, final int y) {
        this.string = str;
        this.x = x;
        this.y = y;
    }

    @Override
    public void drawString(final String s, final float x, final float y) {
        this.string= s;
        this.x = x;
        this.y = y;
    }

    @Override
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
        StringBuffer build = new StringBuffer();
        char c;
        while( (c = iterator.next()) != iterator.DONE ){
            build.append( c );
        }
        this.string = build.toString();
        this.x = x;
        this.y = y;
    }

    @Override
    public void drawString(final AttributedCharacterIterator iterator, final float x,
            final float y) {
        StringBuffer build = new StringBuffer();
        char c;
        while( (c = iterator.next()) != iterator.DONE ){
            build.append( c );
        }
        this.string = build.toString();
        this.x = x;
        this.y = y;
    }

    @Override
    public void fill(final Shape s) {
        this.draw = s;
        this.fill = true;
    }

    @Override
    public Color getBackground() {
        return background;
    }

    @Override
    public Composite getComposite() {
        return composite;
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return null;
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return null;
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public Object getRenderingHint(final Key hintKey) {
        return hints.get( hintKey );
    }

    @Override
    public RenderingHints getRenderingHints() {
        return new RenderingHints( hints );
    }

    @Override
    public Stroke getStroke() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AffineTransform getTransform() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hit(final Rectangle rect, final Shape s, final boolean onStroke) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void rotate(final double theta) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void rotate(final double theta, final double x, final double y) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void scale(final double sx, final double sy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBackground(final Color color) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setComposite(final Composite comp) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPaint(final Paint paint) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setRenderingHint(final Key hintKey, final Object hintValue) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setRenderingHints(final Map<?, ?> hints) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setStroke(final Stroke s) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTransform(final AffineTransform Tx) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void shear(final double shx, final double shy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void transform(final AffineTransform Tx) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void translate(final int x, final int y) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void translate(final double tx, final double ty) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearRect(final int x, final int y, final int width, final int height) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Graphics create() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int arcAngle) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor,
            final ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height,
            final ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height,
            final Color bgcolor, final ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2,
            final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2,
            final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor,
            final ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        x = x1;
        y = y1;
        fill = false;
    }

    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        draw( new Rectangle( x,y, width,height) );
    }

    @Override
    public void drawPolygon(final int[] points, final int[] points2, final int points3) {
        draw( new Polygon( points, points2, points3 ) );
    }

    @Override
    public void drawPolyline(final int[] points, final int[] points2, final int points3) {
        draw( new Polygon( points, points2, points3 ) );
    }

    @Override
    public void drawRoundRect(final int x, final int y, final int width, final int height,
            final int arcWidth, final int arcHeight) {
        draw( new RoundRectangle2D.Float(x,y,width,height,arcWidth, arcHeight ));
    }

    @Override
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
            final int arcAngle) {
        fill( new Rectangle( x,y, width,height) );
    }

    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        fill( new Rectangle( x,y, width,height) );
    }

    @Override
    public void fillPolygon(final int[] points, final int[] points2, final int points3) {
        fill( new Polygon( points, points2, points3 ) );
    }

    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        fill( new Rectangle( x,y,width,height));
    }

    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height,
            final int arcWidth, final int arcHeight) {
        fill( new RoundRectangle2D.Float(x,y,width,height,arcWidth, arcHeight ));
    }

    @Override
    public Shape getClip() {
        return clip;
    }

    @Override
    public Rectangle getClipBounds() {
        return clip != null ? clip.getBounds() : null;
    }

    @Override
    public Color getColor() {
        return (Color) paint;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public FontMetrics getFontMetrics(final Font f) {
        return null;
    }

    @Override
    public void setClip(final Shape clip) {
        this.clip = clip;
    }

    @Override
    public void setClip(final int x, final int y, final int width, final int height) {
        setClip( new Rectangle(x,y,width,height));
    }

    @Override
    public void setColor(final Color c) {
        paint = c;
    }

    @Override
    public void setFont(final Font font) {
        this.font = font;
    }

    @Override
    public void setPaintMode() {        
    }

    @Override
    public void setXORMode(final Color c1) {
        // TODO Auto-generated method stub
        
    }

}
