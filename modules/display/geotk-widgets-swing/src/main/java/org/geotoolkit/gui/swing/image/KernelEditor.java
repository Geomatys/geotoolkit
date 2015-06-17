/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.gui.swing.image;

import javax.media.jai.KernelJAI;
import javax.media.jai.operator.ConvolveDescriptor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.IllegalComponentStateException;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataListener;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Locale;
import java.text.ParseException;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.gui.swing.Dialog;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.swing.SwingUtilities;

import static java.awt.GridBagConstraints.*;


/**
 * A widget for selecting and/or editing a {@link KernelJAI} object. Kernels are used for
 * {@linkplain ConvolveDescriptor image convolutions}. {@code KernelEditor} widgets are
 * initially empty, but a set of default kernels can be added with {@link #addDefaultKernels}
 * including (but not limited to)
 *
 * {@linkplain KernelJAI#ERROR_FILTER_FLOYD_STEINBERG Floyd & Steinberg (1975)},
 * {@linkplain KernelJAI#ERROR_FILTER_JARVIS Jarvis, Judice & Ninke (1976)} and
 * {@linkplain KernelJAI#ERROR_FILTER_STUCKI Stucki (1981)}.
 * <p>
 * Each kernel can belong to an optional category. Example of categories includes
 * "Error filters" and "Gradient masks".
 *
 * <p>&nbsp;</p>
 * <p align="center"><img src="doc-files/KernelEditor.png"></p>
 * <p>&nbsp;</p>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see GradientKernelEditor
 * @see ConvolveDescriptor
 * @see org.geotoolkit.coverage.processing.operation.GradientMagnitude
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class KernelEditor extends JComponent implements Dialog {
    /**
     * The matrix coefficient as a table.
     */
    private final Model model = new Model();

    /**
     * The list of available filter's categories.
     */
    private final JComboBox<String> categorySelector = new JComboBox<>();

    /**
     * The list of available kernels.
     */
    private final JComboBox<String> kernelSelector = new JComboBox<>(model);

    /**
     * The matrix width.
     */
    private final JSpinner widthSelector = new JSpinner();

    /**
     * The matrix height.
     */
    private final JSpinner heightSelector = new JSpinner();

    /**
     * Constructs a new kernel editor. No kernel will be initially shown. The method
     * {@link #setKernel} must be invoked, or the user must performs a selection in
     * a combo box, in order to make a kernel visible.
     */
    public KernelEditor() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        final Vocabulary resources = Vocabulary.getResources(getDefaultLocale());

        categorySelector.addItem(resources.getString(Vocabulary.Keys.All)); // Must be first category
        categorySelector.addItemListener(model);
        widthSelector.   addChangeListener(model);
        heightSelector.  addChangeListener(model);

        final JTable matrixView = new JTable(model);
        matrixView.setTableHeader(null);
        matrixView.setRowSelectionAllowed(false);
        matrixView.setColumnSelectionAllowed(false);

        final GridBagConstraints c = new GridBagConstraints();
        final JPanel predefinedKernels = new JPanel(new GridBagLayout());

        ////////////////////////////////////////////////////
        ////                                            ////
        ////    Put combo box for predefined kernels    ////
        ////                                            ////
        ////////////////////////////////////////////////////
        c.gridx=0; c.fill=HORIZONTAL;
        c.gridy=2; predefinedKernels.add(new JLabel(resources.getLabel(Vocabulary.Keys.Category), JLabel.RIGHT ), c);
        c.gridy=3; predefinedKernels.add(new JLabel(resources.getLabel(Vocabulary.Keys.Kernel),   JLabel.RIGHT ), c);

        c.gridx=1; c.weightx=1; c.insets.left=0;
        c.gridy=2; predefinedKernels.add(categorySelector, c);
        c.gridy=3; predefinedKernels.add(kernelSelector,   c);

        c.gridx=0; c.gridy=2; c.gridwidth=REMAINDER; add(predefinedKernels, c);
        predefinedKernels.setBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.PredefinedKernels)),
            BorderFactory.createEmptyBorder(/*top*/3,/*left*/9,/*bottom*/6,/*right*/6)));


        //////////////////////////////////////////////
        ////                                      ////
        ////    Put spinners for kernel's size    ////
        ////                                      ////
        //////////////////////////////////////////////
        c.weightx=0; c.gridwidth=1; c.insets.bottom=3;
        c.gridy=0; add(new JLabel(    resources.getLabel (Vocabulary.Keys.Size), JLabel.RIGHT), c);
        c.gridx=2; add(new JLabel(' '+resources.getString(Vocabulary.Keys.Lines).toLowerCase()+" \u00D7 ", JLabel.CENTER), c);
        c.gridx=4; add(new JLabel(' '+resources.getString(Vocabulary.Keys.Columns).toLowerCase(), JLabel.LEFT),  c);

        c.weightx=1;
        c.gridx=1; add(heightSelector, c);
        c.gridx=3; add(widthSelector,  c);


        /////////////////////////////////////////////////
        ////                                         ////
        ////    Put table for kernel coefficients    ////
        ////                                         ////
        /////////////////////////////////////////////////
        c.gridx=0; c.gridwidth=REMAINDER; c.fill=BOTH; c.insets.bottom=9;
        c.gridy=1; c.weighty=1; c.insets.top=6; add(new JScrollPane(matrixView), c);
        setPreferredSize(new Dimension(300,220));
    }

    /**
     * Returns the resources for the widget locale.
     */
    final Vocabulary getResources() {
        Locale locale;
        try {
            locale = getLocale();
        } catch (IllegalComponentStateException exception) {
            locale = getDefaultLocale();
        }
        return Vocabulary.getResources(locale);
    }

    /**
     * Add a set of predefined kernels. Default kernels includes (but is not limited to)
     *
     * {@linkplain KernelJAI#ERROR_FILTER_FLOYD_STEINBERG Floyd & Steinberg (1975)},
     * {@linkplain KernelJAI#ERROR_FILTER_JARVIS Jarvis, Judice & Ninke (1976)} and
     * {@linkplain KernelJAI#ERROR_FILTER_STUCKI Stucki (1981)}.
     */
    public void addDefaultKernels() {
        final Vocabulary resources  = getResources();
        final String ERROR_FILTERS  = resources.getString(Vocabulary.Keys.ErrorFilters);
        final String GRADIENT_MASKS = resources.getString(Vocabulary.Keys.GradientMasks);
        addKernel(ERROR_FILTERS,  "Floyd & Steinberg (1975)",      KernelJAI.ERROR_FILTER_FLOYD_STEINBERG);
        addKernel(ERROR_FILTERS,  "Jarvis, Judice & Ninke (1976)", KernelJAI.ERROR_FILTER_JARVIS);
        addKernel(ERROR_FILTERS,  "Stucki (1981)",                 KernelJAI.ERROR_FILTER_STUCKI);
        /*
         * NOTE: Horizontal and vertical sobel masks seems to have been swapped in KernelJAI.
         *       See for example J.J. Simpson (1990) in Remote sensing environment, 33:17-33.
         *       See also some tests in a speadsheet.
         *       Is it an error in JAI 1.1.2 or a misunderstanding of mine?
         */
        addKernel(GRADIENT_MASKS, "Sobel horizontal", KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL);
        addKernel(GRADIENT_MASKS, "Sobel vertical",   KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL);

        addKernel("Sharp 1",   new float[] { 0.0f, -1.0f,  0.0f,
                                            -1.0f,  5.0f, -1.0f,
                                             0.0f, -1.0f,  0.0f});

        addKernel("Sharp 2",   new float[] {-1.0f, -1.0f, -1.0f,
                                            -1.0f,  9.0f, -1.0f,
                                            -1.0f, -1.0f, -1.0f});

        addKernel("Sharp 3",   new float[] { 1.0f, -2.0f,  1.0f,
                                            -2.0f,  5.0f, -2.0f,
                                             1.0f, -2.0f,  1.0f});

        addKernel("Sharp 4"  , new float[] {-1.0f,  1.0f, -1.0f,
                                             1.0f,  1.0f,  1.0f,
                                            -1.0f,  1.0f, -1.0f});

        addKernel("Laplace 1", new float[] { 0.0f, -1.0f,  0.0f,
                                            -1.0f,  4.0f, -1.0f,
                                             0.0f, -1.0f,  0.0f});

        addKernel("Laplace 2", new float[] {-1.0f, -1.0f, -1.0f,
                                            -1.0f,  8.0f, -1.0f,
                                            -1.0f, -1.0f, -1.0f});

        addKernel("Box",       new float[] { 1.0f,  1.0f,  1.0f,
                                             1.0f,  1.0f,  1.0f,
                                             1.0f,  1.0f,  1.0f});

        addKernel("Low pass",  new float[] { 1.0f,  2.0f,  1.0f,
                                             2.0f,  4.0f,  2.0f,
                                             1.0f,  2.0f,  1.0f});

        if (model.getRowCount()*model.getColumnCount() == 0) {
            setKernel("Box");
        }
    }

    /**
     * Adds a 3x3 kernel to the list of available kernels.
     */
    private void addKernel(final String name, final float[] data) {
        double sum = 0;
        for (int i=0; i<data.length; i++) {
            sum += data[i];
        }
        if (sum != 0) {
            for (int i=0; i<data.length; i++) {
                data[i] /= sum;
            }
        }
        addKernel(null, name, new KernelJAI(3,3,data));
    }

    /**
     * Adds a kernel to the list of available kernels. The widget list kernels in the same order
     * they were added, unless {@link #sortKernelNames} has been invoked. Each kernel can belong
     * to an optional category. Example of categories includes "Error filters" and "Gradient masks".
     *
     * @param category The kernel's category name, or {@code null} if none.
     * @param name The kernel name. Kernels will be displayed in alphabetic order.
     * @param kernel The kernel. If an other kernel was registered with the same
     *        name, the previous kernel will be discarded.
     */
    public void addKernel(String category, final String name, final KernelJAI kernel) {
        if (category == null) {
            category = Vocabulary.getResources(getLocale()).getString(Vocabulary.Keys.Others);
        }
        model.addKernel(category, name, kernel);
    }

    /**
     * Adds a new category if not already present.
     */
    private void addCategory(final String category) {
        final ComboBoxModel<String> categories = categorySelector.getModel();
        for (int i=categories.getSize(); --i>=0;) {
            if (category.equals(categories.getElementAt(i))) {
                return;
            }
        }
        categorySelector.addItem(category);
    }

    /**
     * Removes a kernel. If the kernel was the only one in
     * its category, the category is removed as well.
     *
     * @param kernel The kernel to remove.
     */
    public void removeKernel(final KernelJAI kernel) {
        model.removeKernel(kernel);
    }

    /**
     * Removes a kernel by its name. If the kernel was the only
     * one in its category, the category is removed as well.
     *
     * @param kernel The kernel to remove.
     */
    public void removeKernel(final String kernel) {
        removeKernel(model.getKernel(kernel));
    }

    /**
     * Removes all kernels and categories.
     */
    public void removeAllKernels() {
        model.removeAllKernels();
    }

    /**
     * Sets the kernel. The table size will be set to the specified kernel size, add all
     * coefficients will be copied in the table. If the specified kernel matches one of
     * the kernel registered with the {@link #addKernel addKernel} method, then the kernel
     * name and category will be updated according.
     *
     * @param kernel The new kernel.
     */
    public void setKernel(final KernelJAI kernel) {
        model.setKernel(kernel);
        model.findKernelName();
    }

    /**
     * Sets the kernel by its name. It must be one of the name registered with {@link #addKernel}.
     * If {@code name} is not found, then nothing is done.
     *
     * @param name The name of the kernel to select.
     */
    public void setKernel(final String name) {
        kernelSelector.setSelectedItem(name);
        kernelSelector.repaint();
    }

    /**
     * Sets the size of the current kernel.
     *
     * @param width  The number of rows.
     * @param height The number of columns.
     */
    public void setKernelSize(final int width, final int height) {
        model.setKernelSize(height, width); // Inverse argument order.
        model.findKernelName();
    }

    /**
     * Returns the currently edited kernel.
     *
     * @return The edited kernel.
     */
    public KernelJAI getKernel() {
        return model.getKernel();
    }

    /**
     * Returns the category for the current kernel. This is the {@code category} argument
     * given to <code>{@linkplain #addKernel addKernel}(category, name, kernel)</code>, where
     * {@code kernel} is the {@linkplain #getKernel current kernel}.
     *
     * @return The category for the current kernel, or {@code null} if none.
     */
    public String getKernelCategory() {
        // Category at index 0 is "all", which need a special handling.
        return categorySelector.getSelectedIndex() <= 0 ? null :
                  (String) categorySelector.getSelectedItem();
    }

    /**
     * Sort all kernel names according the specified comparator.
     *
     * @param comparator The comparator, or {@code null} for the natural ordering.
     */
    public void sortKernelNames(final Comparator<String> comparator) {
        model.sortKernelNames(comparator);
    }

    /**
     * Returns an array of kernel names in the current category.
     * Changes in the returned array will not affect the {@code KernelEditor} state.
     *
     * @return The name of all kernels in the current category.
     */
    public String[] getKernelNames() {
        return model.getKernelNames().clone();
    }

    /**
     * Returns the list of predefined kernels in the current category. The content of
     * this list will changes every time a kernel is {@linkplain #addKernel added} or
     * {@linkplain #removeKernel(KernelJAI) removed} and every time the user selects a
     * new category. The selected item can change at any time as well, according user action.
     *
     * @return The predefined kernels in the current category.
     */
    public ComboBoxModel<String> getKernelListModel() {
        return model;
    }

    /**
     * Returns the table model containing the current kernel coefficients. The content of this
     * table will changes every time the user select a new predefined kernel, or when the user
     * edit cell values.
     *
     * @return The kernels coefficients.
     */
    public TableModel getKernelTableModel() {
        return model;
    }

    /**
     * The table and list model to use. The list model contains a list of
     * predefined kernels. The table model contains coefficients for the
     * currently selected kernel. This object is also a listener for various
     * events (like changing the size of the table).
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.3
     * @module
     */
    @SuppressWarnings("serial")
    private final class Model extends AbstractTableModel
            implements ComboBoxModel<String>, ChangeListener, ItemListener
    {
        /**
         * Dictionnary of kernels by their name.
         */
        private final Map<String,KernelJAI> kernels = new HashMap<>();

        /**
         * List of categories by kernel's name.
         */
        private final Map<String,String> categories = new LinkedHashMap<>();

        /**
         * {@code true} if the keys into {@link #categories}
         * are sorted according their <em>natural</em> ordering.
         */
        private boolean sorted;

        /**
         * List of kernel names in alphabetical order.
         * This list is constructed only when first needed.
         */
        private String[] names;

        /**
         * Name of the current kernel, or {@code null}
         * if the user is editing a custom kernel.
         */
        private String name;

        /**
         * Array of elements for the current kernel.
         */
        private float[][] elements = new float[0][];

        /**
         * Returns the number of kernels in the list.
         * Used by the combox box of kernel names.
         */
        @Override
        public int getSize() {
            return getKernelNames().length;
        }

        /**
         * Returns the number of rows in the kernel.
         * Used by the table of kernel values.
         */
        @Override
        public int getRowCount() {
            return elements.length;
        }

        /**
         * Returns the number of columns in the model.
         * Used by the table of kernel values.
         */
        @Override
        public int getColumnCount() {
            return (elements.length!=0) ? elements[0].length : 0;
        }

        /**
         * Returns {@code true} regardless of row and column index
         * Used by the table of kernel values.
         */
        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            return true;
        }

        /**
         * Returns {@code Float.class} regardless of column index
         * Used by the table of kernel values.
         */
        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            return Float.class;
        }

        /**
         * Returns the value for the cell at {@code columnIndex} and {@code rowIndex}.
         * This method is automatically invoked in order to paint the kernel as a table.
         */
        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            return Float.valueOf(elements[rowIndex][columnIndex]);
        }

        /**
         * Set the value for the cell at {@code columnIndex} and {@code rowIndex}.
         * This method is automatically invoked when the user edited one of kernel values.
         */
        @Override
        public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
            elements[rowIndex][columnIndex] = (value!=null) ? ((Number) value).floatValue() : 0;
            fireTableCellUpdated(rowIndex, columnIndex);
            findKernelName();
        }

        /**
         * Returns the kernel at the specified index.
         * Used by the combox box of kernel names.
         */
        @Override
        public String getElementAt(final int index) {
            return getKernelNames()[index];
        }

        /**
         * Returns the selected kernel name (never {@code null}).
         * Used by the combox box of kernel names.
         */
        @Override
        public Object getSelectedItem() {
            return (name != null) ? name : getString(Vocabulary.Keys.Personalized);
        }

        /**
         * Set the selected kernel by its name (never {@code null}).
         * Used by the combox box of kernel names.
         */
        @Override
        public void setSelectedItem(final Object item) {
            final String newName = item.toString();
            if (!newName.equals(name)) {
                // 'kernel' may be null if 'item' is the "Personalized" kernel name.
                final KernelJAI kernel = kernels.get(newName);
                if (kernel != null) {
                    setKernel(kernel);
                }
                categorySelector.setSelectedItem(categories.get(newName));
                this.name = newName;
            }
        }

        /**
         * Returns the current kernel.
         *
         * @see KernelEditor#getKernel
         */
        public KernelJAI getKernel() {
            final int   height = elements.length;
            final int    width = height!=0 ? elements[0].length : 0;
            final float[] data = new float[width*height];
            int c = 0;
            for (int j=0; j<height; j++) {
                for (int i=0; i<width; i++) {
                    data[c++] = elements[j][i];
                }
            }
            return new KernelJAI(width, height, data);
        }

        /**
         * Set the kernel. The table size will be set to the specified kernel size, add all
         * coefficients will be copied in the table. If the specified kernel matches one of
         * the kernel registered with the {@link #addKernel addKernel} method, then the kernel
         * name and category will be updated according.
         *
         * @see KernelEditor#setKernel
         */
        public void setKernel(final KernelJAI kernel) {
            final int rowCount = kernel.getHeight();
            final int colCount = kernel.getWidth();
            setKernelSize(rowCount, colCount);
            for (int j=0; j<rowCount; j++) {
                for (int i=0; i<colCount; i++) {
                    elements[j][i] = kernel.getElement(i,j);
                }
            }
            fireTableDataChanged();
        }

        /**
         * Set the size of the current kernel.
         *
         * @param width  The number of rows.
         * @param height The number of columns.
         *
         * @see KernelEditor#setKernelSize
         */
        public void setKernelSize(final int rowCount, final int colCount) {
            final int oldRowCount = elements.length;
            final int oldColCount = oldRowCount!=0 ? elements[0].length : 0;
            if (rowCount!=oldRowCount || colCount!=oldColCount) {
                elements = ArraysExt.resize(elements, rowCount);
                for (int i=0; i<elements.length; i++) {
                    if (elements[i] == null) {
                        elements[i] = new float[colCount];
                    } else {
                        elements[i] = ArraysExt.resize(elements[i], colCount);
                    }
                }
                if (colCount != oldColCount) {
                    fireTableStructureChanged();
                } else if (rowCount > oldRowCount) {
                    fireTableRowsInserted(oldRowCount, rowCount-1);
                } else if (rowCount < oldRowCount) {
                    fireTableRowsDeleted(rowCount, oldRowCount-1);
                }
                widthSelector .setValue(colCount);
                heightSelector.setValue(rowCount);
            }
        }

        /**
         * Returns the index of the specified kernel name in the specified category,
         * or -1 if it was not found.   This method is invoked by {@link #addKernel}
         * and {@link #removeKernel} in order to determine the range of index values
         * to give to {@link ListDataEvent}.
         *
         * @param category The kernel category. Only kernels in this category will
         *        be taken in account. This argument is usually provided by
         *        {@link KernelEditor#getKernelCategory}. {@code null}
         *        is a special value taking all categories in account.
         * @param toSearch The name of the kernel to search.
         */
        private int indexOf(final String category, final String toSearch) {
            int index = 0;
            for (final Map.Entry<String,String> entry : categories.entrySet()) {
                final String name = entry.getKey();
                if (category == null || category.equals(entry.getValue())) {
                    // A kernel of the required category has been found.
                    if (toSearch.equals(name)) {
                        // Found the kernel we are looking for.
                        assert !sorted || Arrays.binarySearch(getKernelNames(), toSearch) == index;
                        return index;
                    }
                    // Right category, but wrong kernel.
                    index++;
                }
                if (sorted && name.compareTo(toSearch) >= 0) {
                    // Since kernel names are sorted, there is no
                    // need to continue the iteration past this point.
                    break;
                }
            }
            assert !sorted || Arrays.binarySearch(getKernelNames(), toSearch) < 0;
            return -1;
        }

        /**
         * Adds a kernel to the list of available kernels. Each kernel can belong to an optional
         * category. Example of categories includes "Error filters" and "Gradient masks".
         *
         * @param category The kernel's category name, which <strong>must not</strong> be
         *        {@code null}. The public method in {@link KernelEditor} is responsible
         *        for substituing a string (usually "Others" or "Personalized") in place of the
         *        null value.
         * @param name The kernel name. Kernels will be displayed in alphabetic order.
         * @param kernel The kernel. If an other kernel was registered with the same
         *        name, the previous kernel will be discarded.
         *
         * @see KernelEditor#addKernel
         */
        public void addKernel(final String category, final String name, final KernelJAI kernel) {
            sorted = false;
            if (!category.equals(categories.put(name, category))) {
                // The category doesn't already exists.
                addCategory(category);
            }
            if (kernels.put(name, kernel) != null) {
                // The new kernel replace an existing one.
                findKernelName();
            } else {
                // The kernel must be added to existing ones.
                final String cc = getKernelCategory();
                if (cc == null || category.equals(cc)) {
                    names = null; // Must be before 'indexOf'
                    final int index = indexOf(cc, name);
                    assert index >= 0 : name;
                    fireListChanged(ListDataEvent.INTERVAL_ADDED, index, index);
                }
            }
            assert kernels.size() == categories.size();
        }

        /**
         * Removes a kernel. If the kernel was the only one in
         * its category, the category is removed as well.
         *
         * @see KernelEditor#removeKernel
         */
        public void removeKernel(final KernelJAI kernel) {
            final String cc = getKernelCategory();
            for (final Iterator<Map.Entry<String,KernelJAI>> it=kernels.entrySet().iterator(); it.hasNext();) {
                final Map.Entry<String,KernelJAI> entry = it.next();
                if (kernel.equals(entry.getValue())) {
                    // Found the kernel to remove.
                    final String name     = entry.getKey();
                    final int    index    = indexOf(cc, name); // Must be before any remove.
                    final String category = categories.remove(name);
                    if (!categories.values().contains(category)) {
                        // No other kernel in this category.
                        categorySelector.removeItem(category);
                    }
                    it.remove();
                    if (index >= 0) {
                        names = null; // Must be after 'it.remove'
                        fireListChanged(ListDataEvent.INTERVAL_REMOVED, index, index);
                    }
                }
            }
            assert kernels.size() == categories.size();
        }

        /**
         * Removes all kernels and categories.
         *
         * @see KernelEditor#removeAllKernels
         */
        public void removeAllKernels() {
            final int size = kernels.size();
            kernels.clear();
            categories.clear();
            names = null;
            fireListChanged(ListDataEvent.INTERVAL_REMOVED, 0, size-1);
            categorySelector.removeAllItems();
            categorySelector.addItem(getResources().getString(Vocabulary.Keys.All));
        }

        /**
         * Returns a kernel by its name.
         *
         * @param  name The kernel name.
         * @return The kernel, or {@code null} if there is no kernel for the specified name.
         */
        public KernelJAI getKernel(final String name) {
            return kernels.get(name);
        }

        /**
         * Returns the array of kernel names. <strong>This method
         * returns the array by reference; do not modify!</strong>.
         *
         * @see KernelEditor#getKernelNames
         */
        public String[] getKernelNames() {
            if (names == null) {
                int count = 0;
                names = new String[kernels.size() + 1];
                final String category = getKernelCategory();
                for (final Map.Entry<String,String> entry : categories.entrySet()) {
                    if (category==null || category.equals(entry.getValue())) {
                        names[count++] = entry.getKey();
                    }
                }
                names[count++] = getString(Vocabulary.Keys.Personalized);
                names = ArraysExt.resize(names, count);
            }
            return names;
        }

        /**
         * Find the name for the current kernel. If such a name is
         * found, it will be given to the combo-box. Otherwise,
         * nothing is done.
         */
        protected void findKernelName() {
            String newName = null; // "Personalized"
            final int rowCount = elements.length;
            final int colCount = rowCount!=0 ? elements[0].length : 0;
      iter: for (final Map.Entry<String,KernelJAI> entry : kernels.entrySet()) {
                final KernelJAI kernel = entry.getValue();
                if (rowCount==kernel.getHeight() && colCount==kernel.getWidth()) {
                    for (int j=0; j<rowCount; j++) {
                        for (int i=0; i<colCount; i++) {
                            if (elements[j][i] != kernel.getElement(i,j)) {
                                continue iter;
                            }
                        }
                    }
                    newName = entry.getKey();
                }
            }
            if (newName == null) {
                newName = getString(Vocabulary.Keys.Personalized);
            }
            if (!newName.equals(name)) {
                // Set the name now in order to avoid that
                // setSelectedItem invokes setKernel again.
                this.name = newName;
                categorySelector.setSelectedItem(categories.get(newName));
                kernelSelector.setSelectedItem(newName);
                kernelSelector.repaint(); // JComboBox doesn't seems to repaint by itself.
            }
        }

        /**
         * Sorts all kernel names according the specified comparator.
         *
         * @param comparator The comparator, or {@code null} for the natural ordering.
         *
         * @see KernelEditor#sortKernelNames
         */
        public void sortKernelNames(final Comparator<String> comparator) {
            final Map<String,String> sorted = new TreeMap<>(comparator);
            sorted.putAll(categories);
            categories.clear();
            categories.putAll(sorted);
            names = null;
            this.sorted = (comparator == null);
            fireListChanged(ListDataEvent.CONTENTS_CHANGED, 0, categories.size()-1);
        }

        /**
         * Invoked when a {@link JSpinner} has changed its state. This method reset
         * the matrix size according the new spinner value.
         */
        @Override
        public void stateChanged(final ChangeEvent event) {
            final int rowCount = ((Number) heightSelector.getValue()).intValue();
            final int colCount = ((Number) widthSelector. getValue()).intValue();
            setKernelSize(rowCount, colCount);
            findKernelName();
        }

        /**
         * Invoked when the user selected a new kernel category.
         * The kernel list must be cleared and reconstructed.
         */
        @Override
        public void itemStateChanged(final ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                names = null;
                fireListChanged(ListDataEvent.CONTENTS_CHANGED, 0, categories.size());
            }
        }

        /**
         * Convenience method returning a string for the specified resource keys.
         */
        private String getString(final short key) {
            return getResources().getString(key);
        }

        /**
         * Adds a listener to the list that's notified
         * each time a change to the data model occurs.
         */
        @Override
        public void addListDataListener(final ListDataListener listener) {
            listenerList.add(ListDataListener.class, listener);
        }

        /**
         * Removes a listener from the list that's notified
         * each time a change to the data model occurs.
         */
        @Override
        public void removeListDataListener(final ListDataListener listener) {
            listenerList.remove(ListDataListener.class, listener);
        }

        /**
         * Invoked after one or more kernels are added to the model.
         *
         * @param type Must be one of {@link ListDataEvent#CONTENTS_CHANGED},
         *        {@link ListDataEvent#INTERVAL_ADDED} or {@link ListDataEvent#INTERVAL_REMOVED}.
         * @param index0 Lower index, inclusive.
         * @param index1 Upper index, <strong>inclusive</strong>.
         */
        private void fireListChanged(final int type, final int index0, final int index1) {
            ListDataEvent event = null;
            final Object[] listeners = listenerList.getListenerList();
            for (int i=listeners.length; (i-=2)>=0;) {
                if (listeners[i] == ListDataListener.class) {
                    if (event==null) {
                        event = new ListDataEvent(this, type, index0, index1);
                    }
                    final ListDataListener listener = (ListDataListener) listeners[i+1];
                    switch (type) {
                        case ListDataEvent.CONTENTS_CHANGED: listener.contentsChanged(event); break;
                        case ListDataEvent.INTERVAL_ADDED  : listener.intervalAdded  (event); break;
                        case ListDataEvent.INTERVAL_REMOVED: listener.intervalRemoved(event); break;
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.12
     */
    @Override
    public void commitEdit() throws ParseException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showDialog(final Component owner, final String title) {
        return SwingUtilities.showDialog(owner, this, title);
    }
}
