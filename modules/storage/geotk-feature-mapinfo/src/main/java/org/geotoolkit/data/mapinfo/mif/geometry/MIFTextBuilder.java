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
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.opengis.referencing.operation.MathTransform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sis.feature.DefaultAttributeType;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;

import static org.geotoolkit.data.mapinfo.mif.style.LabelLine.LABEL;
import org.geotoolkit.geometry.jts.JTS;

/**
 * A class which manage MIF text geometry.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 26/02/13
 */
public class MIFTextBuilder extends MIFRectangleBuilder {
    public static final GenericName NAME = NamesExt.create("ENVELOPE");
    public static final GenericName TEXT_NAME = NamesExt.create("TEXT");
    public static final GenericName SPACING_NAME = NamesExt.create("SPACING");
    public static final GenericName JUSTIFY_NAME = NamesExt.create("JUSTIFY");
    public static final GenericName ANGLE_NAME = NamesExt.create("ANGLE");

    public static final AttributeType TEXT_DESCRIPTOR = new DefaultAttributeType(Collections.singletonMap("name", TEXT_NAME), String.class, 1, 1, null);
    public static final AttributeType FONT_DESCRIPTOR = new DefaultAttributeType(Collections.singletonMap("name", Font.NAME), String.class, 1, 1, null);
    public static final AttributeType SPACING_DESCRIPTOR = new DefaultAttributeType(Collections.singletonMap("name", SPACING_NAME), Float.class, 1, 1, 1f);
    public static final AttributeType JUSTIFY_DESCRIPTOR = new DefaultAttributeType(Collections.singletonMap("name", JUSTIFY_NAME), String.class, 1, 1, "Left");
    public static final AttributeType ANGLE_DESCRIPTOR = new DefaultAttributeType(Collections.singletonMap("name", ANGLE_NAME), Double.class, 1, 1, null);

    public static final Pattern SPACING_PATTERN = Pattern.compile(SPACING_NAME.tip().toString()+"\\s*\\([^\\)]+\\)", Pattern.CASE_INSENSITIVE);
    public static final Pattern JUSTIFY_PATTERN = Pattern.compile(JUSTIFY_NAME.tip().toString(), Pattern.CASE_INSENSITIVE);
    public static final Pattern ANGLE_PATTERN = Pattern.compile(ANGLE_NAME.tip().toString()+"\\s*\\([^\\)]+\\)", Pattern.CASE_INSENSITIVE);

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
            toFill.setPropertyValue(TEXT_NAME.toString(),geomText);

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

            toFill.setPropertyValue(FeatureExt.getDefaultGeometry(toFill.getType()).getName().tip().toString(), JTS.toGeometry(env));
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
                toFill.setPropertyValue(Font.NAME.toString(),font);
            }
        }

        if (scanner.hasNext(SPACING_PATTERN)) {
            String spacing = scanner.next(SPACING_PATTERN);
            Matcher match = ProjectionUtils.DOUBLE_PATTERN.matcher(spacing);
            if(match.find()) {
                toFill.setPropertyValue(SPACING_NAME.toString(),Double.parseDouble(match.group()));
            }
        }

        if (scanner.hasNext(JUSTIFY_PATTERN)) {
            String spacing = scanner.next(JUSTIFY_PATTERN);
            toFill.setPropertyValue(SPACING_NAME.toString(),scanner.next());
        }

        if (scanner.hasNext(ANGLE_PATTERN)) {
            String spacing = scanner.next(ANGLE_PATTERN);
            Matcher match = ProjectionUtils.DOUBLE_PATTERN.matcher(spacing);
            if(match.find()) {
                toFill.setPropertyValue(ANGLE_NAME.toString(),Double.parseDouble(match.group()));
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
            toFill.setPropertyValue(LabelLine.NAME.toString(),labelLine);
        }
    }


    @Override
    public String toMIFSyntax(Feature source) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Source feature", source);
        if(!FeatureExt.hasAGeometry(source.getType())) {
            throw new DataStoreException("Input feature does not contain any geometry.");
        }

        final Object text = MIFUtils.getPropertySafe(source, TEXT_NAME.toString());
        if (text == null) {
            throw new DataStoreException("Not enough information to build an arc (missing text).");
        }

        StringBuilder builder = new StringBuilder(TEXT_NAME.tip().toString()).append(' ');
        builder.append(' ').append(text).append('\n');
        appendMIFEnvelope(builder, MIFUtils.getGeometryValue(source));
        builder.append('\n');

        Object styleData = MIFUtils.getPropertySafe(source, Font.NAME.toString());
        if (styleData instanceof Font)
            builder.append(styleData);

        styleData = MIFUtils.getPropertySafe(source, SPACING_NAME.toString());
        if(styleData != null) {
            builder.append("SPACING (").append(styleData).append(")\n");
        }

        styleData = MIFUtils.getPropertySafe(source, JUSTIFY_NAME.toString());
        if(styleData != null) {
            builder.append("JUSTIFY (").append(styleData).append(")\n");
        }

        styleData = MIFUtils.getPropertySafe(source, ANGLE_NAME.toString());
        if(styleData != null) {
            builder.append("ANGLE ").append(styleData).append('\n');
        }

        styleData = MIFUtils.getPropertySafe(source, LabelLine.NAME.toString());
        if(styleData != null) {
            builder.append(styleData).append('\n');
        }

        return builder.toString();
    }

    @Override
    public GenericName getName() {
        return NAME;
    }

    @Override
    protected List<AttributeType> getAttributes() {
        final List<AttributeType> descList = new ArrayList<AttributeType>(5);
        descList.add(TEXT_DESCRIPTOR);
        descList.add(FONT_DESCRIPTOR);
        descList.add(SPACING_DESCRIPTOR);
        descList.add(ANGLE_DESCRIPTOR);
        descList.add(LABEL);
        return descList;
    }
}
