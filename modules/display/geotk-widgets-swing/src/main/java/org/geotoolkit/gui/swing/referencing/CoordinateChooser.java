/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.referencing;

import java.util.Date;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.EventListener;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.BorderFactory;
import javax.swing.AbstractButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.AbstractSpinnerModel;
import javax.swing.JFormattedTextField;
import javax.swing.text.InternationalFormatter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.text.Format;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.sis.measure.Angle;
import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.apache.sis.measure.AngleFormat;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.display.shape.DoubleDimension2D;
import org.geotoolkit.gui.swing.Dialog;


/**
 * A pane of controls designed to allow a user to select spatio-temporal coordinates.
 * Current implementation uses geographic coordinates (longitudes/latitudes) and dates
 * according some locale calendar. Future version may allow the use of user-specified
 * coordinate system. Latitudes are constrained in the range 90°S to 90°N inclusive.
 * Longitudes are constrained in the range 180°W to 180°E inclusive. By default, dates
 * are constrained in the range January 1st, 1970 up to the date at the time the widget
 * was created.
 *
 * <table cellspacing="24" cellpadding="12" align="center"><tr valign="top"><td>
 * <img src="doc-files/CoordinateChooser.png">
 * </td><td width="500" bgcolor="lightblue">
 * {@section Demo}
 * The image on the left side gives an example of this widget appearance.
 * To try this component in your browser, see the
 * <a href="http://www.geotoolkit.org/demos/geotk-simples/applet/CoordinateChooser.html">demonstration applet</a>.
 * </td></tr></table>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.14
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class CoordinateChooser extends JComponent implements Dialog {
    /**
     * The factory by which to multiply the resolution. Current value is 60, because
     * the resolution is displayed in minutes of angle. A future Geotk version may
     * replace that factor by something inferred from the CRS.
     */
    private static final double RESOLUTION_FACTOR = 60;

    /**
     * An enumeration constant for showing or hiding the geographic area selector.
     * Used as argument for {@link #isSelectorVisible} and {@link #setSelectorVisible}.
     *
     * @see #TIME_RANGE
     * @see #RESOLUTION
     * @see #isSelectorVisible
     * @see #setSelectorVisible
     * @see #addChangeListener
     * @see #removeChangeListener
     */
    public static final int GEOGRAPHIC_AREA = 1;

    /**
     * An enumeration constant for showing or hiding the time range selector.
     * Used as argument for {@link #isSelectorVisible} and {@link #setSelectorVisible}.
     *
     * @see #GEOGRAPHIC_AREA
     * @see #RESOLUTION
     * @see #isSelectorVisible
     * @see #setSelectorVisible
     * @see #addChangeListener
     * @see #removeChangeListener
     */
    public static final int TIME_RANGE = 2;

    /**
     * An enumeration constant for showing or hiding the resolution selector.
     * Used as argument for {@link #isSelectorVisible} and {@link #setSelectorVisible}.
     *
     * @see #GEOGRAPHIC_AREA
     * @see #TIME_RANGE
     * @see #isSelectorVisible
     * @see #setSelectorVisible
     * @see #addChangeListener
     * @see #removeChangeListener
     */
    public static final int RESOLUTION = 4;

    /**
     * The three mean panels in this dialog box:
     * geographic area, time and preferred resolution.
     */
    private final JComponent areaPanel, timePanel, resoPanel;

    /**
     * Liste de choix dans laquelle l'utilisateur
     * choisira le fuseau horaire de ses dates.
     */
    private final JComboBox<String> timezone;

    /**
     * Dates de début et de fin de la plage de temps demandée par l'utilisateur.
     * Ces dates sont gérées par un modèle {@link SpinnerDateModel}.
     */
    private final JSpinner tmin, tmax;

    /**
     * Longitudes et latitudes minimales et maximales demandées par l'utilisateur.
     * Ces coordonnées sont gérées par un modèle {@link SpinnerNumberModel}.
     */
    private final JSpinner xmin, xmax, ymin, ymax;

    /**
     * Résolution (en minutes de longitudes et de latitudes) demandée par l'utilisateur.
     * Ces résolution sont gérées par un modèle {@link SpinnerNumberModel}.
     */
    private final JSpinner xres, yres;

    /**
     * Bouton radio pour sélectioner la meilleure résolution possible.
     */
    private final AbstractButton radioBestRes;

    /**
     * Bouton radio pour sélectioner la résolution spécifiée.
     */
    private final AbstractButton radioPrefRes;

    /**
     * Composante facultative à afficher à la droite du paneau {@code CoordinateChooser}.
     */
    private JComponent accessory;

    /**
     * Class encompassing various listeners for users selections.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.3
     * @module
     */
    private final class Listeners implements ActionListener, ChangeListener {
        /**
         * List of components to toggle.
         */
        private final JComponent[] toggle;

        /**
         * Constructs a {@code Listeners} object.
         */
        public Listeners(final JComponent[] toggle) {
            this.toggle=toggle;
        }

        /**
         * Invoked when user select a new timezone.
         */
        @Override
        public void actionPerformed(final ActionEvent event) {
            update(getTimeZone());
        }

        /**
         * Invoked when user change the button radio state
         * ("use best resolution" / "set resolution").
         */
        @Override
        public void stateChanged(final ChangeEvent event) {
            setEnabled(radioPrefRes.isSelected());
        }

        /**
         * Enable or disable {@link #toggle} components.
         */
        final void setEnabled(final boolean state) {
            for (int i=0; i<toggle.length; i++) {
                toggle[i].setEnabled(state);
            }
        }
    }

    /**
     * Constructs a default coordinate chooser. Date will be constrained in the range from
     * January 1st, 1970 00:00 UTC up to the {@linkplain System#currentTimeMillis current time}.
     */
    public CoordinateChooser() {
        this(new Date(0), new Date());
    }

    /**
     * Constructs a coordinate chooser with date constrained in the specified range.
     * Note that the {@code [minTime..maxTime]} range is not the same than the
     * range given to {@link #setTimeRange}. The later set only the time range shown
     * in the widget, while this constructor set also the minimum and maximum dates
     * allowed.
     *
     * @param minTime The minimal date allowed.
     * @param maxTime the maximal date allowed.
     */
    public CoordinateChooser(final Date minTime, final Date maxTime) {
        setLayout(new GridBagLayout());
        final Locale locale = getDefaultLocale();
        final int timeField = Calendar.DAY_OF_YEAR;
        final Vocabulary resources = Vocabulary.getResources(locale);

        radioBestRes = new JRadioButton(resources.getString(Vocabulary.Keys.UseBestResolution), true);
        radioPrefRes = new JRadioButton(resources.getString(Vocabulary.Keys.SetPreferredResolution));

        tmin = new JSpinner(new SpinnerDateModel(minTime, minTime, maxTime, timeField));
        tmax = new JSpinner(new SpinnerDateModel(maxTime, minTime, maxTime, timeField));
        xmin = new JSpinner(new SpinnerAngleModel(new Longitude(Longitude.MIN_VALUE)));
        xmax = new JSpinner(new SpinnerAngleModel(new Longitude(Longitude.MAX_VALUE)));
        ymin = new JSpinner(new SpinnerAngleModel(new  Latitude( Latitude.MIN_VALUE)));
        ymax = new JSpinner(new SpinnerAngleModel(new  Latitude( Latitude.MAX_VALUE)));
        xres = new JSpinner(new SpinnerNumberModel(1d, 0d, 360d*60, 1d));
        yres = new JSpinner(new SpinnerNumberModel(1d, 0d, 180d*60, 1d));

        final AngleFormat   angleFormat = AngleFormat.getInstance(locale);
        final DateFormat     dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        xmin.setEditor(new SpinnerAngleModel.Editor(xmin, angleFormat));
        xmax.setEditor(new SpinnerAngleModel.Editor(xmax, angleFormat));
        ymin.setEditor(new SpinnerAngleModel.Editor(ymin, angleFormat));
        ymax.setEditor(new SpinnerAngleModel.Editor(ymax, angleFormat));

        setup(tmin, 10,   dateFormat);
        setup(tmax, 10,   dateFormat);
        setup(xmin,  7,         null);
        setup(xmax,  7,         null);
        setup(ymin,  7,         null);
        setup(ymax,  7,         null);
        setup(xres,  3, numberFormat);
        setup(yres,  3, numberFormat);

        final String[] timezones = TimeZone.getAvailableIDs();
        Arrays.sort(timezones);
        timezone = new JComboBox<>(timezones);
        timezone.setSelectedItem(dateFormat.getTimeZone().getID());

        final JLabel labelSize1 = new JLabel(resources.getLabel(Vocabulary.Keys.SizeInMinutes));
        final JLabel labelSize2 = new JLabel("\u00D7"  /* Multiplication symbol */);
        final ButtonGroup group = new ButtonGroup();
        group.add(radioBestRes);
        group.add(radioPrefRes);

        final Listeners listeners = new Listeners(new JComponent[] {
                                                  labelSize1, labelSize2, xres, yres});
        listeners   .setEnabled(false);
        timezone    .addActionListener(listeners);
        radioPrefRes.addChangeListener(listeners);

        areaPanel = getPanel(resources.getString(Vocabulary.Keys.GeographicCoordinates));
        timePanel = getPanel(resources.getString(Vocabulary.Keys.TimeRange            ));
        resoPanel = getPanel(resources.getString(Vocabulary.Keys.PreferredResolution  ));
        final GridBagConstraints c = new GridBagConstraints();

        c.weightx=1;
        c.gridx=1; c.gridy=0; areaPanel.add(ymax, c);
        c.gridx=0; c.gridy=1; areaPanel.add(xmin, c);
        c.gridx=2; c.gridy=1; areaPanel.add(xmax, c);
        c.gridx=1; c.gridy=2; areaPanel.add(ymin, c);

        JLabel label;
        c.gridx=0; c.anchor=GridBagConstraints.WEST; c.insets.right=3; c.weightx=0;
        c.gridy=0; timePanel.add(label=new JLabel(resources.getLabel(Vocabulary.Keys.StartTime)), c); label.setLabelFor(tmin);
        c.gridy=1; timePanel.add(label=new JLabel(resources.getLabel(Vocabulary.Keys.EndTime  )), c); label.setLabelFor(tmax);
        c.gridy=2; timePanel.add(label=new JLabel(resources.getLabel(Vocabulary.Keys.TimeZone )), c); label.setLabelFor(timezone); c.gridwidth=4;
        c.gridy=0; resoPanel.add(radioBestRes,  c);
        c.gridy=1; resoPanel.add(radioPrefRes,  c);
        c.gridy=2; c.gridwidth=1; c.anchor=GridBagConstraints.EAST; c.insets.right=c.insets.left=1; c.weightx=1;
        c.gridx=0; resoPanel.add(labelSize1, c); labelSize1.setLabelFor(xres);  c.weightx=0;
        c.gridx=1; resoPanel.add(xres,       c);
        c.gridx=2; resoPanel.add(labelSize2, c); labelSize2.setLabelFor(yres);
        c.gridx=3; resoPanel.add(yres,       c);

        c.gridx=1; c.fill=GridBagConstraints.HORIZONTAL; c.insets.right=c.insets.left=0; c.weightx=1;
        c.gridy=0; timePanel.add(tmin,     c);
        c.gridy=1; timePanel.add(tmax,     c);
        c.gridy=2; timePanel.add(timezone, c);

        c.insets.right=c.insets.left=c.insets.top=c.insets.bottom=3;
        c.gridx=0; c.anchor=GridBagConstraints.CENTER; c.fill=GridBagConstraints.BOTH; c.weighty=1;
        c.gridy=0; add(areaPanel, c);
        c.gridy=1; add(timePanel, c);
        c.gridy=2; add(resoPanel, c);
    }

    /**
     * Creates a panel with a titled border.
     */
    private static JPanel getPanel(final String title) {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(title),
                        BorderFactory.createEmptyBorder(6,6,6,6)));
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Sets the width of the given field, in amount of columns.
     * As a side effect, this method set also the format.
     */
    private static void setup(final JSpinner spinner, final int width, final Format format) {
        final JFormattedTextField field = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        field.setColumns(width);
        if (format != null) {
            ((InternationalFormatter)field.getFormatter()).setFormat(format);
        }
    }

    /**
     * Tells if a selector is currently visible or not. The default {@code CoordinateChooser}
     * contains three selectors: one for geographic area, one for time range and one for the
     * preferred resolution.
     *
     * @param selector One of the following constants:
     *                 {@link #GEOGRAPHIC_AREA},
     *                 {@link #TIME_RANGE} or
     *                 {@link #RESOLUTION}.
     * @return {@code true} if the specified selector is visible, or {@code false} otherwise.
     * @throws IllegalArgumentException if {@code selector} is not legal.
     */
    public boolean isSelectorVisible(final int selector) {
        switch (selector) {
            case GEOGRAPHIC_AREA: return areaPanel.isVisible();
            case TIME_RANGE:      return timePanel.isVisible();
            case RESOLUTION:      return resoPanel.isVisible();
            default: throw new IllegalArgumentException();
                     // TODO: provide some error message.
        }
    }

    /**
     * Sets the visible state of one or many selectors.
     * All selectors are visible by default.
     *
     * @param  selectors Any bitwise combinations of {@link #GEOGRAPHIC_AREA},
     *         {@link #TIME_RANGE} and/or {@link #RESOLUTION}.
     * @param  visible {@code true} to show the selectors, or {@code false} to hide them.
     * @throws IllegalArgumentException if {@code selectors} contains illegal bits.
     */
    public void setSelectorVisible(final int selectors, final boolean visible) {
        ensureValidSelectors(selectors);
        if ((selectors & GEOGRAPHIC_AREA) != 0) areaPanel.setVisible(visible);
        if ((selectors & TIME_RANGE     ) != 0) timePanel.setVisible(visible);
        if ((selectors & RESOLUTION     ) != 0) resoPanel.setVisible(visible);
    }

    /**
     * Ensures that the specified bitwise combination of selectors is valid.
     *
     * @param  selectors Any bitwise combinations of {@link #GEOGRAPHIC_AREA},
     *         {@link #TIME_RANGE} and/or {@link #RESOLUTION}.
     * @throws IllegalArgumentException if {@code selectors} contains illegal bits.
     *
     * @todo Provide a better error message.
     */
    private static void ensureValidSelectors(final int selectors) throws IllegalArgumentException {
        if ((selectors & ~(GEOGRAPHIC_AREA | TIME_RANGE | RESOLUTION)) != 0) {
            throw new IllegalArgumentException(String.valueOf(selectors));
        }
    }

    /**
     * Same as {@link Math#min(double, double}, except that the {@code ceil}
     * argument is returned if the {@code value} argument is {@code NaN}.
     */
    private static double min(final double ceil, final double value) {
        return (value < ceil) ? value : ceil;
    }

    /**
     * Same as {@link Math#max(double, double}, except that the {@code floor}
     * argument is returned if the {@code value} argument is {@code NaN}.
     */
    private static double max(final double floor, final double value) {
        return (value > floor) ? value : floor;
    }

    /**
     * Returns the value for the specified number, or NaN if {@code value} is not a number.
     */
    private static double doubleValue(final JSpinner spinner) {
        final Object value = spinner.getValue();
        return (value instanceof Number) ? ((Number) value).doubleValue() : Double.NaN;
    }

    /**
     * Returns the value for the specified angle, or NaN if {@code value} is not an angle.
     */
    private static double degrees(final JSpinner spinner, final boolean expectLatitude) {
        final Object value = spinner.getValue();
        if (value instanceof Angle) {
            if (expectLatitude ? (value instanceof Longitude) : (value instanceof Latitude)) {
                return Double.NaN;
            }
            return ((Angle) value).degrees();
        }
        return Double.NaN;
    }

    /**
     * Gets the geographic area, in latitude and longitude degrees.
     *
     * @return The current geographic area of interest.
     */
    public Rectangle2D getGeographicArea() {
        final double xmin = degrees(this.xmin, false);
        final double ymin = degrees(this.ymin,  true);
        final double xmax = degrees(this.xmax, false);
        final double ymax = degrees(this.ymax,  true);
        return new Rectangle2D.Double(
                Math.min(xmin,  xmax), Math.min(ymin,  ymax),
                Math.abs(xmax - xmin), Math.abs(ymax - ymin));
    }

    /**
     * Sets the geographic area, in latitude and longitude degrees.
     *
     * @param area The new geographic area of interest.
     */
    public void setGeographicArea(final Rectangle2D area) {
        // We allow [-360…360]° range, since the [0…360]° range is sometime used.
        xmin.setValue(new Longitude(max(2*Longitude.MIN_VALUE, area.getMinX())));
        xmax.setValue(new Longitude(min(2*Longitude.MAX_VALUE, area.getMaxX())));
        ymin.setValue(new  Latitude(max(   Latitude.MIN_VALUE, area.getMinY())));
        ymax.setValue(new  Latitude(min(   Latitude.MAX_VALUE, area.getMaxY())));
    }

    /**
     * Returns the preferred resolution. A {@code null} value means that the
     * best available resolution should be used.
     *
     * @return The current preferred resolution, or {@code null} for the best available one.
     */
    public Dimension2D getPreferredResolution() {
        if (radioPrefRes.isSelected()) {
            return new DoubleDimension2D(
                    doubleValue(xres) / RESOLUTION_FACTOR,
                    doubleValue(yres) / RESOLUTION_FACTOR);
        }
        return null;
    }

    /**
     * Sets the preferred resolution. A {@code null} value means that the best
     * available resolution should be used.
     *
     * @param resolution The new preferred resolution, or {@code null} for the best available one.
     */
    public void setPreferredResolution(final Dimension2D resolution) {
        if (resolution != null) {
            xres.setValue(Double.valueOf(max(0, resolution.getWidth () * RESOLUTION_FACTOR)));
            yres.setValue(Double.valueOf(max(0, resolution.getHeight() * RESOLUTION_FACTOR)));
            radioPrefRes.setSelected(true);
        }  else {
            radioBestRes.setSelected(true);
        }
    }

    /**
     * Returns the time zone used for displaying dates.
     *
     * @return The current timezone.
     */
    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(timezone.getSelectedItem().toString());
    }

    /**
     * Sets the time zone. This method change the control's display. It doesn't change the
     * date values, i.e. it has no effect on previous or future call to {@link #setTimeRange}.
     *
     * @param timezone The new timezone.
     */
    public void setTimeZone(final TimeZone timezone) {
        this.timezone.setSelectedItem(timezone.getID());
    }

    /**
     * Updates the time zone in text fields. This method is automatically invoked
     * by {@link JComboBox} on user's selection. It is also (indirectly) invoked
     * on {@link #setTimeZone} call.
     */
    private void update(final TimeZone timezone) {
        boolean refresh=true;
        try {
            tmin.commitEdit();
            tmax.commitEdit();
        } catch (ParseException exception) {
            refresh = false;
        }
        ((JSpinner.DateEditor)tmin.getEditor()).getFormat().setTimeZone(timezone);
        ((JSpinner.DateEditor)tmax.getEditor()).getFormat().setTimeZone(timezone);
        if (refresh) {
            // TODO: If a "JSpinner.reformat()" method was available, we would use it here.
            fireStateChanged((AbstractSpinnerModel)tmin.getModel());
            fireStateChanged((AbstractSpinnerModel)tmax.getModel());
        }
    }

    /**
     * Run each {@link ChangeListener#stateChanged()} method for the specified spinner model.
     */
    private static void fireStateChanged(final AbstractSpinnerModel model) {
        final ChangeEvent   changeEvent = new ChangeEvent(model);
        final EventListener[] listeners = model.getListeners(ChangeListener.class);
        for (int i=listeners.length; --i>=0;) {
            ((ChangeListener)listeners[i]).stateChanged(changeEvent);
        }
    }

    /**
     * Returns the start time, or {@code null} if there is none.
     *
     * @return The start time, or {@code null} if none.
     */
    public Date getStartTime() {
        return (Date) tmin.getValue();
    }

    /**
     * Returns the end time, or {@code null} if there is none.
     *
     * @return The end time, or {@code null} if none.
     */
    public Date getEndTime() {
        return (Date) tmax.getValue();
    }

    /**
     * Sets the time range.
     *
     * @param startTime The start time.
     * @param endTime   The end time.
     *
     * @see #getStartTime
     * @see #getEndTime
     */
    public void setTimeRange(final Date startTime, final Date endTime) {
        tmin.setValue(startTime);
        tmax.setValue(  endTime);
    }

    /**
     * Returns the accessory component.
     *
     * @return The accessory component, or {@code null} if there is none.
     */
    public JComponent getAccessory() {
        return accessory;
    }

    /**
     * Sets the accessory component. An accessory is often used to show available data.
     * However, it can be used for anything that the programmer wishes, such as extra
     * custom coordinate chooser controls.
     * <p>
     * <strong>Note:</strong> If there was a previous accessory, you should unregister any
     * listeners that the accessory might have registered with the coordinate chooser.
     *
     * @param accessory The accessory component, or {@code null} to remove any previous accessory.
     */
    public void setAccessory(final JComponent accessory) {
        synchronized (getTreeLock()) {
            if (this.accessory!=null) {
                remove(this.accessory);
            }
            this.accessory = accessory;
            if (accessory != null) {
                final GridBagConstraints c = new GridBagConstraints();
                c.insets.right=c.insets.left=c.insets.top=c.insets.bottom=3;
                c.gridx=1; c.weightx=1; c.gridwidth=1;
                c.gridy=0; c.weighty=1; c.gridheight=3;
                c.anchor=GridBagConstraints.CENTER; c.fill=GridBagConstraints.BOTH;
                add(accessory, c);
            }
            validate();
        }
    }

    /**
     * Check if an angle is of expected type (latitude or longitude).
     */
    private void checkAngle(final JSpinner field, final boolean expectLatitude) throws ParseException {
        final Object angle=field.getValue();
        if (expectLatitude ? (angle instanceof Longitude) : (angle instanceof Latitude)) {
            throw new ParseException(Errors.getResources(getLocale()).getString(
                    Errors.Keys.IllegalCoordinate_1, angle), 0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commitEdit() throws ParseException {
        JSpinner focus = null;
        try {
            (focus = tmin).commitEdit();
            (focus = tmax).commitEdit();
            (focus = xmin).commitEdit();
            (focus = xmax).commitEdit();
            (focus = ymin).commitEdit();
            (focus = ymax).commitEdit();
            (focus = xres).commitEdit();
            (focus = yres).commitEdit();

            checkAngle(focus = xmin, false);
            checkAngle(focus = xmax, false);
            checkAngle(focus = ymin,  true);
            checkAngle(focus = ymax,  true);
        } catch (ParseException exception) {
            focus.requestFocus();
            throw exception;
        }
    }

    /**
     * Adds a change listener to the listener list. This change listener will be notify when
     * a value changed. The change may be in a geographic coordinate field, a date field, a
     * resolution field, etc. The watched values depend on the {@code selectors} arguments:
     * {@link #GEOGRAPHIC_AREA} will watches for the bounding box (East, West, North and South
     * value); {@link #TIME_RANGE} watches for start time and end time; {@link #RESOLUTION}
     * watches for the resolution along East-West and North-South axis. Bitwise combinations
     * are allowed. For example, <code>GEOGRAPHIC_AREA | TIME_RANGE</code> will register a
     * listener for both geographic area and time range.
     * <p>
     * The source of {@link ChangeEvent}s delivered to {@link ChangeListener}s will be in most
     * case the {@link SpinnerModel} for the edited field.
     *
     * @param  selectors Any bitwise combinations of
     *                   {@link #GEOGRAPHIC_AREA},
     *                   {@link #TIME_RANGE} and/or
     *                   {@link #RESOLUTION}.
     * @param  listener The listener to add to the specified selectors.
     * @throws IllegalArgumentException if {@code selectors} contains illegal bits.
     */
    public void addChangeListener(final int selectors, final ChangeListener listener) {
        ensureValidSelectors(selectors);
        if ((selectors & GEOGRAPHIC_AREA) != 0) {
            xmin.getModel().addChangeListener(listener);
            xmax.getModel().addChangeListener(listener);
            ymin.getModel().addChangeListener(listener);
            ymax.getModel().addChangeListener(listener);
        }
        if ((selectors & TIME_RANGE) != 0) {
            tmin.getModel().addChangeListener(listener);
            tmax.getModel().addChangeListener(listener);
        }
        if ((selectors & RESOLUTION) != 0) {
            xres.getModel().addChangeListener(listener);
            yres.getModel().addChangeListener(listener);
            radioPrefRes.getModel().addChangeListener(listener);
        }
    }

    /**
     * Removes a change listener from the listener list.
     *
     * @param  selectors Any bitwise combinations of
     *                   {@link #GEOGRAPHIC_AREA},
     *                   {@link #TIME_RANGE} and/or
     *                   {@link #RESOLUTION}.
     * @param  listener The listener to remove from the specified selectors.
     * @throws IllegalArgumentException if {@code selectors} contains illegal bits.
     */
    public void removeChangeListener(final int selectors, final ChangeListener listener) {
        ensureValidSelectors(selectors);
        if ((selectors & GEOGRAPHIC_AREA) != 0) {
            xmin.getModel().removeChangeListener(listener);
            xmax.getModel().removeChangeListener(listener);
            ymin.getModel().removeChangeListener(listener);
            ymax.getModel().removeChangeListener(listener);
        }
        if ((selectors & TIME_RANGE) != 0) {
            tmin.getModel().removeChangeListener(listener);
            tmax.getModel().removeChangeListener(listener);
        }
        if ((selectors & RESOLUTION) != 0) {
            xres.getModel().removeChangeListener(listener);
            yres.getModel().removeChangeListener(listener);
            radioPrefRes.getModel().removeChangeListener(listener);
        }
    }

    /**
     * Shows a dialog box requesting input from the user. The dialog box will be parented to
     * {@code owner}. If {@code owner} is contained into a {@link javax.swing.JDesktopPane},
     * the dialog box will appears as an internal frame.
     * <p>
     * This method can be invoked from any thread (may or may not be the <cite>Swing</cite> thread).
     *
     * @param  owner The parent component for the dialog box, or {@code null} if there is no parent.
     * @return {@code true} if user pressed the "Ok" button, or {@code false} otherwise
     *         (e.g. pressing "Cancel" or closing the dialog box from the title bar).
     */
    public boolean showDialog(final Component owner) {
        return showDialog(owner, Vocabulary.getResources(getLocale()).
                getString(Vocabulary.Keys.CoordinatesSelection));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showDialog(final Component owner, final String title) {
        return SwingUtilities.showDialog(owner, this, title);
    }
}
