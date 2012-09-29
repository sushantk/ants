package ants.test;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import junit.framework.TestListener;

import org.junit.Test;

import com.sun.corba.se.spi.orbutil.fsm.Input;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import static org.junit.Assert.*;

import ants.ObjectFactory;
import ants.ObjectTree;
import ants.StringDefault;
import ants.annotation.ConfigurableClass;
import ants.annotation.ConfigurableMethod;
import ants.api.Configurable;
import ants.api.IConfigurable;
import ants.api.IList;
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

        LinkedHashMap<String, IConfigurable> children;
        
        LinkedHashMap<String, IConfigurable> getChildren() {
            return this.children;
        }

        @ConfigurableMethod(listItemTag="child")
        public void setList(LinkedHashMap<String, IConfigurable> children) {
            this.children = children;
        }        
    }

    @ConfigurableClass(expectsList=true)
    public static class TestParams extends Configurable {
        
        public TestParams(String tagName, String id) {
            super(tagName, id);
        }

        LinkedHashMap<String, IConfigurable> children;
        
        LinkedHashMap<String, IConfigurable> getChildren() {
            return this.children;
        }

        @ConfigurableMethod(listItemTag="param")
        public void setList(LinkedHashMap<String, IConfigurable> children) {
            this.children = children;
        }        
    }

    public static class TestClass extends Configurable {
        
        public TestClass(String tagName, String id) {
            super(tagName, id);
        }

        IConfigurable requiredChild;
        IConfigurable notRequiredChild;
        IConfigurable childObject;
        IConfigurable childParams;
        TestList childList;
        TestParams childMap;
        
        IConfigurable getRequiredChild() {
            return this.requiredChild;
        }

        IConfigurable getNotRequiredChild() {
            return this.notRequiredChild;
        }

        IConfigurable getChildObject() {
            return this.childObject;
        }

        IConfigurable getChildParams() {
            return this.childParams;
        }

        LinkedHashMap<String, IConfigurable> getChildList() {
            return this.childList.getChildren();
        }

        LinkedHashMap<String, IConfigurable> getChildMap() {
            return this.childMap.getChildren();
        }

        @ConfigurableMethod(required=true)
        public void setRequiredChild(IConfigurable child) {
            this.requiredChild = child;
        }
        
        public void setNotRequiredChild(IConfigurable child) {
            this.notRequiredChild = child;
        }

        public void setChildObject(IConfigurable child) {
            this.childObject = child;
        }

        public void setChildParams(IConfigurable child) {
            this.childParams = child;
        }
        
        @ConfigurableMethod(defaultClass="ants.test.ObjectFactoryTest$TestList", listItemTag="item")
        public void setChildList(IConfigurable list) {
            this.childList = (TestList)list;
        }
     
        @ConfigurableMethod(defaultClass="ants.test.ObjectFactoryTest$TestParams")
        public void setChildMap(IConfigurable map) {
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
        assertEquals("Not required child should be a string", StringDefault.class, testObject.getNotRequiredChild().getClass());
        assertEquals("Child object should be of type TestClass", TestClass.class, testObject.getChildObject().getClass());
        assertEquals("Child list should be configured", 2, testObject.getChildList().size());
        assertEquals("Child map should be configured", 2, testObject.getChildMap().size());
        
        // test params
        
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
        
        LinkedHashMap<String, IConfigurable> children = testList.getChildren();
        assertEquals("Params have a list of children", 2, children.size());
        Iterator<IConfigurable> collection = children.values().iterator();
        
        IConfigurable first = collection.next();
        IConfigurable second = collection.next();
        
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

        LinkedHashMap<String, IConfigurable> children = testParams.getChildren();
        assertEquals("Params have a list of children", 2, children.size());
        Iterator<IConfigurable> collection = children.values().iterator();        
        
        IConfigurable first = collection.next();
        IConfigurable second = collection.next();
        
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
