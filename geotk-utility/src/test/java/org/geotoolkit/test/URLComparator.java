/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.util.ArgumentChecks;
import static org.junit.Assert.*;

/**
 * Compare urls ignoring possible parameter order differences.
 *
 * @author Johann Sorel (Geomatys)
 */
public class URLComparator {

    private static final Object NO_VALUE = new Object();

    private final String expected;
    private final String result;

    //configuration
    private boolean parameterNameCaseSensitive = true;

    /**
     *
     * @param expected String or URL
     * @param result String or URL
     */
    public URLComparator(Object expected, Object result) {
        ArgumentChecks.ensureNonNull("expected", expected);
        ArgumentChecks.ensureNonNull("result", result);

        if (!(expected instanceof String)) {
            expected = expected.toString();
        }
        if (!(result instanceof String)) {
            result = result.toString();
        }
        this.expected = (String) expected;
        this.result = (String) result;
    }

    public void setParameterNameCaseSensitive(boolean parameterNameCaseSensitive) {
        this.parameterNameCaseSensitive = parameterNameCaseSensitive;
    }

    /**
     * Compare URLs.
     * will raise assertion errors if urls are not equal.
     */
    public void compare() {
        if(expected.equals(result)) return;

        //compare ignoring parameters order
        int split1 = expected.indexOf('?');
        int split2 = result.indexOf('?');
        assertEquals(split1, split2);
        if (split1>0) {
            //
            final String base1 = expected.substring(0,split1);
            final String base2 = result.substring(0,split1);
            assertEquals(base1,base2);

            final String params1str = expected.substring(split1+1);
            final String params2str = result.substring(split1+1);
            final Map<String, Object> params1 = toParameters(params1str);
            final Map<String, Object> params2 = toParameters(params2str);
            assertEquals(params1, params2);

        } else {
            fail("URLs are not equal : \n"+expected+"\n"+result);
        }
    }

    private Map<String,Object> toParameters(String params) {
        final Map<String,Object> map = new HashMap<>();
        for (String param : params.split("&")) {
            int idx = param.indexOf('=');
            String name;
            Object value;
            if (idx>0) {
                name = param.substring(0, idx);
                value = param.substring(idx+1);
            } else {
                name = param;
                value = NO_VALUE;
            }
            if (!parameterNameCaseSensitive) {
               name = name.toLowerCase();
            }
            map.put(name, value);
        }
        return map;
    }
}
