package ants.api;

import java.util.LinkedHashMap;

import ants.exception.EvaluateException;

public interface IList {

    LinkedHashMap<String, Configurable> getItems(ExecuteContext context)
            throws EvaluateException;
}
