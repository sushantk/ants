package ants.api;

import java.util.LinkedHashMap;

import ants.exception.ObjectEvaluateException;

public interface IList {

    LinkedHashMap<String, Configurable> getItems(Context context) throws ObjectEvaluateException;
}
