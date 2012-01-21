/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.swing.filestore;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.util.logging.Logging;

/**
 * Chooser for image files.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JFileCoverageChooser extends JPanel{
    
    private static final Logger LOGGER = Logging.getLogger(JFileCoverageChooser.class);
    private static volatile File lastPath = null;
    private static volatile FileFilter lastFilter = null;
    private final Map<FileFilter, ImageReaderSpi> filterMap;
    private final JFileChooser guiChooser = new JFileChooser();
    
    public JFileCoverageChooser() {
        this(null);
    }

    /**
     * Creates new form panel, similar to a JFileChooser panel.
     * @param openPath : default path to open
     */
    public JFileCoverageChooser(final File openPath) {
        setLayout(new BorderLayout());

        guiChooser.setControlButtonsAreShown(false);
        guiChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        add(BorderLayout.CENTER,guiChooser);

        // get all file filters
        filterMap = getFilterList();
        
        for (FileFilter ff : filterMap.keySet()) {
            guiChooser.addChoosableFileFilter(ff);
        }

        guiChooser.setMultiSelectionEnabled(true);

        //restore last path and file chooser -----------------------------------
        final FileFilter lastf = lastFilter;
        if (lastf != null) {
            guiChooser.setFileFilter(lastf);
        }

        if (openPath != null) {
            guiChooser.setCurrentDirectory(openPath);
            lastPath = openPath;
        } else {
            final File lastp = lastPath;
            if (lastp != null) {
                guiChooser.setCurrentDirectory(lastp);
            }
        }

    }

    /**
     * Go to the given directory.
     */
    public void setDirectory(final File directory) {
        guiChooser.setCurrentDirectory(directory);
    }

    /**
     * Get the current directory.
     */
    public File getDirectory() {
        return guiChooser.getCurrentDirectory();
    }

    private Map<FileFilter,ImageReaderSpi> getFilterList(){
        //find all image readers and create a file filter for each.
        final Map<FileFilter,ImageReaderSpi> filters = new TreeMap<FileFilter, ImageReaderSpi>(
            new Comparator<FileFilter>() {
                @Override
                public int compare(FileFilter o1, FileFilter o2) {
                    return o1.getDescription().compareTo(o2.getDescription());
                }
        });

        final Locale locale = Locale.getDefault();
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        final Iterator<? extends ImageReaderSpi> it = registry.getServiceProviders(ImageReaderSpi.class, true);
        final Map<String,String> suffixDone = new HashMap<String,String>();
        final Set<String> formatsDone = new HashSet<String>();
        final StringBuilder buffer = new StringBuilder();

        skip:
        while (it.hasNext()) {
            final ImageReaderSpi spi = it.next();

            String longFormat = null;
            for (final String format : spi.getFormatNames()) {
                if (!formatsDone.add(format)) {
                    // Avoid declaring the same format twice (e.g. declaring
                    // both the JSE and JAI ImageReaders for the PNG format).
                    continue skip;
                }
                // Remember the longuest format string. If two of them
                // have the same length, favor the one in upper case.
                longFormat = longest(longFormat, format);
            }
            if (longFormat == null) {
                longFormat = spi.getDescription(locale);
            }
            /*
             * At this point, we have a provider to take in account. We need to get the list of
             * suffixes, but we don't need both the lower-case and upper-case flavors of the same
             * suffix. If those two flavors exist, then we will keep only the first one (which is
             * usually the lower-case flavor). The iteration is performed in reverse order for that
             * reason.
             */
            String[] suffix = spi.getFileSuffixes();
            for (int i=suffix.length; --i >= 0;) {
                final String s = suffix[i].trim();
                if (s.length() != 0) {
                    suffixDone.put(s.toLowerCase(locale), s);
                }
            }
            if (!suffixDone.isEmpty()) {
                suffix = suffixDone.values().toArray(new String[suffixDone.size()]);
                suffixDone.clear();
                buffer.setLength(0);
                
                //try to find it it's a spatial format
                try{
                    final ImageReader reader = spi.createReaderInstance();
                    if(reader instanceof SpatialImageReader){
                        buffer.append("[").append(MessageBundle.getString("geographic")).append("] ");
                    }else{
                        buffer.append("[").append(MessageBundle.getString("standard")).append("] ");
                    }
                    reader.dispose();
                }catch(Exception ex){
                    //not important
                    buffer.setLength(0);
                    buffer.append("[").append(MessageBundle.getString("standard")).append("] ");
                }
                
                buffer.append(longFormat);
                String separator = "  (";
                for (final String s : suffix) {
                    buffer.append(separator).append("*.").append(s);
                    separator = ", ";
                }
                buffer.append(')');
                final FileFilter filter = new FileNameExtensionFilter(buffer.toString(), suffix);
                filters.put(filter, spi);
            }
        }

        return filters;
    }
    
    
    private Map<File, ImageReaderSpi> getSources() {
        final Map<File, ImageReaderSpi> map = new HashMap<File, ImageReaderSpi>();
        final FileFilter ff = guiChooser.getFileFilter();
        final ImageReaderSpi spi = filterMap.get(ff);
        
        for(File f : guiChooser.getSelectedFiles()){
            if(f.isDirectory() || !f.exists()){
                continue;
            }
            try {
                if(spi.canDecodeInput(f)){
                    map.put(f, spi);
                }
            } catch (IOException ex) {
            }
        }
        return map;
    }
    
    /**
     * Selects the longest format string. If two of them
     * have the same length, favor the one in upper case.
     *
     * @param current    The previous longest format string, or {@code null} if none.
     * @param candidate  The format string which may be longer than the previous one.
     * @return The format string which is the longest one up to date.
     */
    private static String longest(final String current, final String candidate) {
        if (current != null) {
            final int dl = candidate.length() - current.length();
            if (dl < 0 || (dl == 0 && candidate.compareTo(current) >= 0)) {
                return current;
            }
        }
        return candidate;
    }
    
    public static Map<File,ImageReaderSpi> showDialog(){
        final JFileCoverageChooser chooser = new JFileCoverageChooser();
        final JDialog dialog = new JDialog();
        
        final JToolBar bar = new JToolBar();
        bar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bar.setFloatable(false);
        bar.add(new AbstractAction(MessageBundle.getString("open")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.CENTER,chooser);        
        panel.add(BorderLayout.SOUTH, bar);
        dialog.setModal(true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return chooser.getSources();
    }
    
}
