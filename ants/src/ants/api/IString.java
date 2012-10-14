package ants.api;

import ants.exception.EvaluateException;

public interface IString {
    
    String getValue(ExecuteContext context)  throws EvaluateException;
}
