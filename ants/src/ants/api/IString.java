package ants.api;

import ants.exception.ObjectEvaluateException;

public interface IString {
    
    String getValue(Context context)  throws ObjectEvaluateException;
}
