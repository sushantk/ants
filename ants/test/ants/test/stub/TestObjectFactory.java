package ants.test.stub;

import ants.api.Configurable;
import ants.api.IObjectFactory;
import ants.exception.ObjectConfigureException;
import ants.exception.ParseException;

public class TestObjectFactory implements IObjectFactory {

    private Configurable instance;

    public TestObjectFactory(Configurable instance) {
        this.instance = instance;
    }

    @Override
    public Configurable create(String id, String defaultClass, String tagName)
            throws ObjectConfigureException, ParseException {
        return this.instance;
    }

}
