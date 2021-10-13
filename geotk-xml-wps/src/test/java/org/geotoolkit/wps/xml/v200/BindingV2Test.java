package org.geotoolkit.wps.xml.v200;

import org.geotoolkit.wps.xml.XMLBindingTestBuilder;
import org.junit.Test;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class BindingV2Test {

    @Test
    public void getCapabilities() throws Exception {
        XMLBindingTestBuilder.test("xml/v200/GetCapabilities_response.xml", Capabilities.class);
        XMLBindingTestBuilder.test("xml/v200/WPSCapabilities_Examind.xml", Capabilities.class);
    }

    @Test
    public void describeProcess() throws Exception {
        XMLBindingTestBuilder.test("xml/v200/DescribeProcess_request.xml", DescribeProcess.class);
        XMLBindingTestBuilder.test("xml/v200/DescribeProcess_response.xml", ProcessOfferings.class);
        XMLBindingTestBuilder.test("xml/v200/ProcessOfferings.xml", ProcessOfferings.class);
    }

    @Test
    public void execute() throws Exception {
        XMLBindingTestBuilder.test("xml/v200/Execute.xml", Execute.class);
    }

    @Test
    public void getStatus() throws Exception {
        XMLBindingTestBuilder.test("xml/v200/StatusInfo.xml", StatusInfo.class);
    }

    @Test
    public void result() throws Exception {
        XMLBindingTestBuilder.test("xml/v200/Result_literal.xml", Result.class);
        XMLBindingTestBuilder.test("xml/v200/Result_reference.xml", Result.class);
    }
}
