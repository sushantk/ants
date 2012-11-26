package ants.core;

import java.util.LinkedHashMap;

import ants.exception.EvaluateException;

public interface IList {

    LinkedHashMap<String, Configurable> getItems(ContextModule context)
            throws EvaluateException;
}
