package ants.test;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import ants.ParamsDefault;
import ants.core.ContextModule;
import ants.core.ObjectFactory;
import ants.core.IParams.Type;
import ants.exception.ObjectConfigureException;
import ants.exception.EvaluateException;
import ants.exception.ParseException;
import ants.test.stub.TestModuleContext;

public class ParamsDefaultTest {

    @Test
    public void testSimple() throws ParseException, ObjectConfigureException, EvaluateException {
        String jsonFile = "data/ParamsDefault/simple.json";
        InputStream is = this.getClass().getResourceAsStream(jsonFile);
        JsonNode tree = ObjectFactory.parse(jsonFile, is);
        
        ContextModule context = new TestModuleContext();
        ParamsDefault params = (ParamsDefault)ObjectFactory.configure(tree, ParamsDefault.class.getName(), "", "", "", "");
        LinkedHashMap<String, Type> result = params.getPairs(context, true);
        assertEquals("Simple params size", 2, result.size());
        assertEquals("Simple params name value", "v1", result.get("n1").getSimple());
        assertEquals("Simple params name value", "v2", result.get("n2").getSimple());
    }

    @Test
    public void testSimpleOrder() throws ParseException, ObjectConfigureException, EvaluateException {
        String jsonFile = "data/ParamsDefault/simpleArray.json";
        InputStream is = this.getClass().getResourceAsStream(jsonFile);
        JsonNode tree = ObjectFactory.parse(jsonFile, is);
        
        ContextModule context = new TestModuleContext();
        ParamsDefault params = (ParamsDefault)ObjectFactory.configure(tree, ParamsDefault.class.getName(), "", "", "", "");
        LinkedHashMap<String, Type> result = params.getPairs(context, true);
        assertEquals("Simple params size", 3, result.size());
        
        Iterator<String> niter = result.keySet().iterator();
        assertEquals("Simple params name 1", "n1", niter.next());
        assertEquals("Simple params name 2", "n2", niter.next());
        assertEquals("Simple params name 3", "n3", niter.next());
        
        Iterator<Type> viter = result.values().iterator();
        assertEquals("Simple params value 1", "v1", viter.next().getSimple());
        assertEquals("Simple params value 2", "v2", viter.next().getSimple());
        assertEquals("Simple params value 3", "v3", viter.next().getSimple());
    }
    
    @Test
    public void testComplexAsSimple() throws ParseException, ObjectConfigureException {
        String jsonFile = "data/ParamsDefault/complex.json";
        InputStream is = this.getClass().getResourceAsStream(jsonFile);
        JsonNode tree = ObjectFactory.parse(jsonFile, is);
        
        ContextModule context = new TestModuleContext();
        ParamsDefault params = (ParamsDefault)ObjectFactory.configure(tree, ParamsDefault.class.getName(), "", "", "", "");
        boolean failed = false;
        try {
            params.getPairs(context, true);
        } catch(EvaluateException e) {
            failed = true;
        }
        
        assertTrue("Complex node cannot be parsed as simple", failed);
    }
    
    @Test
    public void testComplex() throws ParseException, ObjectConfigureException, EvaluateException {
        String jsonFile = "data/ParamsDefault/complex.json";
        InputStream is = this.getClass().getResourceAsStream(jsonFile);
        JsonNode tree = ObjectFactory.parse(jsonFile, is);
        
        ContextModule context = new TestModuleContext();
        ParamsDefault params = (ParamsDefault)ObjectFactory.configure(tree, ParamsDefault.class.getName(), "", "", "", "");
        LinkedHashMap<String, Type> result = params.getPairs(context, false);
        System.out.println(result);
        
        assertEquals("Complex params size", 2, result.size());
        assertEquals("Complex as simple value 1", "v1", result.get("n1").getSimple());
        assertEquals("Complex not as simple", null, result.get("n2").getSimple());
        
        LinkedHashMap<String, Type> complex = result.get("n2").getComplex();
        assertEquals("Nested complex params size", 2, complex.size());
        assertEquals("Nested complex value 1", "v21", complex.get("n21").getSimple());
        assertEquals("Nested complex value 2", "v22", complex.get("n22").getSimple());
    }
    
    @Test
    public void testMultiValue() throws ParseException, ObjectConfigureException, EvaluateException {
        String jsonFile = "data/ParamsDefault/multivalue.json";
        InputStream is = this.getClass().getResourceAsStream(jsonFile);
        JsonNode tree = ObjectFactory.parse(jsonFile, is);
        
        ContextModule context = new TestModuleContext();
        ParamsDefault params = (ParamsDefault)ObjectFactory.configure(tree, ParamsDefault.class.getName(), "", "", "", "");
        LinkedHashMap<String, Type> result = params.getPairs(context, false);

        assertEquals("Simple params size", 1, result.size());

        LinkedHashMap<String, Type> value = result.get("n1").getComplex();
        assertEquals("Multivalue size", 2, value.size());
        assertEquals("Multivalue value 1", "v1", value.get("id1").getSimple());
        assertEquals("Multivalue value 2", "v2", value.get("id2").getSimple());
    }

    @Test
    public void testMultiValueArray() throws ParseException, ObjectConfigureException, EvaluateException {
        String jsonFile = "data/ParamsDefault/multivalueArray.json";
        InputStream is = this.getClass().getResourceAsStream(jsonFile);
        JsonNode tree = ObjectFactory.parse(jsonFile, is);
        
        ContextModule context = new TestModuleContext();
        ParamsDefault params = (ParamsDefault)ObjectFactory.configure(tree, ParamsDefault.class.getName(), "", "", "", "");
        LinkedHashMap<String, Type> result = params.getPairs(context, false);

        assertEquals("Simple params size", 1, result.size());
        
        LinkedHashMap<String, Type> value = result.get("n1").getComplex();
        assertEquals("Multivalue size", 2, value.size());
        Iterator<Type> viter = value.values().iterator();
        assertEquals("Multivalue value 1", "v1", viter.next().getSimple());
        assertEquals("Multivalue value 2", "v2", viter.next().getSimple());
    }
}
