package ants.api;

import ants.exception.EvaluateException;

public interface IString {
    
    String getValue(ContextModule context)  throws EvaluateException;
}
