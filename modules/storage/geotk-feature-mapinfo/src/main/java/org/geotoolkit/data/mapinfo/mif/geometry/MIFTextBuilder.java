/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.mif.style.Font;
import org.geotoolkit.data.mapinfo.mif.style.LabelLine;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.feature.type.DefaultAttributeType;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.opengis.util.GenericName;
import org.opengis.referencing.operation.MathTransform;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.util.ArgumentChecks;

/**
 * A class which manage MIF text geometry.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFTextBuilder extends MIFGeometryBuilder {
    public static final GenericName NAME = DefaultName.create("ENVELOPE");
    public static final GenericName TEXT_NAME = DefaultName.create("TEXT");
    public static final GenericName SPACING_NAME = DefaultName.create("SPACING");
    public static final GenericName JUSTIFY_NAME = DefaultName.create("JUSTIFY");
    public static final GenericName ANGLE_NAME = DefaultName.create("ANGLE");

    public static final AttributeDescriptor TEXT_DESCRIPTOR;
    public static final AttributeDescriptor FONT_DESCRIPTOR;
    public static final AttributeDescriptor SPACING_DESCRIPTOR;
    public static final AttributeDescriptor JUSTIFY_DESCRIPTOR;
    public static final AttributeDescriptor ANGLE_DESCRIPTOR;
    public static final AttributeDescriptor LABEL_DESCRIPTOR;

    public static final Pattern SPACING_PATTERN = Pattern.compile(SPACING_NAME.tip().toString()+"\\s*\\([^\\)]+\\)", Pattern.CASE_INSENSITIVE);
    public static final Pattern JUSTIFY_PATTERN = Pattern.compile(JUSTIFY_NAME.tip().toString(), Pattern.CASE_INSENSITIVE);
    public static final Pattern ANGLE_PATTERN = Pattern.compile(ANGLE_NAME.tip().toString()+"\\s*\\([^\\)]+\\)", Pattern.CASE_INSENSITIVE);

    static {
        final DefaultAttributeType textType = new DefaultAttributeType(TEXT_NAME, String.class, true, false, null, null, null);
        TEXT_DESCRIPTOR = new DefaultAttributeDescriptor(textType, TEXT_NAME, 1, 1, false, "No data");

        FONT_DESCRIPTOR = new DefaultAttributeDescriptor(STRING_TYPE, Font.NAME, 1, 1, true, null);

        final DefaultAttributeType spacingType = new DefaultAttributeType(SPACING_NAME, Float.class, true, false, null, null, null);
        SPACING_DESCRIPTOR = new DefaultAttributeDescriptor(spacingType, SPACING_NAME, 1, 1, false, 1f);

        final DefaultAttributeType justifyType = new DefaultAttributeType(JUSTIFY_NAME, String.class, true, false, null, null, null);
        JUSTIFY_DESCRIPTOR = new DefaultAttributeDescriptor(justifyType, JUSTIFY_NAME, 1, 1, false, "Left");

        final DefaultAttributeType angleType = new DefaultAttributeType(ANGLE_NAME, Double.class, true, false, null, null, null);
        ANGLE_DESCRIPTOR = new DefaultAttributeDescriptor(angleType, ANGLE_NAME, 1, 1, true, null);

        LABEL_DESCRIPTOR = new DefaultAttributeDescriptor(STRING_TYPE, LabelLine.NAME, 1, 1, true, null);
    }

    /**
     * Build a feature describing a MIF point geometry. That assume that user gave a {@link Scanner} which is placed on
     * a TEXT tag.
     *
     * @param scanner The scanner to use for data reading (must be pointing on a mif POINT element).
     * @param toFill
     * @param toApply
     * @throws DataStoreException If there's a problem while parsing stream of the given Scanner.
     */
    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {

        try {
            String geomText = scanner.next("\\w+");
            if (TEXT_NAME.tip().toString().equalsIgnoreCase(geomText)) {
                geomText = scanner.next("\\w+");
            }
            toFill.getProperty(TEXT_NAME).setValue(geomText);

            final double[] pts = new double[4];

            for (int i = 0; i < pts.length; i++) {
                pts[i] = Double.parseDouble(scanner.next(ProjectionUtils.DOUBLE_PATTERN));
            }

            final CoordinateSequence seq;
            if (toApply != null) {
                try {
                    double[] afterT = new double[4];
                    toApply.transform(pts, 0, afterT, 0, 2);
                    seq = new PackedCoordinateSequence.Double(afterT, 2);
                } catch (Exception e) {
                    throw new DataStoreException("Unable to transform geometry", e);
                }
            } else {
                seq = new PackedCoordinateSequence.Double(pts, 2);
            }
            final Envelope env = new Envelope(seq.getCoordinate(0), seq.getCoordinate(1));

            toFill.getDefaultGeometryProperty().setValue(env);
        } catch (Exception e) {
            throw new DataStoreException("Unable to build envelope from given data", e);
        }

        /**
         * Add a management for the text options.
         */
        if (scanner.hasNext(Font.PATTERN)) {
            String args = scanner.next()+scanner.next(Font.PATTERN);
            String[] argsTab = args.substring(args.indexOf('(')+1, args.length()-1)
                    .replaceAll("[^\\w^,]+", "")
                    .split(",");
            if (argsTab.length < 3) {
                LOGGER.log(Level.WARNING, "A FONT clause have been found, but can't be read (bad syntax ?). Ignore it.");
            }
            else {
                final String fName = argsTab[0];
                final int fStyle = Integer.decode(argsTab[1]);
                final int fColor = Integer.decode(argsTab[2]);
                Font font = new Font(fName, fStyle, fColor);
                if(argsTab.length > 3) {
                    final int background = Integer.decode(argsTab[3]);
                    font.setBackColorCode(background);
                }
                toFill.getProperty(Font.NAME).setValue(font);
            }
        }

        if (scanner.hasNext(SPACING_PATTERN)) {
            String spacing = scanner.next(SPACING_PATTERN);
            Matcher match = ProjectionUtils.DOUBLE_PATTERN.matcher(spacing);
            if(match.find()) {
                toFill.getProperty(SPACING_NAME).setValue(Double.parseDouble(match.group()));
            }
        }

        if (scanner.hasNext(JUSTIFY_PATTERN)) {
            String spacing = scanner.next(JUSTIFY_PATTERN);
            toFill.getProperty(SPACING_NAME).setValue(scanner.next());
        }

        if (scanner.hasNext(ANGLE_PATTERN)) {
            String spacing = scanner.next(ANGLE_PATTERN);
            Matcher match = ProjectionUtils.DOUBLE_PATTERN.matcher(spacing);
            if(match.find()) {
                toFill.getProperty(ANGLE_NAME).setValue(Double.parseDouble(match.group()));
            }
        }

        if(scanner.hasNext(LabelLine.PATTERN)) {
            String label = scanner.next(LabelLine.PATTERN);
            String type = label.contains("?i(arrow)")? "arrow" : "simple";
            Double x = null, y = null;
            Matcher match = ProjectionUtils.DOUBLE_PATTERN.matcher(label);
            if(match.find()) {
                x = Double.parseDouble(match.group());
            }
            if(match.find()) {
                y = Double.parseDouble(match.group());
            }
            LabelLine labelLine = new LabelLine(type, new Coordinate(x, y));
            toFill.getProperty(LabelLine.NAME).setValue(labelLine);
        }
    }


    @Override
    public String toMIFSyntax(Feature source) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Source feature", source);
        if(source.getDefaultGeometryProperty() == null) {
            throw new DataStoreException("Input feature does not contain any geometry.");
        }

        if (source.getProperty(TEXT_NAME) == null) {
            throw new DataStoreException("Not enough information to build an arc (missing text).");
        }

        StringBuilder builder = new StringBuilder(TEXT_NAME.tip().toString()).append(' ');
        builder.append(' ').append(source.getProperty(TEXT_NAME).getValue()).append('\n');
        Object value = source.getDefaultGeometryProperty().getValue();
        if(value instanceof Envelope) {
            Envelope env = (Envelope) value;
            builder.append(env.getMinX()).append(' ')
                    .append(env.getMinY()).append(' ')
                    .append(env.getMaxX()).append(' ')
                    .append(env.getMaxY());
        } else if (value instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) value;
            builder.append(rect.getMinX()).append(' ')
                    .append(rect.getMinY()).append(' ')
                    .append(rect.getMaxX()).append(' ')
                    .append(rect.getMaxY());
        } else if(value instanceof Envelope2D) {
            Envelope2D env = (Envelope2D) value;
            builder.append(env.getMinX()).append(' ')
                    .append(env.getMinY()).append(' ')
                    .append(env.getMaxX()).append(' ')
                    .append(env.getMaxY());
        } else {
            throw new DataStoreException("Unable to build a text with the current geometry (Non compatible type"+value.getClass()+").");
        }
        builder.append('\n');

        if(source.getProperty(Font.NAME) != null) {
            Object ftValue = source.getProperty(Font.NAME).getValue();
            if(ftValue != null && ftValue instanceof Font) {
                builder.append(ftValue);
            }
        }

        if(source.getProperty(SPACING_NAME) != null && source.getProperty(SPACING_NAME).getValue() != null) {
            builder.append("SPACING ("+source.getProperty(SPACING_NAME).getValue()+")\n");
        }

        if(source.getProperty(JUSTIFY_NAME) != null && source.getProperty(JUSTIFY_NAME).getValue() != null) {
            builder.append("JUSTIFY ("+source.getProperty(JUSTIFY_NAME).getValue()+")\n");
        }

        if(source.getProperty(ANGLE_NAME) != null && source.getProperty(ANGLE_NAME).getValue() != null) {
            builder.append("ANGLE "+source.getProperty(ANGLE_NAME).getValue()).append('\n');
        }

        if(source.getProperty(LabelLine.NAME) != null && source.getProperty(LabelLine.NAME).getValue() != null) {
            builder.append(source.getProperty(LabelLine.NAME).getValue()).append('\n');
        }

        return builder.toString();
    }

    @Override
    public Class getGeometryBinding() {
        return Envelope.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{Envelope.class, Envelope2D.class, Rectangle2D.class};
    }

    @Override
    public GenericName getName() {
        return NAME;
    }

    @Override
    protected List<AttributeDescriptor> getAttributes() {
        final List<AttributeDescriptor> descList = new ArrayList<AttributeDescriptor>(5);
        descList.add(TEXT_DESCRIPTOR);
        descList.add(FONT_DESCRIPTOR);
        descList.add(SPACING_DESCRIPTOR);
        descList.add(ANGLE_DESCRIPTOR);
        descList.add(LABEL_DESCRIPTOR);
        return descList;
    }
}
