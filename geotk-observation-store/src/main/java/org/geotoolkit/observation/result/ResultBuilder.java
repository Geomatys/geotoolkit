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
import java.util.Map;
import org.geotoolkit.observation.OMUtils;
import org.geotoolkit.observation.model.FieldDataType;
import static org.geotoolkit.observation.model.ResultMode.COUNT;
import static org.geotoolkit.observation.model.ResultMode.CSV;
import static org.geotoolkit.observation.model.ResultMode.DATA_ARRAY;
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

    /**
     * Reset all values.
     */
    public void clear() {
        switch (mode) {
            case DATA_ARRAY -> dataArray = new ArrayList<>();
            case CSV        -> values = new StringBuilder();
        }
    }

    /**
     * Start a new measure line.
     */
    public void newBlock() {
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine = new ArrayList<>();
            case CSV        -> currentLine = new StringBuilder();
        }
        this.emptyLine = true;
    }

    /**
     * Append a date to the current data line.
     *
     * @param value Date value.
     * @param measureField if set to {@code false} this will not change the status of empty line.
     * @param f The current field.
     */
    public void appendTime(Date value, boolean measureField, Field f) {
        if (value != null && measureField) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(value);
            case CSV -> {
                String strValue = "";
                if (value!= null) {
                    DateFormat df;
                    df = csvHack ? format : format2;
                    synchronized (df) {
                        strValue = df.format(value);
                    }
                }
                currentLine.append(strValue).append(encoding.getTokenSeparator());
            }
        }
    }

    /**
     * Append a date in millisecond to the current data line.
     *
     * @param value Date value in millisecond.
     * @param measureField if set to {@code false} this will not change the status of empty line.
     * @param f The current field.
     */
    public void appendTime(Long value, boolean measureField, Field f) {
        Date d = null;
        if (value != null) {
            d = new Date(value);
        }
        appendTime(d, measureField, f);
    }

    /**
     * Append a number value to the current data line.
     *
     * @param value Number value.
     * @param measureField if set to {@code false} this will not change the status of empty line.
     * @param field The current field.
     */
    public void appendNumber(Number value, boolean measureField, Field field) {
        if (value != null && measureField) {
            emptyLine = false;
        }
        if (value instanceof Double d) {
            appendDouble(d, measureField, field);
        } else if (value instanceof Float f) {
            appendFloat(f, measureField, field);
        } else if (value instanceof Integer i) {
            appendInteger(i, measureField, field);
        } else if (value instanceof Long l) {
            appendLong(l, measureField, field);
        } else if (value == null) {
            switch (getMode()) {
                case DATA_ARRAY -> currentArrayLine.add(null);
                case CSV        -> currentLine.append(encoding.getTokenSeparator());
            }
        } else {
            throw new IllegalArgumentException("Unexpected number type:" + value.getClass().getSimpleName());
        }
    }

    /**
     * Append a boolean value to the current data line.
     *
     * @param value Boolean value.
     * @param measureField if set to {@code false} this will not change the status of empty line.
     * @param field The current field.
     */
    public void appendBoolean(Boolean value, boolean measureField, Field field) {
        if (value != null && measureField) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(value);
            case CSV -> {
                if (value != null) {
                    currentLine.append(Boolean.toString(value));
                }
                currentLine.append(encoding.getTokenSeparator());
            }

        }
    }

    public void appendMap(Map value, boolean measureField, Field field) {
        if (value != null && measureField) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(value);
            case CSV -> {
                if (value != null) {
                    currentLine.append(OMUtils.writeJsonMap(value));
                }
                currentLine.append(encoding.getTokenSeparator());
            }

        }
    }

    /**
     * Append a Double value to the current data line.
     *
     * @param value Double value.
     * @param measureField if set to {@code false} this will not change the status of empty line.
     * @param field The current field.
     */
    public void appendDouble(Double value, boolean measureField, Field field) {
        if (value != null && !value.isNaN() && measureField) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(value);
            case CSV -> {
                if (!value.isNaN()) {
                    currentLine.append(Double.toString(value));
                }
                currentLine.append(encoding.getTokenSeparator());
            }

        }
    }

    /**
     * Append a Float value to the current data line.
     *
     * @param value Float value.
     * @param measureField if set to {@code false} this will not change the status of empty line.
     * @param field The current field.
     */
    public void appendFloat(Float value, boolean measureField, Field field) {
        if (value != null && !value.isNaN() && measureField) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(value);
            case CSV -> {
                if (value != null && !value.isNaN()) {
                    currentLine.append(Double.toString(value));
                }
                currentLine.append(encoding.getTokenSeparator());
            }

        }
    }

    /**
     * Append a Integer value to the current data line.
     *
     * @param value Integer value.
     * @param measureField if set to {@code false} this will not change the status of empty line.
     * @param field The current field.
     */
    public void appendInteger(Integer value, boolean measureField, Field field) {
        if (value != null && measureField) {
            emptyLine = false;
        }
        switch (getMode()) {
            case DATA_ARRAY -> currentArrayLine.add(value);
            case CSV -> {
                if (value != null) {
                    currentLine.append(Integer.toString(value));
                }
                currentLine.append(encoding.getTokenSeparator());
            }

        }
    }

    /**
     * Append a Integer value to the current data line.
     *
     * @param value String value.
     * @param measureField if set to {@code false} this will not change the status of empty line.
     * @param field The current field.
     */
    public void appendString(String value, boolean measureField, Field field) {
        if (value != null && !value.isEmpty() && measureField) {
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

    /**
     * Append a Long value to the current data line.
     *
     * @param value String value.
     * @param measureField if set to {@code false} this will not change the status of empty line.
     * @param field The current field.
     */
    public void appendLong(Long value, boolean measureField, Field field) {
        if (value != null && measureField) {
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

    /**
     * Append a value to the current data line.
     *
     * @param value value.
     * @param measureField if set to {@code false} this will not change the status of empty line.
     * @param f The current field.
     */
    public void appendValue(Object value, boolean measureField, Field f) {
        if (value instanceof Number d) {
            appendNumber(d, measureField, f);
        } else if (value instanceof String s) {
            appendString(s, measureField, f);
        } else if (value instanceof Date d) {
            appendTime(d, measureField, f);
        } else if (value instanceof Boolean b) {
            appendBoolean(b, measureField, f);
        } else if (value == null) {
            appendString((String) null, measureField, f);
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
                    if (csvHack && FieldDataType.TIME.equals(pheno.dataType) && first) {
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
