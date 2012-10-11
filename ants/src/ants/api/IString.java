package ants.api;

import ants.exception.ObjectEvaluateException;

public interface IString {
    
    String getValue(ExecuteContext context)  throws ObjectEvaluateException;
}
