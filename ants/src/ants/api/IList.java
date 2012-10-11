package ants.api;

import java.util.LinkedHashMap;

import ants.exception.ObjectEvaluateException;

public interface IList {

    LinkedHashMap<String, Configurable> getItems(ExecuteContext context)
            throws ObjectEvaluateException;
}
