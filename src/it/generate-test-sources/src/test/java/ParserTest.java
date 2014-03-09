import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;

import org.junit.Assert;
import org.junit.Test;

public class ParserTest {
    @Test
    public void test() throws Exception {
        List<XMLInputFactory> factories = new ArrayList<XMLInputFactory>();
        BufferedReader in = new BufferedReader(new InputStreamReader(ParserTest.class.getResourceAsStream("parsers.list"), "utf-8"));
        Thread currentThread = Thread.currentThread();
        try {
            ClassLoader savedTCCL = currentThread.getContextClassLoader();
            try {
                String url;
                while ((url = in.readLine()) != null) {
                    ClassLoader classLoader = new URLClassLoader(new URL[] { new URL(url) });
                    currentThread.setContextClassLoader(classLoader);
                    factories.add(XMLInputFactory.newFactory());
                }
            } finally {
                currentThread.setContextClassLoader(savedTCCL);
            }
        } finally {
            in.close();
        }
        assertEquals(2, factories.size());
        Class<? extends XMLInputFactory> defaultFactoryClass = XMLInputFactory.newFactory().getClass();
        for (XMLInputFactory factory : factories) {
            Assert.assertNotSame(defaultFactoryClass, factory.getClass());
        }
    }
}
