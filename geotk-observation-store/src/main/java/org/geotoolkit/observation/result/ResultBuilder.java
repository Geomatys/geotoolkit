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
package org.geotoolkit.observation.result;

import org.geotoolkit.observation.model.ResultMode;
import org.geotoolkit.observation.model.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.geotoolkit.observation.model.FieldType;
import org.geotoolkit.observation.model.TextEncoderProperties;

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
    private final TextEncoderProperties encoding;

    private List<Object> dataArray;
    private List<Object> currentArrayLine;

    private int count = 0;

    public ResultBuilder(ResultMode mode, final TextEncoderProperties encoding, boolean csvHack) {
        this.mode = mode;
        this.csvHack = csvHack;
        this.encoding = encoding;
        switch (mode) {
            case DATA_ARRAY -> dataArray = new ArrayList<>();
            case CSV        -> values = new StringBuilder();
        }
    }

    public void clear() {
        switch (mode) {
            case DATA_ARRAY -> dataArray = new ArrayList<>();
            case CSV        -> values = new StringBuilder();
        }
    }

    public void newBlock() {
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine = new ArrayList<>();
            case CSV        -> currentLine = new StringBuilder();
        }
        this.emptyLine = true;
    }

    public void appendTime(Date t) {
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(t);
            case CSV -> {
                String value = "";
                if (t != null) {
                    DateFormat df;
                    if (csvHack) {
                        df = format;
                    } else {
                        df = format2;
                    }
                    synchronized (df) {
                        value = df.format(t);
                    }
                }
                currentLine.append(value).append(encoding.getTokenSeparator());
            }
        }
    }

    public void appendTime(Long t) {
        Date d = null;
        if (t != null) {
            d = new Date(t);
        }
        appendTime(d);
    }

    public void appendNumber(Number value) {
        if (value != null) {
            emptyLine = false;
        }
        if (value instanceof Double d) {
            appendDouble(d);
        } else if (value instanceof Float f) {
            appendFloat(f);
        } else if (value instanceof Integer i) {
            appendInteger(i);
        } else if (value instanceof Long l) {
            appendLong(l);
        } else if (value == null) {
            switch (getMode()) {
                case DATA_ARRAY -> currentArrayLine.add(null);
                case CSV        -> currentLine.append(encoding.getTokenSeparator());
            }
        } else {
            throw new IllegalArgumentException("Unexpected number type:" + value.getClass().getSimpleName());
        }
    }

    public void appendBoolean(Boolean d) {
        if (d != null) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(d);
            case CSV -> {
                if (d != null) {
                    currentLine.append(Boolean.toString(d));
                }
                currentLine.append(encoding.getTokenSeparator());
            }
        }
    }

    public void appendDouble(Double d) {
        if (!d.isNaN()) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(d);
            case CSV -> {
                if (!d.isNaN()) {
                    currentLine.append(Double.toString(d));
                }
                currentLine.append(encoding.getTokenSeparator());
            }
        }
    }

    public void appendFloat(Float d) {
        if (!d.isNaN()) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(d);
            case CSV -> {
                if (!d.isNaN()) {
                    currentLine.append(Double.toString(d));
                }
                currentLine.append(encoding.getTokenSeparator());
            }
        }
    }

    public void appendInteger(Integer d) {
        if (d != null) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(d);
            case CSV -> {
                if (d != null) {
                    currentLine.append(Integer.toString(d));
                }
                currentLine.append(encoding.getTokenSeparator());
            }
        }
    }

    public void appendString(String value) {
        if (value != null && !value.isEmpty()) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(value);
            case CSV -> {
                if (value != null && !value.isEmpty()) {
                    currentLine.append(value);
                }
                currentLine.append(encoding.getTokenSeparator());
            }
        }
    }

    public void appendLong(Long value) {
        if (value != null) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(value);
            case CSV -> {
                if (value != null) {
                    currentLine.append(value);
                }
                currentLine.append(encoding.getTokenSeparator());
            }
        }
    }

    public void appendValue(Object value) {
        if (value instanceof Number d) {
            appendNumber(d);
        } else if (value instanceof String s) {
            appendString(s);
        } else if (value instanceof Date d) {
            appendTime(d);
        } else if (value instanceof Boolean b) {
            appendBoolean(b);
        } else if (value == null) {
            appendString((String) null);
        } else {
            throw new IllegalArgumentException("Unssuported value type:" + value);
        }
    }

    public int endBlock() {
        if (!emptyLine) {
            switch (getMode()) {
                case DATA_ARRAY -> dataArray.add(currentArrayLine);
                case CSV -> {
                    values.append(currentLine);
                    // remove last token separator
                    values.deleteCharAt(values.length() - 1);
                    values.append(encoding.getBlockSeparator());
                }
                case COUNT -> count++;
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
            case CSV -> {
                boolean first = true;
                for (Field pheno : fields) {
                    // hack for the current graph in examind you only work when the main field is named "time"
                    if (csvHack && FieldType.TIME.equals(pheno.type) && first) {
                        values.append("time").append(encoding.getTokenSeparator());
                    } else {
                        values.append(pheno.label).append(encoding.getTokenSeparator());
                    }
                    first = false;
                }
                values.setCharAt(values.length() - 1, '\n');
            }
        }
    }

    public ResultMode getMode() {
        return mode;
    }

    public TextEncoderProperties getEncoding(){
        return encoding;
    }
}
