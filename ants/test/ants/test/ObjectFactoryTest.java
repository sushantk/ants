package ants.test;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.junit.Test;

import static org.junit.Assert.*;

import ants.ObjectFactory;
import ants.ObjectTree;
import ants.ParamsDefault;
import ants.StringDefault;
import ants.annotation.ConfigurableClass;
import ants.annotation.ConfigurableMethod;
import ants.api.Configurable;
import ants.exception.ObjectConfigureException;
import ants.exception.ObjectCreateException;
import ants.exception.ObjectIncompleteException;
import ants.exception.ObjectParseException;

public class ObjectFactoryTest {

    @Test
    public void testParse() {
        ObjectFactory factory = new ObjectFactory();
        
        boolean parseFailure = false;
        try {
            factory.parse("??");
        } catch(ObjectParseException ex) {
            parseFailure = true;
        }
        assertTrue("Invalid json parsing", parseFailure);        

        parseFailure = false;
        try {
            factory.parse("[]");
        } catch(ObjectParseException ex) {
            parseFailure = true;
        }
        assertFalse("Non object json parsing", parseFailure);        

        parseFailure = false;
        try {
            factory.parse("{}");
        } catch(ObjectParseException ex) {
            parseFailure = true;
        }
        assertFalse("object json parsing", parseFailure);
    }

    @ConfigurableClass(expectsList=true)
    public static class TestList extends Configurable {
        
        public TestList(String tagName, String id) {
            super(tagName, id);
        }

        LinkedHashMap<String, Configurable> children;
        
        LinkedHashMap<String, Configurable> getChildren() {
            return this.children;
        }

        @ConfigurableMethod(listItemTag="child")
        public void setList(LinkedHashMap<String, Configurable> children) {
            this.children = children;
        }        
    }

    @ConfigurableClass(expectsList=true)
    public static class TestParams extends Configurable {
        
        public TestParams(String tagName, String id) {
            super(tagName, id);
        }

        LinkedHashMap<String, Configurable> children;
        
        LinkedHashMap<String, Configurable> getChildren() {
            return this.children;
        }

        @ConfigurableMethod(listItemTag="param")
        public void setList(LinkedHashMap<String, Configurable> children) {
            this.children = children;
        }        
    }

    public static class TestClass extends Configurable {
        
        public TestClass(String tagName, String id) {
            super(tagName, id);
        }

        Configurable requiredChild;
        Configurable notRequiredChild;
        Configurable childObject;
        Configurable childParams;
        TestList childList;
        TestParams childMap;
        
        Configurable getRequiredChild() {
            return this.requiredChild;
        }

        Configurable getNotRequiredChild() {
            return this.notRequiredChild;
        }

        Configurable getChildObject() {
            return this.childObject;
        }

        Configurable getChildParams() {
            return this.childParams;
        }

        LinkedHashMap<String, Configurable> getChildList() {
            return this.childList.getChildren();
        }

        LinkedHashMap<String, Configurable> getChildMap() {
            return this.childMap.getChildren();
        }

        @ConfigurableMethod(required=true)
        public void setRequiredChild(Configurable child) {
            this.requiredChild = child;
        }
        
        public void setNotRequiredChild(Configurable child) {
            this.notRequiredChild = child;
        }

        public void setChildObject(Configurable child) {
            this.childObject = child;
        }

        public void setChildParams(Configurable child) {
            this.childParams = child;
        }
        
        @ConfigurableMethod(defaultClass="ants.test.ObjectFactoryTest$TestList", listItemTag="item")
        public void setChildList(Configurable list) {
            this.childList = (TestList)list;
        }
     
        @ConfigurableMethod(defaultClass="ants.test.ObjectFactoryTest$TestParams")
        public void setChildMap(Configurable map) {
            this.childMap = (TestParams)map;
        }
    }

