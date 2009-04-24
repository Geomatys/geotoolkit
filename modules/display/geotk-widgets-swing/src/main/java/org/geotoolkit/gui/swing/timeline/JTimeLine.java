package org.geotoolkit.gui.swing.timeline;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author johann sorel
 */
public class JTimeLine extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    public static final String CENTRAL_DATE_PROPERTY = "centralDate";
    public static final String SELECTION_PROPERTY = "selection";
    
    
    private final HashMap<TimeLineItem, Integer> items = new HashMap<TimeLineItem, Integer>();
    private final List<TimeLineItem> sorted = new ArrayList<TimeLineItem>();
    private static final long YEAR_IN_MILLI = (long) (365f * 24f * 60f * 60f * 1000f);
    private float zoom = 0.5f;
    //temp factor : for zoom = 1, we will see factor year on the component width
    private float factor = 10;
    private final float topHeight = 0.60f;
    private final float lastHeight = 0.20f;
    private final Color Cseparator = Color.WHITE;
    private final Color Cbase = Color.GRAY;
    private final Color Ctop;
    private final Color Ccenter;
    private final Color Clast;
    private int height = 0;
    private int width = 0;
    private int limit1 = 0;
    private int limit2 = 0;
    private int scrollMargin = 10;
    private int scrollThickness = 20;
    private int scrollStartX = 0;
    private int scrollStartY = 0;
    private int scrollHeight = 0;
    private int zoomPosition = 0;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private int newMouseX = 0;
    private int newMouseY = 0;
    private boolean flagScroll = false;
    
    private Date centralDate = new Date();
    private GregorianCalendar centralGregorian = new GregorianCalendar();
    private TimeLineItem selectedItem = null;

    public JTimeLine() {
        
        Ctop = Cbase.brighter();
        Ccenter = Cbase;
        Clast = Cbase.darker();

        centralGregorian.setTime(centralDate);

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {

        if (zoom >= 0 && zoom <= 1) {
            this.zoom = zoom;
            repaint();
        } else if (zoom > 1) {
            this.zoom = 1;
            repaint();
        }

    }

    public void setFactor(float factor){
        this.factor = factor;
    }
    
    public Date getCentralDate() {
        return centralDate;
    }

    public void setCentralDate(Date centralDate) {
        
        if(this.centralDate != centralDate){
            Date old = this.centralDate;
            this.centralDate = centralDate;
            centralGregorian.setTime(centralDate);
            repaint();
            firePropertyChange(CENTRAL_DATE_PROPERTY, old, this.centralDate);
        }
    }

    public TimeLineItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(TimeLineItem selectedItem) {
        if(this.selectedItem != selectedItem){
            TimeLineItem old = this.selectedItem;
            this.selectedItem = selectedItem;
            repaint();
            firePropertyChange(SELECTION_PROPERTY, old, this.selectedItem);
        }
    }

    public Date getDateAt(int x) {
        return null;
    }

    private int getPosition(Date t) {
        int position = -1;

        float center = width / 2f;
        long centerMili = centralGregorian.getTimeInMillis();
        long diff = centerMili - t.getTime();

        width = getWidth();
        float extend = YEAR_IN_MILLI * factor * zoom;
        float timeByPixel = extend / width;

        float f = center - diff / timeByPixel;

        return Math.round(f);
    }

    public void addItem(TimeLineItem item) {
        this.items.put(item, getPosition(item.getDate()));
        sorted.add(item);
        repaint();
    }

    public void addItems(Collection<TimeLineItem> items) {

        for (TimeLineItem item : items) {
            this.items.put(item, getPosition(item.getDate()));
        }
        sorted.addAll(items);
        repaint();
    }

    public void removeItem(TimeLineItem item) {
        items.remove(item);
        sorted.remove(item);
        
        if(item == selectedItem){
            setSelectedItem(null);
        }
        
        repaint();
    }

    public void removeItems(Collection<TimeLineItem> items) {

        for (TimeLineItem item : items) {
            this.items.remove(item);
            
            if(item == selectedItem){
                setSelectedItem(null);
            }
        }
        sorted.removeAll(items);
        repaint();
    }

    public void clear() {
        if (!items.isEmpty()) {
            items.clear();
            sorted.clear();
            setSelectedItem(null);
            repaint();
        }
    }

    public Collection<TimeLineItem> getItems() {
        return items.keySet();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        height = getHeight();
        width = getWidth();
        limit1 = (int) (height * topHeight);
        limit2 = height - (int) (height * lastHeight);

        paintYears(g2, height, width, limit1, limit2);
        paintMonths(g2, height, width, limit1, limit2);
        paintDays(g2, height, width, limit1, limit2);




        paintGraduation(g2, height, width);
    }

    private void paintYears(Graphics2D g2, int height, int width, int limit1, int limit2) {
        float pxYear = ((float) width) / (((float) factor) * zoom);

        g2.setColor(Clast);
        g2.fillRect(0, limit2, width, height - limit2);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(Cseparator);
        g2.drawLine(0, limit2, width, limit2);

        //draw a gradiant
        GradientPaint mask;
        mask = new GradientPaint(
                0, 0, new Color(1.0f, 1.0f, 1.0f, 1f),
                0, height, new Color(1.0f, 1.0f, 1.0f, 0.0f));
        g2.setPaint(mask);
        g2.fillRect(0, limit2, width, height - limit2);


        Calendar calendar = new GregorianCalendar();
        calendar.setTime(centralDate);


        int nbYears = (int) (((float) width / 2f) / pxYear) + 1;

        //draw the years
        g2.setColor(Ccenter);
        g2.setFont(new Font("Serif", Font.BOLD, 12));
        calendar.set(centralGregorian.get(Calendar.YEAR), 0, 1, 1, 1);
        calendar.add(Calendar.YEAR, -nbYears);

        for (int y = -nbYears; y <= nbYears; y++, calendar.add(Calendar.YEAR, 1)) {
            int x = getPosition(calendar.getTime());

            if (x >= 0) {
                g2.setColor(Ccenter);
                g2.drawLine(x, limit2 + 1, x, height);
                paintString(g2, String.valueOf(calendar.get(Calendar.YEAR)), Ccenter, x + 5, height - 5);
            } else if ((pxYear + x) > 40) {
                paintString(g2, String.valueOf(calendar.get(Calendar.YEAR)), Ccenter, 5, height - 5);
            }
        }
    }

    private void paintMonths(Graphics2D g2, int height, int width, int limit1, int limit2) {
        float pxMonth = ((float) width) / (((float) factor) * zoom) / 12f;

        g2.setColor(Ccenter);
        g2.fillRect(0, limit1, width, limit2 - limit1);

        paintItems(g2, limit1 + 1, limit2 - 1, true);


        //draw a gradiant
        GradientPaint mask;
        mask = new GradientPaint(
                0, limit1, new Color(1.0f, 1.0f, 1.0f, 0.1f),
                0, limit2, new Color(Ccenter.getRed(), Ccenter.getGreen(), Ccenter.getBlue(), 255));
        g2.setPaint(mask);
        g2.fillRect(0, limit1, width, limit2 - limit1);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(Cseparator);
        g2.drawLine(0, limit1, width, limit1);


        Calendar calendar = new GregorianCalendar();
        calendar.setTime(centralDate);

        if (pxMonth > 28) {
            int nbMonth = (int) (((float) width / 2f) / pxMonth) + 1;

            //draw the months
            g2.setColor(Ctop);
            g2.setFont(new Font("Serif", Font.PLAIN, 12));
            calendar.set(centralGregorian.get(Calendar.YEAR), centralGregorian.get(Calendar.MONTH) + 1, 1, 1, 1);
            calendar.add(Calendar.MONTH, -nbMonth);

            for (int y = -nbMonth; y <= nbMonth; y++, calendar.add(Calendar.MONTH, 1)) {
                int x = getPosition(calendar.getTime());

                if (x >= 0) {
                    g2.setColor(Ctop);
                    g2.drawLine(x, limit1 + 1, x, limit2 - 1);

                    if (pxMonth < 60) {
                        paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, getLocale()), Ctop, x + 5, limit2 - 5);
                    } else if (pxMonth >= 60) {
                        paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getLocale()), Ctop, x + 5, limit2 - 5);
                    }
                } else if ((pxMonth + x) >= 60) {
                    paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getLocale()), Ctop, 5, limit2 - 5);
                } else if ((pxMonth + x) > 30) {
                    paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, getLocale()), Ctop, 5, limit2 - 5);
                }
            }
        }
    }

    private void paintDays(Graphics2D g2, int height, int width, int limit1, int limit2) {
        float pxDay = ((float) width) / (((float) factor) * zoom) / 365f;

        g2.setColor(Ctop);
        g2.fillRect(0, 0, width, limit1);


        //paint items
        if (pxDay > 22) {
            paintItems(g2, 1, limit1 - 20, false);
        } else {
            paintItems(g2, 1, limit1 - 1, false);
        }

        //draw a gradiant
        GradientPaint mask;
        mask = new GradientPaint(
                0, 0, new Color(1.0f, 1.0f, 1.0f, 0.8f),
                0, height, new Color(1.0f, 1.0f, 1.0f, 0.0f));
        g2.setPaint(mask);
        g2.fillRect(0, 0, width, limit1);


        Calendar calendar = new GregorianCalendar();
        calendar.setTime(centralDate);

        if (pxDay > 22) {
            int nbDays = (int) (((float) width / 2f) / pxDay) + 1;

            //draw the days
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Serif", Font.PLAIN, 12));
            calendar.set(centralGregorian.get(Calendar.YEAR), centralGregorian.get(Calendar.MONTH), centralGregorian.get(Calendar.DAY_OF_MONTH), 1, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -nbDays);

            for (int y = -nbDays; y <= nbDays; y++, calendar.add(Calendar.DAY_OF_MONTH, 1)) {
                int x = getPosition(calendar.getTime());

                if (x >= 0) {
                    g2.setColor(Color.WHITE);
                    g2.drawLine(x, 0, x, limit1 - 1);

                    if (pxDay < 60) {
                        paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, x + 5, limit1 - 5);
                    } else if (pxDay >= 60) {
                        paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, x + 5, limit1 - 5);
                    }
                } else if ((pxDay + x) >= 60) {
                    paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, 5, limit1 - 5);
                } else if ((pxDay + x) > 30) {
                    paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, 5, limit1 - 5);
                }
            }
        }


    }

    private void paintGraduation(Graphics2D g2, int height, int width) {

        scrollStartX = width - scrollThickness - scrollMargin;
        scrollHeight = height - 2 * scrollMargin - scrollThickness;
        scrollStartY = scrollMargin + scrollThickness / 2;
        zoomPosition = (int) (scrollHeight * zoom) + scrollStartY;

        GradientPaint mask;
        mask = new GradientPaint(scrollStartX, 0, new Color(Clast.getRed(), Clast.getGreen(), Clast.getBlue(), 220),
                scrollStartX + scrollThickness, 0, new Color(Clast.getRed(), Clast.getGreen(), Clast.getBlue(), 0));
        g2.setPaint(mask);

        //draw the caps
        g2.fillArc(scrollStartX, scrollMargin, scrollThickness, scrollThickness, 0, 180);
        g2.fillArc(scrollStartX, height - scrollThickness - scrollMargin, scrollThickness, scrollThickness, 0, -180);

        //draw the fill
        g2.fillRect(scrollStartX, scrollStartY, scrollThickness, scrollHeight);

        //draw zoom        
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g2.setColor(Cseparator);
        g2.drawLine(scrollStartX, zoomPosition, scrollStartX + scrollThickness - 1, zoomPosition);

    }

    private void paintItems(Graphics2D g2, int ymin, int ymax, boolean simple) {

        Collections.sort(sorted);

        int n = 0;
        int selectedN = 0;

        for (TimeLineItem item : sorted) {
            items.put(item, getPosition(item.getDate()));
            int x = items.get(item);
            n = (n > limit1 - 36) ? 0 : n + 6;

            //if selectedItem,
            //we save n value and move on to next item
            if (item == selectedItem) {
                selectedN = n;
                continue;
            }

            if (x >= -8 && x < width + 8) {
                if (!simple && item.getImage() != null) {
                    g2.drawImage(item.getImage(), x - 8, n, this);
                } else if (item.getImage() == null) {
                    g2.setColor(item.getColor());
                    g2.drawLine(x, ymin, x, ymax);
                }
            }
        }

        //draw selectedItem above all other ------------------------------------
        if (selectedItem != null) {
            int x = items.get(selectedItem);

            if (x >= -8 && x < width + 8) {
                Stroke def = g2.getStroke();
                g2.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
                if (!simple && selectedItem.getImage() != null) {

                    if (selectedItem.getSelectedImage() != null) {
                        g2.drawImage(selectedItem.getSelectedImage(), x - 8, selectedN, this);
                    } else {
                        g2.drawImage(selectedItem.getImage(), x - 8, selectedN, this);
                        g2.setColor(Color.RED);
                        g2.drawRect(x - 9, selectedN - 1, 18, 18);
                    }

                } else if (selectedItem.getImage() == null) {
                    g2.setColor(Color.RED);
                    g2.drawLine(x, ymin, x, ymax);
                }
                g2.setStroke(def);
            }
        }


    }

    private void paintString(Graphics2D g2, String txt, Color color, int x, int y) {
        g2.setColor(color.darker().darker());
        g2.drawString(txt, x + 1, y + 1);
        g2.setColor(color.brighter());
        g2.drawString(txt, x, y);
    }

    private float isInScrollArea(int x, int y) {

        if (x >= scrollStartX &&
                x < (scrollStartX + scrollThickness) &&
                y >= scrollStartY &&
                y < (scrollStartY + scrollHeight)) {

            float v = y - scrollStartY;
            return v / scrollHeight;
        }

        return -1;
    }

    public void mouseClicked(MouseEvent e) {

        if (e.getY() < limit1) {
            Collections.sort(sorted);

            for (int i = sorted.size() - 1; i >= 0; i--) {
                TimeLineItem item = sorted.get(i);

                if (item != selectedItem) {
                    int x = items.get(item);
                    if (x >= e.getX() - 2 && x <= e.getX() + 2) {
                        setSelectedItem(item);
                        break;
                    }
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        newMouseX = e.getX();
        newMouseY = e.getY();

        float test = isInScrollArea(newMouseX, newMouseY);
        if (test > 0) {
            flagScroll = true;
            setZoom(test);
        }

        lastMouseX = newMouseX;
        lastMouseY = newMouseY;
    }

    public void mouseReleased(MouseEvent e) {
        flagScroll = false;
    }

    public void mouseEntered(MouseEvent e) {
        requestFocus();
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        newMouseX = e.getX();
        newMouseY = e.getY();

        float test = isInScrollArea(newMouseX, newMouseY);
        if (flagScroll && test > 0) {
            setZoom(test);
        } else {
            width = getWidth();
            float extend = YEAR_IN_MILLI * factor * zoom;
            float timeByPixel = extend / width;

            int mouseDiff = lastMouseX - newMouseX;
            float diff = mouseDiff * timeByPixel;
            long nd = (long) (centralDate.getTime() + diff);
            Date date = new Date(nd);
            setCentralDate(date);

        //mouse zoom en vertical axe
//            int mouseydiff = lastMouseY - newMouseY;
//            System.out.println(mouseydiff);
//            if(mouseydiff > 0) {
//                float v = (float) (getZoom() * 1.02f ) ;
//                System.out.println( Math.pow(1.1f, mouseydiff) +" "+ v);
//                setZoom(v);
//            }else if(mouseydiff < 0) {
//                float v = (float) (getZoom() * 0.98f );
//                System.out.println( Math.pow(0.9f, mouseydiff) +" "+ v);
//                setZoom(v);
//            }

        }

        lastMouseX = newMouseX;
        lastMouseY = newMouseY;
    }

    public void mouseMoved(MouseEvent e) {

        if (isInScrollArea(e.getX(), e.getY()) > 0) {
            setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            if (e.getY() < limit1) {

                for (TimeLineItem item : items.keySet()) {
                    int x = items.get(item);

                    if (x >= e.getX() - 1 && x <= e.getX() + 1) {

                        setToolTipText(item.getToolTip());
                        break;
                    }
                    setToolTipText(null);
                }
            } else {
                setToolTipText(null);
            }


        }

    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() > 0) {
            setZoom(getZoom() * 1.1f);
        } else {
            setZoom(getZoom() * 0.9f);
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            centralGregorian.add(Calendar.DAY_OF_MONTH, (int) (30 * zoom) + 1);
            setCentralDate(centralGregorian.getTime());
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            centralGregorian.add(Calendar.DAY_OF_MONTH, -(int) (30 * zoom) - 1);
            setCentralDate(centralGregorian.getTime());
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            setZoom(getZoom() * 0.9f);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            setZoom(getZoom() * 1.1f);
        }
    }

    public void keyReleased(KeyEvent e) {
    }
}
