package org.geotoolkit.wps.xml.v100;

import org.geotoolkit.wps.xml.XMLBindingTestBuilder;
import org.junit.Test;
import org.geotoolkit.wps.xml.v200.Capabilities;
import org.geotoolkit.wps.xml.v200.Execute;
import org.geotoolkit.wps.xml.v200.ProcessOfferings;
import org.geotoolkit.wps.xml.v200.Result;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class BindingV1Test {

    @Test
    public void GetCapabilities() throws Exception {
        XMLBindingTestBuilder.test("xml/v100/WPSCapabilities_Examind.xml", Capabilities.class);
    }

    @Test
    public void describeProcess() throws Exception {
        XMLBindingTestBuilder.test("xml/v100/ProcessDescriptions.xml", ProcessOfferings.class);
    }

    @Test
    public void execute() throws Exception {
        XMLBindingTestBuilder.test("xml/v100/Execute.xml", Execute.class);
        XMLBindingTestBuilder.test("xml/v100/Execute2.xml", Execute.class);
        XMLBindingTestBuilder.test("xml/v100/Execute3.xml", Execute.class);
    }

    @Test
    public void executeChain() throws Exception {
        XMLBindingTestBuilder.test("xml/v100/Execute_chain.xml", Execute.class);
    }

//    @Test
//    public void getStatus() throws Exception {
//        XMLBindingTestBuilder.test("xml/v200/StatusInfo.xml", StatusInfo.class);
//    }

    @Test
    public void result() throws Exception {
        XMLBindingTestBuilder.test("xml/v100/ExecuteResponse.xml", Result.class);
    }
}