    @Test
    public void testChildren() throws ObjectParseException, ObjectCreateException, ObjectConfigureException, ObjectIncompleteException {
        String jsonFile = "data/ObjectFactory/childObjectTree.json";
        InputStream is = this.getClass().getResourceAsStream(jsonFile);
        ObjectFactory factory = new ObjectFactory();
        ObjectTree tree = factory.parse(jsonFile, is);
        
        TestClass testObject = (TestClass)factory.configure(tree, TestClass.class.getName(), "test", "t1", "", "");
        
        assertEquals("Tag is populated", "test", testObject.getTag());
        assertEquals("Id is populated", "t1", testObject.getId());
        assertEquals("Required child should be a string", StringDefault.class, testObject.getRequiredChild().getClass());
        assertEquals("Not required child should be a StringDefault", StringDefault.class, testObject.getNotRequiredChild().getClass());
        assertEquals("Child object should be of type TestClass", TestClass.class, testObject.getChildObject().getClass());
        assertEquals("Child list should be configured", 2, testObject.getChildList().size());
        assertEquals("Child map should be configured", 2, testObject.getChildMap().size());
        assertEquals("Child params should be ParamsDefault", ParamsDefault.class, testObject.getChildParams().getClass());
    }
    
    @Test
    public void testMissingChildren() throws ObjectParseException, ObjectCreateException, ObjectConfigureException {
        ObjectFactory factory = new ObjectFactory();
        ObjectTree tree = factory.parse("{}");
        
        boolean incomplete = false;
        try {
            factory.configure(tree, TestClass.class.getName(), "test", "t1", "", "");
        } catch (ObjectConfigureException e) {
            incomplete = true;
        }
        assertTrue("Incomplete object cannot be configured", incomplete);
    }

    @Test
    public void testParamsArray() throws ObjectParseException, ObjectCreateException, ObjectConfigureException, ObjectIncompleteException {
        String jsonFile = "data/ObjectFactory/paramsArrayTree.json";
        InputStream is = this.getClass().getResourceAsStream(jsonFile);
        ObjectFactory factory = new ObjectFactory();
        ObjectTree tree = factory.parse(jsonFile, is);
        
        TestList testList = (TestList)factory.configure(tree, TestList.class.getName(), "params", "", "", "child");
        
        LinkedHashMap<String, Configurable> children = testList.getChildren();
        assertEquals("Params have a list of children", 2, children.size());
        Iterator<Configurable> collection = children.values().iterator();
        
        Configurable first = collection.next();
        Configurable second = collection.next();
        
        assertEquals("First children is a string", StringDefault.class, first.getClass());
        assertEquals("Child tag is set", "child", first.getTag());
        assertTrue("First children has auto id", !first.getId().isEmpty());
        assertEquals("Child tag is set", "child", second.getTag());
        assertEquals("Second children has id", "i2", second.getId());
    }        
    
    @Test
    public void testParamsMap() throws ObjectParseException, ObjectCreateException, ObjectConfigureException, ObjectIncompleteException {
        String jsonFile = "data/ObjectFactory/paramsMapTree.json";
        InputStream is = this.getClass().getResourceAsStream(jsonFile);
        ObjectFactory factory = new ObjectFactory();
        ObjectTree tree = factory.parse(jsonFile, is);
        
        TestParams testParams = (TestParams)factory.configure(tree, TestParams.class.getName(), "params", "", "", "testParam");

        LinkedHashMap<String, Configurable> children = testParams.getChildren();
        assertEquals("Params have a list of children", 2, children.size());
        Iterator<Configurable> collection = children.values().iterator();        
        
        Configurable first = collection.next();
        Configurable second = collection.next();
        
        assertEquals("Child tag is set", "testParam", first.getTag());
        assertEquals("Child tag is set", "testParam", second.getTag());
        assertEquals("First child is object", TestClass.class, first.getClass());
        assertEquals("Second child is object", TestClass.class, second.getClass());
        
        HashSet<String> idSet = new HashSet<String>();
        idSet.add("i1");
        idSet.add("i2");
        assertTrue("All ids are set", children.keySet().equals(idSet));
    }        

}
