/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.internal;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.util.ArgumentChecks;
import org.controlsfx.dialog.ExceptionDialog;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.StyleFactory;
import org.opengis.util.InternationalString;

/**
 * Internalization, IconBundle, JavaFX utilities for geotoolkit javafx widgets.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GeotkFX {


    public static final Image ICON_STYLE     = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_BOOK,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_FTS       = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_TAG,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_RULE      = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FILTER,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_NEW       = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PLUS,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_DUPLICATE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_COPY,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_DELETE    = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_TRASH,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_MOVEUP    = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_CHEVRON_UP,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_MOVEDOWN  = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_CHEVRON_DOWN,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_SLIDERS   = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_SLIDERS_H,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_FILTER    = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FILTER,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_CALENDAR  = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_CALENDAR,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_EDIT      = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PENCIL_ALT,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_ADD       = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PLUS,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_OPEN      = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FOLDER_OPEN,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_SAVE      = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_SAVE,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_UNLINK    = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_UNLINK,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_UNDO      = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_UNDO,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_MOVE      = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_HAND_PAPER,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    public static final Image ICON_EMPTY     = new WritableImage(16, 16);

    public static final String BUNDLE_PATH = "org/geotoolkit/gui/javafx/internal/Bundle";
    public static final String CSS_PATH = "/org/geotoolkit/gui/javafx/style/StyleEditor.css";
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_PATH,Locale.getDefault(),GeotkFX.class.getClassLoader());
    private static FilterFactory2 FF;
    private static MutableStyleFactory SF;

    public synchronized static FilterFactory2 getFilterFactory(){
        if(FF==null)FF = (FilterFactory2) DefaultFactories.forBuildin(FilterFactory.class);
        return FF;
    }

    public synchronized static MutableStyleFactory getStyleFactory(){
        if(SF==null)SF = (MutableStyleFactory) DefaultFactories.forBuildin(StyleFactory.class);
        return SF;
    }

    /**
     * Get the local string for the given class and key.
     * The object class name will be pre-concatenate with the key.
     *
     * @param base base class
     * @param key Bundle key
     * @return I18N String
     */
    public static String getString(Object base, final String key){
        return getString(base.getClass(), key);
    }

    /**
     * Get the local string for the given class and key.
     * The class name will be pre-concatenate with the key.
     *
     * @param clazz base class
     * @param key Bundle key
     * @return I18N String
     */
    public static String getString(Class clazz, final String key){
        try{
            return BUNDLE.getString(clazz.getName()+"."+key);
        }catch(MissingResourceException ex){
            return "Missing key : "+key;
        }
    }

    /**
     * Get the local string for the given key.
     *
     * @param key Bundle key
     * @return I18N String
     */
    public static String getString(final String key){
        try{
            return BUNDLE.getString(key);
        }catch(MissingResourceException ex){
            return "Missing key : "+key;
        }
    }

    /**
     * Get I18N String with parameters.
     *
     * @param key Bundle key
     * @param obj1 first argument
     * @return I18N String
     */
    public static String getString(final String key, Object obj1){
        return getString(key, new Object[]{obj1});
    }

    /**
     * Get I18N String with parameters.
     *
     * @param key Bundle key
     * @param objects arguments
     * @return I18N String
     */
    public static String getString(final String key, final Object[] objects){
        String text = getString(key);
        String pattern;
        for (int i = 0; i < objects.length; i++) {
            pattern = "{"+i+"}";
            if (text.contains(pattern)) {
                text = text.replace(pattern, objects[i].toString());
            }
        }
        return text;
    }

    /**
     * Get I18N String.
     *
     * @param key Bundle key
     * @return I18N String
     */
    public static InternationalString getI18NString(final String key){
        try{
            String text = BUNDLE.getString(key);
            if(text.startsWith("$")){
                return getI18NString(text.substring(1));
            }
        }catch(MissingResourceException ex){
            throw new RuntimeException("Missing resource key : "+key,ex);
        }
        return new ClassLoaderInternationalString(GeotkFX.class,BUNDLE_PATH, key);
    }

    public static InternationalString getI18NString(Class clazz, final String key){
        return getI18NString(clazz.getName()+"."+key);
    }

    public static final BufferedImage EMPTY_ICON_16;
    static {
        try {
            EMPTY_ICON_16 = ImageIO.read(GeotkFX.class.getResource("/org/geotoolkit/gui/javafx/icon/empty16.png"));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public static BufferedImage getBufferedImage(final String key) {
        try {
            return ImageIO.read(GeotkFX.class.getResourceAsStream("/org/geotoolkit/gui/javafx/icon/"+key+".png"));
        } catch (IOException ex) {
            Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            return null;
        }
    }

    public static BufferedImage getBufferedImage(final String key, Dimension resize) {
        try {
            final BufferedImage img = ImageIO.read(GeotkFX.class.getResourceAsStream("/org/geotoolkit/gui/javafx/icon/"+key+".png"));

            final BufferedImage resized = new BufferedImage(resize.width, resize.height, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(img, 0, 0, resize.width, resize.height, null);
            g.dispose();

            return resized;

        } catch (IOException ex) {
            Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            return null;
        }
    }

    public static Image getImage(final String key) throws IOException{
       return SwingFXUtils.toFXImage(getBufferedImage(key), null);
    }

    /**
     * Load and initialize geotk bundle,css,loader for given object.
     *
     * @param candidate widget to load JRXML in
     * @param cdtClass class as base for resource classloader
     */
    public static void loadJRXML(Parent candidate, Class cdtClass) {
        loadJRXML(candidate, cdtClass, true);
    }

    public static void loadJRXML(Parent candidate, Class cdtClass, boolean loadDefaultCSS) {
        loadJRXML(candidate, cdtClass, loadDefaultCSS, null);
    }

    public static void loadJRXML(Parent candidate, Class cdtClass, boolean loadDefaultCSS, ResourceBundle bundle) {
        final String fxmlpath = "/"+cdtClass.getName().replace('.', '/')+".fxml";
        final FXMLLoader loader = new FXMLLoader(cdtClass.getResource(fxmlpath));
        loader.setResources(bundle==null ? GeotkFX.BUNDLE : bundle);
        loader.setController(candidate);
        loader.setRoot(candidate);
        //in special environement like osgi or other, we must use the proper class loaders
        //not necessarly the one who loaded the FXMLLoader class
        loader.setClassLoader(cdtClass.getClassLoader());
        try {
            loader.load();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        if (loadDefaultCSS) {
            candidate.getStylesheets().add(GeotkFX.CSS_PATH);
        }
    }

    public static ExceptionDialog newExceptionDialog(final String headerText, final Throwable t) {
        ArgumentChecks.ensureNonNull("Exception to display", t);
        ExceptionDialog d = new ExceptionDialog(t);
        d.setResizable(true);
        if (headerText != null) {
            d.setHeaderText(headerText);
        }
        d.setWidth(400);
        d.setHeight(500);
        return d;
    }

    private GeotkFX(){}

    /**
     * Binding which checks if the given object is an instance of given class.
     * Binding value is null otherwise.
     *
     * @param <T> binding class
     * @param value observed value
     * @param clazz binding class
     * @return binding object
     */
    public static <T> ObjectBinding<T> isInstance(ObservableValue<?> value, Class<T> clazz){
        return new IsInstanceBinding<>(value,clazz);
    }

    private static class IsInstanceBinding<T> extends ObjectBinding<T>{

        private final ObservableValue value;
        private final Class clazz;

        private IsInstanceBinding(ObservableValue value, Class clazz){
            this.value = value;
            this.clazz = clazz;
            bind(value);
        }

        @Override
        protected T computeValue() {
            final Object candidate = value.getValue();
            return clazz.isInstance(candidate) ? (T)candidate : null;
        }

    }

}
