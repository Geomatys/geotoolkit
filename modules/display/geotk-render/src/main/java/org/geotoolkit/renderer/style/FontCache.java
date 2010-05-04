/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.renderer.style;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lookup and caches font definitions for faster retrieval
 * 
 * @author Andrea Aime - TOPP
 * 
 * @module pending
 */
public class FontCache {
    /** The logger for the rendering module. */
    private static final Logger LOGGER = org.geotoolkit.util.logging.Logging
            .getLogger("org.geotoolkit.renderer.style");

    private static FontCache defaultInstance;

    /** Set containing the font families known of this machine */
    private Set<String> fontFamilies = null;

    /** Fonts already loaded */
    private Map<String, Font> loadedFonts = new HashMap<String, Font>();

    /**
     * Returns the default, system wide font cache
     */
    public static FontCache getDefaultInsance() {
        if (defaultInstance == null) {
            defaultInstance = new FontCache();
        }
        return defaultInstance;
    }

    public synchronized Font getFont(String requestedFont) {
        // make sure we load the known font families once
        if (fontFamilies == null) {
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            fontFamilies = new HashSet<String>(Arrays.asList(ge.getAvailableFontFamilyNames()));

            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("there are " + fontFamilies.size() + " fonts available");
            }
        }

        // see if the font has already been loaded
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("trying to load " + requestedFont);
        }

        if (loadedFonts.containsKey(requestedFont)) {
            return loadedFonts.get(requestedFont);
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("not already loaded");
        }

        // if not, try to load from the java runtime or as an URL
        final Font javaFont;
        if (fontFamilies.contains(requestedFont)) {
            javaFont = new Font(requestedFont, Font.PLAIN, 12);
        } else {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("not a system font");
            }
            javaFont = loadFromUrl(requestedFont); 
        }
        
        // log the result and exit
        if(javaFont == null) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Could not load font " + requestedFont);
            }
        } else {
            loadedFonts.put(requestedFont, javaFont);
        }
        return javaFont;
    }

    /**
     * Tries to load the specified font name as a URL
     * @param fontUrl
     * @return
     */
    Font loadFromUrl(String fontUrl) {
        // may be its a file or url
        InputStream is = null;

        if (fontUrl.startsWith("http") || fontUrl.startsWith("file:")) {
            try {
                URL url = new URL(fontUrl);
                is = url.openStream();
            } catch (MalformedURLException mue) {
                // this may be ok - but we should mention it
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("Bad url in SLDStyleFactory " + fontUrl + "\n" + mue);
                }
            } catch (IOException ioe) {
                // we'll ignore this for the moment
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("IO error in SLDStyleFactory " + fontUrl + "\n" + ioe);
                }
            }
        } else {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("not a URL");
            }

            File file = new File(fontUrl);

            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException fne) {
                // this may be ok - but we should mention it
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("Bad file name in SLDStyleFactory" + fontUrl + "\n" + fne);
                }
            }
        }

        // make sure we have anything to load
        if (is == null) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("null input stream, could not load the font");
            }
            return null;
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("about to load");
        }

        try {
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException ffe) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Font format error in SLDStyleFactory " + fontUrl + "\n" + ffe);
            }
            return null;
        } catch (IOException ioe) {
            // we'll ignore this for the moment
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("IO error in SLDStyleFactory " + fontUrl + "\n" + ioe);
            }
            return null;
        }
    }
    
}
