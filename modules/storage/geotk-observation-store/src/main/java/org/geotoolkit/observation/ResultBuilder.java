/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.observation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.geotoolkit.swe.xml.TextBlock;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ResultBuilder {

    protected final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    protected final SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");

    private final ResultMode mode;
    private final boolean csvHack;
    private boolean emptyLine;

    private StringBuilder values;
    private StringBuilder currentLine;
    private final TextBlock encoding;

    private List<Object> dataArray;
    private List<Object> currentArrayLine;

    private int count = 0;

    public ResultBuilder(ResultMode mode, final TextBlock encoding, boolean csvHack) {
        this.mode = mode;
        this.csvHack = csvHack;
        this.encoding = encoding;
        switch (mode) {
            case DATA_ARRAY:
                dataArray = new ArrayList<>();
                break;
            case CSV:
                values = new StringBuilder();
                break;
        }
    }

    public void newBlock() {
        switch (getMode()) {
            case DATA_ARRAY:
                currentArrayLine = new ArrayList<>();
                break;
            case CSV:
                currentLine = new StringBuilder();
                break;
        }
        this.emptyLine = true;
    }

    public void appendTime(Date t) {
        switch (getMode()) {
            case DATA_ARRAY:
                currentArrayLine.add(t);
                break;
            case CSV:
                DateFormat df;
                if (csvHack) {
                    df = format;
                } else {
                    df = format2;
                }
                synchronized (df) {
                    currentLine.append(df.format(t)).append(encoding.getTokenSeparator());
                }
                break;
        }
    }

    public void appendDouble(Double d) {
        if (!d.isNaN()) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY:
                currentArrayLine.add(d);
                break;
            case CSV:
                if (!d.isNaN()) {
                    currentLine.append(Double.toString(d));
                }
                currentLine.append(encoding.getTokenSeparator());
                break;
        }
    }

    public void appendString(String value) {
        if (value != null && !value.isEmpty()) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY:
                currentArrayLine.add(value);
                break;
            case CSV:
                if (value != null && !value.isEmpty()) {
                    currentLine.append(value);
                }
                currentLine.append(encoding.getTokenSeparator());
                break;
        }
    }

    public void appendLong(Long value) {
        if (value != null) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY:
                currentArrayLine.add(value);
                break;
            case CSV:
                if (value != null) {
                    currentLine.append(value);
                }
                currentLine.append(encoding.getTokenSeparator());
                break;
        }
    }

    public int endBlock() {
        if (!emptyLine) {
            switch (getMode()) {
                case DATA_ARRAY:
                    dataArray.add(currentArrayLine);
                    break;
                case CSV:
                    values.append(currentLine);
                    // remove last token separator
                    values.deleteCharAt(values.length() - 1);
                    values.append(encoding.getBlockSeparator());
                    break;
                case COUNT:
                    count++;
                    break;
            }
            return 1;
        }
        return 0;
    }

    public String getStringValues() {
        if (values != null) {
            return values.toString();
        }
        return null;
    }

    public List<Object> getDataArray() {
        return dataArray;
    }

    public int getCount() {
        return count;
    }

    public void appendHeaders(List<Field> fields) {
        switch (getMode()) {
            case CSV:
                for (Field pheno : fields) {
                    // hack for the current graph in cstl you only work when the main field is named "time"
                    if (csvHack && "Time".equals(pheno.fieldType)) {
                        values.append("time").append(encoding.getTokenSeparator());
                    } else {
                        values.append(pheno.fieldDesc).append(encoding.getTokenSeparator());
                    }
                }
                values.setCharAt(values.length() - 1, '\n');
        }
    }

    public ResultMode getMode() {
        return mode;
    }
}
