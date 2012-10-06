package ants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.annotation.ConfigurableClass;
import ants.annotation.ConfigurableMethod;
import ants.api.Configurable;
import ants.exception.ObjectConfigureException;
import ants.exception.ObjectCreateException;
import ants.exception.ObjectIncompleteException;
import ants.exception.ObjectParseException;

public class ObjectFactory {

    static final Logger logger = LoggerFactory.getLogger(ObjectFactory.class);
    static JsonFactory jsonMapper = new MappingJsonFactory();
    static JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
    
    static int autoId = 0;

    /**
     * parse a json string into an object tree.
     * 
     * @throws ObjectParseException
     */
    public ObjectTree parse(String json) throws ObjectParseException {
        try {
            return this.parse(json,
                    new ByteArrayInputStream(json.getBytes("utf-8")));
        } catch (UnsupportedEncodingException ex) {
            // ignored, we dont expect this with utf-8
            throw new RuntimeException(ex);
        }
    }

    /**
     * parse a json stream into an object tree.
     * 
     * @throws ObjectParseException
     */
    public ObjectTree parse(String identifier, InputStream json)
            throws ObjectParseException {
        JsonNode node = null;
        try {
            JsonParser parser = jsonMapper.createJsonParser(json);
            node = parser.readValueAsTree();
        } catch (IOException ex) {
            throw new ObjectParseException("Failed to parse JSON: "
                    + identifier, ex);
        }

        return new ObjectTree(node);
    }

    /**
     * create and configure the object with the given object tree
     * 
     * @throws ObjectConfigureException
     */
    public Configurable configure(ObjectTree tree, String defaultClass,
            String tagName, String id, String defaultListItemClass,
            String listItemTag) throws ObjectConfigureException {
        Stack<String> tagStack = new Stack<String>();

        Exception ex = null;
        try {
            return this.configure(tree.getNode(), defaultClass, tagName,
                    id, defaultListItemClass, listItemTag, tagStack);
        } catch (SecurityException e) {
            ex = e;
        } catch (IllegalArgumentException e) {
            ex = e;
        } catch (NoSuchMethodException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        } catch (InvocationTargetException e) {
            ex = e;
        } catch (ObjectCreateException e) {
            ex = e;
        } catch (ObjectIncompleteException e) {
            ex = e;
        } catch (ClassNotFoundException e) {
            ex = e;
        } catch (InstantiationException e) {
            ex = e;
        }

        String tagStackString = Util.implode(tagStack, "=>");
        throw new ObjectConfigureException("Failed to configure: "
                + tagStackString, ex);
    }

    /**
     * Internal method to create and configure the object recursively with the
     * given node
     */
    private Configurable configure(JsonNode node,
            String defaultClass, String tagName, String id,
            String defaultListItemClass, String listItemTag,
            Stack<String> tagStack)
            throws ObjectCreateException, ObjectIncompleteException,
            SecurityException, IllegalArgumentException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException,
            ClassNotFoundException, InstantiationException {
        
        Configurable object = ObjectFactory.create(node, defaultClass,
                tagName, id);
        tagStack.push(object.toTagString());
        
        logger.debug("Configuring node: {}", tagStack.peek());
        Class<?> klass = object.getClass();
        ConfigurableClass kannotation = klass.getAnnotation(ConfigurableClass.class);
        if (node.isValueNode()) {
            Method method = klass.getMethod(Const.setValue, String.class);
            method.invoke(object, node.asText());
        } else if (node.isArray() || ((null != kannotation) && kannotation.expectsList())) {
            this.configureWithList(object, node, defaultListItemClass, listItemTag, tagStack);
        } else if (node.isObject()) {
            this.configureWithObjects(object, (ObjectNode) node, tagStack);
        }
        
        tagStack.pop();
        return object;
    }

    /**
     * Internal method to configure the object recursively with the given node's
     * children, treating them as child objects
     */
    private void configureWithObjects(Configurable object, ObjectNode node,
            Stack<String> tagStack) throws ObjectCreateException,
            ObjectIncompleteException, SecurityException,
            IllegalArgumentException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException,
            ClassNotFoundException, InstantiationException {
        HashSet<String> setMethods = new HashSet<String>();

        Class<?> klass = object.getClass();
        ConfigurableClass kannotation = klass.getAnnotation(ConfigurableClass.class);
        boolean expectsValue = (null == kannotation ? false : kannotation.expectsValue());
        Iterator<Entry<String, JsonNode>> it = node.getFields();
        while (it.hasNext()) {
            Entry<String, JsonNode> entry = it.next();
            String childTagName = entry.getKey();
            JsonNode childNode = entry.getValue();
            
            // attributes
            if (childTagName.startsWith("@")) {
                object.setAttribute(childTagName.substring(1), childNode.asText()); 
                continue;
            }

            String setMethodName = Const.set
                    + childTagName.substring(0, 1).toUpperCase()
                    + childTagName.substring(1);
            Method method;
            if(expectsValue) {
                method = klass.getMethod(setMethodName, String.class);
                method.invoke(object, childNode.asText());
            } else {
                method = klass.getMethod(setMethodName, Configurable.class);
                ConfigurableMethod mannotation = method
                    .getAnnotation(ConfigurableMethod.class);
            
                String defaultClass = "";
                String defaultListItemClass = "";
                String listItemTag = "";
                if(null != mannotation) {
                    defaultClass = mannotation.defaultClass();
                    defaultListItemClass = mannotation.defaultListItemClass();
                    listItemTag = mannotation.listItemTag();
                }
    
                Configurable childObject = this.configure(childNode,
                            defaultClass, childTagName, "",
                            defaultListItemClass, listItemTag, tagStack);
                method.invoke(object, childObject);
            }

            setMethods.add(setMethodName);
        }

        ObjectFactory.verify(object, setMethods);
    }

