package ants;

import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.annotation.ConfigurableClass;
import ants.annotation.ConfigurableMethod;
import ants.core.Configurable;
import ants.core.ContextModule;
import ants.core.IList;
import ants.exception.EvaluateException;

/**
 * Default list class to configure and hold a list of items
 */
@ConfigurableClass(expectsList = true)
public class ListDefault extends Configurable implements IList {
    static final Logger logger = LoggerFactory.getLogger(ListDefault.class);

    private LinkedHashMap<String, Configurable> list;

    public ListDefault(String tagName, String id) {
        super(tagName, id);
    }

    @ConfigurableMethod(required = true, defaultListItemClass = "ants.StringDefault", listItemTag = "item")
    public void setList(LinkedHashMap<String, Configurable> list) {
        this.list = list;
    }

    @Override
    public LinkedHashMap<String, Configurable> getItems(ContextModule context)
            throws EvaluateException {
        return this.list;
    }
}
