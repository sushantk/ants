package ants.core;

import ants.exception.EvaluateException;

public interface IString {
    
    String getValue(ContextModule context)  throws EvaluateException;
}
