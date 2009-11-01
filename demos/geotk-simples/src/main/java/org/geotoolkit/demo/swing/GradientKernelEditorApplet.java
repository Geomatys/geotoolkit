/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.geotoolkit.demo.swing;

import javax.swing.JApplet;

import org.geotoolkit.gui.swing.image.GradientKernelEditor;


/**
 * Display a {@link GradientKernelEditor} in an applet.
 */
@SuppressWarnings("serial")
public class GradientKernelEditorApplet extends JApplet {
    /**
     * Initialization method that will be called after the applet is loaded into the browser.
     * This method creates a new applet showing a {@link GradientKernelEditor} initialized to
     * default values.
     */
    @Override
    public void init() {
        final GradientKernelEditor editor = new GradientKernelEditor();
        editor.addDefaultKernels();
        add(editor);
    }
}
