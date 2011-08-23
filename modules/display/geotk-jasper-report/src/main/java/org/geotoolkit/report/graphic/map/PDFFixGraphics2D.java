/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.report.graphic.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.geotoolkit.util.collection.WeakHashSet;

/**
 * Itext has several weakness which are fixed in this encapsulation
 * - does not support composite alpha on image.
 * Here we override image paint operations.
 * The composite is multiply directly in the image before drawing.
 * - is not concurrent
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PDFFixGraphics2D extends Graphics2D {

    private final Graphics2D wrapped;

    public PDFFixGraphics2D(Graphics2D wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public synchronized void draw(Shape s) {
        wrapped.draw(s);
    }

    @Override
    public synchronized void drawString(String str, int x, int y) {
        wrapped.drawString(str, x, y);
    }

    @Override
    public synchronized void drawString(String str, float x, float y) {
        wrapped.drawString(str, x, y);
    }

    @Override
    public synchronized void drawString(AttributedCharacterIterator iterator, int x, int y) {
        wrapped.drawString(iterator, x, y);
    }

    @Override
    public synchronized void drawString(AttributedCharacterIterator iterator, float x, float y) {
        wrapped.drawString(iterator, x, y);
    }

    @Override
    public synchronized void drawGlyphVector(GlyphVector g, float x, float y) {
        wrapped.drawGlyphVector(g, x, y);
    }

    @Override
    public synchronized void fill(Shape s) {
        wrapped.fill(s);
    }

    @Override
    public synchronized boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return wrapped.hit(rect,s,onStroke);
    }

    @Override
    public synchronized GraphicsConfiguration getDeviceConfiguration() {
        return wrapped.getDeviceConfiguration();
    }

    @Override
    public synchronized void setComposite(Composite comp) {
        wrapped.setComposite(comp);
    }

    @Override
    public synchronized void setPaint(Paint paint) {
        wrapped.setPaint(paint);
    }

    @Override
    public synchronized void setStroke(Stroke s) {
        wrapped.setStroke(s);
    }

    @Override
    public synchronized void setRenderingHint(Key hintKey, Object hintValue) {
        wrapped.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public synchronized Object getRenderingHint(Key hintKey) {
        return wrapped.getRenderingHint(hintKey);
    }

    @Override
    public synchronized void setRenderingHints(Map<?, ?> hints) {
        wrapped.setRenderingHints(hints);
    }

    @Override
    public synchronized void addRenderingHints(Map<?, ?> hints) {
        wrapped.addRenderingHints(hints);
    }

    @Override
    public synchronized RenderingHints getRenderingHints() {
        return wrapped.getRenderingHints();
    }

    @Override
    public synchronized void translate(int x, int y) {
        wrapped.translate(x, y);
    }

    @Override
    public synchronized void translate(double tx, double ty) {
        wrapped.translate(tx, ty);
    }

    @Override
    public synchronized void rotate(double theta) {
        wrapped.rotate(theta);
    }

    @Override
    public synchronized void rotate(double theta, double x, double y) {
        wrapped.rotate(theta, x, y);
    }

    @Override
    public synchronized void scale(double sx, double sy) {
        wrapped.scale(sx, sy);
    }

    @Override
    public synchronized void shear(double shx, double shy) {
        wrapped.shear(shx, shy);
    }

    @Override
    public synchronized void transform(AffineTransform Tx) {
        wrapped.transform(Tx);
    }

    @Override
    public synchronized void setTransform(AffineTransform Tx) {
        wrapped.setTransform(Tx);
    }

    @Override
    public synchronized AffineTransform getTransform() {
        return wrapped.getTransform();
    }

    @Override
    public synchronized Paint getPaint() {
        return wrapped.getPaint();
    }

    @Override
    public synchronized Composite getComposite() {
        return wrapped.getComposite();
    }

    @Override
    public synchronized void setBackground(Color color) {
        wrapped.setBackground(color);
    }

    @Override
    public synchronized Color getBackground() {
        return wrapped.getBackground();
    }

    @Override
    public synchronized Stroke getStroke() {
        return wrapped.getStroke();
    }

    @Override
    public synchronized void clip(Shape s) {
        wrapped.clip(s);
    }

    @Override
    public synchronized FontRenderContext getFontRenderContext() {
        return wrapped.getFontRenderContext();
    }

    @Override
    public synchronized Graphics create() {
        final Graphics2D gr = (Graphics2D) wrapped.create();
        return new PDFFixGraphics2D(gr);
    }

    @Override
    public synchronized Color getColor() {
        return wrapped.getColor();
    }

    @Override
    public synchronized void setColor(Color c) {
        wrapped.setColor(c);
    }

    @Override
    public synchronized void setPaintMode() {
        wrapped.setPaintMode();
    }

    @Override
    public synchronized void setXORMode(Color c1) {
        wrapped.setXORMode(c1);
    }

    @Override
    public synchronized Font getFont() {
        return wrapped.getFont();
    }

    @Override
    public synchronized void setFont(Font font) {
        wrapped.setFont(font);
    }

    @Override
    public synchronized FontMetrics getFontMetrics(Font f) {
        return wrapped.getFontMetrics(f);
    }

    @Override
    public synchronized Rectangle getClipBounds() {
        return wrapped.getClipBounds();
    }

    @Override
    public synchronized void clipRect(int x, int y, int width, int height) {
        wrapped.clipRect(x, y, width, height);
    }

    @Override
    public synchronized void setClip(int x, int y, int width, int height) {
        wrapped.setClip(x, y, width, height);
    }

    @Override
    public synchronized Shape getClip() {
        return wrapped.getClip();
    }

    @Override
    public synchronized void setClip(Shape clip) {
        wrapped.setClip(clip);
    }

    @Override
    public synchronized void copyArea(int x, int y, int width, int height, int dx, int dy) {
        wrapped.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public synchronized void drawLine(int x1, int y1, int x2, int y2) {
        wrapped.drawLine(x1, y1, x2, y2);
    }

    @Override
    public synchronized void fillRect(int x, int y, int width, int height) {
        wrapped.fillRect(x, y, width, height);
    }

    @Override
    public synchronized void clearRect(int x, int y, int width, int height) {
        wrapped.clearRect(x, y, width, height);
    }

    @Override
    public synchronized void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        wrapped.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public synchronized void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        wrapped.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public synchronized void drawOval(int x, int y, int width, int height) {
        wrapped.drawOval(x, y, width, height);
    }

    @Override
    public synchronized void fillOval(int x, int y, int width, int height) {
        wrapped.fillOval(x, y, width, height);
    }

    @Override
    public synchronized void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        wrapped.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public synchronized void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        wrapped.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public synchronized void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        wrapped.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public synchronized void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        wrapped.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public synchronized void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        wrapped.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public synchronized void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        wrapped.drawRenderedImage(img, xform);
    }

    @Override
    public synchronized void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        wrapped.drawRenderableImage(img, xform);
    }
    
    @Override
    public synchronized boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        img = combineComposite(img);
        return wrapped.drawImage(img, xform, obs);
    }

    @Override
    public synchronized void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        img = (BufferedImage) combineComposite(img);
        wrapped.drawImage(img, op, x, y);
    }
    
    @Override
    public synchronized boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        img = combineComposite(img);
        return wrapped.drawImage(img, x, y, observer);
    }

    @Override
    public synchronized boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        img = combineComposite(img);
        return wrapped.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public synchronized boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        img = combineComposite(img);
        return wrapped.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public synchronized boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        img = combineComposite(img);
        return wrapped.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public synchronized boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        img = combineComposite(img);
        return wrapped.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public synchronized boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        img = combineComposite(img);
        return wrapped.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public synchronized void dispose() {
        wrapped.dispose();
    }
    
    private final WeakHashSet<BufferedImage> done = WeakHashSet.newInstance(BufferedImage.class);
    
    /**
     * Itext does not support composite on image.
     * Here we generate another image with the alpha composite already combine in the image.
     */
    private Image combineComposite(Image img){
        
        if(img == null || done.contains(img)){
            //already done
            return img;
        }
        
        final Composite composite = this.getComposite();
        
        if(composite instanceof AlphaComposite){
            final AlphaComposite alphaComposite = (AlphaComposite) composite;
            if(alphaComposite.getAlpha() != 1){
                final BufferedImage buffer = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                final Graphics2D g = buffer.createGraphics();
                g.setComposite(alphaComposite);
                g.drawImage(img, 0, 0, null);
                g.dispose();
                done.add(buffer);
                img = buffer;
            }
        }
        
        return img;
    }
    
}
