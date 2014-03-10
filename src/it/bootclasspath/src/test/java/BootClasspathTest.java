import static org.junit.Assert.fail;

import javax.xml.soap.SOAPElement;

import org.junit.Test;
import org.w3c.dom.Node;

public class BootClasspathTest {
    @Test
    public void testDOM2() {
        // Test that the DOM API is level 2, not level 3
        try {
            Node.class.getMethod("getTextContent");
            fail("Expected NoSuchMethodException");
        } catch (NoSuchMethodException ex) {
            // Expected
        }
    }
    
    @Test
    public void testSAAJ12() {
        // Test that the SAAJ API is version 1.2, not 1.3
        try {
            SOAPElement.class.getMethod("getElementQName");
            fail("Expected NoSuchMethodException");
        } catch (NoSuchMethodException ex) {
            // Expected
        }
    }
}