    /**
     * Internal method to configure the object recursively with the given node's
     * children treating them as a list of child objects
     */
    private void configureWithList(Configurable object, JsonNode node,
            String defaultListItemClass, String listItemTag,
            Stack<String> tagStack)
            throws ObjectCreateException, ObjectIncompleteException,
            SecurityException, IllegalArgumentException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException,
            ClassNotFoundException, InstantiationException {
        Class<?> klass = object.getClass();
        Method method = klass.getMethod(Const.setList, LinkedHashMap.class);
        
        ConfigurableMethod mannotation = method
                .getAnnotation(ConfigurableMethod.class);
        if(null != mannotation) {
            if(listItemTag.isEmpty()) {
                listItemTag = mannotation.listItemTag();
            }
            if(defaultListItemClass.isEmpty()) {
                defaultListItemClass = mannotation.defaultListItemClass();
            }
        }

        LinkedHashMap<String, Configurable> childList = new LinkedHashMap<String, Configurable>();
        if (node.isArray()) {
            Iterator<JsonNode> it = node.getElements();
            while (it.hasNext()) {
                Configurable childObject = this.configure(it.next(),
                        defaultListItemClass, listItemTag, "", "", "", tagStack);
                childList.put(childObject.getId(), childObject);
            }
        } else if (node.isObject()) {
            Iterator<Entry<String, JsonNode>> it = node.getFields();
            while (it.hasNext()) {
                Entry<String, JsonNode> entry = it.next();
                String childId = entry.getKey();
                JsonNode childNode = entry.getValue();

                // attributes
                if (childId.startsWith("@")) {
                    object.setAttribute(childId.substring(1), childNode.asText()); 
                    continue;
                }

                Configurable childObject = this.configure(childNode,
                        defaultListItemClass, listItemTag, childId, "", "", tagStack);
                childList.put(childObject.getId(), childObject);
            }
        }

        method.invoke(object, childList);
    }

    /**
     * Verifies if all required parameters are set on an object after it is
     * configured
     */
    private static void verify(Configurable object, Set<String> setMethods)
            throws ObjectIncompleteException {
        ArrayList<String> missingParams = new ArrayList<String>();

        Class<?> klass = object.getClass();
        Method[] methods = klass.getMethods();
        for (Method method : methods) {
            ConfigurableMethod mannotation = method
                    .getAnnotation(ConfigurableMethod.class);
            if (null != mannotation) {
                String methodName = method.getName();
                boolean required = mannotation.required();
                if (required && !setMethods.contains(methodName)) {
                    missingParams.add(methodName.substring(3).toLowerCase());
                }
            }
        }

        if (missingParams.size() > 0) {
            String error = "Missing required parameters: "
                    + Util.implode(missingParams, Const.comma);
            throw new ObjectIncompleteException(error);
        }
    }

    /**
     * instantiate an Configurable object using the class name specified in the
     * JsonNode or the provided default class name.
     */
    private static Configurable create(JsonNode node, String defaultClass,
            String tagName, String id) throws ObjectCreateException,
            ClassNotFoundException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        String className = defaultClass;
        if (node.isObject()) {
            JsonNode classNode = node.get(Const.attribute.nodeClass);
            if (null != classNode) {
                className = classNode.asText();
            }
        }
        if (className.isEmpty()) {
            if (node.isValueNode()) {
                className = Const.thefault.StringClass;
            } else {
                className = Const.thefault.ParamsClass;
            }
        }

        if (id.isEmpty()) {
            JsonNode idNode = node.get(Const.attribute.nodeId);
            if (null != idNode) {
                id = idNode.asText();
            }
            if(id.isEmpty()) {
                id = String.format("_id_%d", ObjectFactory.autoId++);
            }
        }

        Class<?> klass = Class.forName(className);
        Constructor<?> tc = klass.getConstructor(String.class, String.class);
        Object t = tc.newInstance(tagName, id);
        return (Configurable) t;
    }
}
