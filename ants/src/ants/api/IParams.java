package ants.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ants.exception.ObjectEvaluateException;

public interface IParams {

    public static abstract class SimpleOrComplex {
        abstract public String getSimple();

        abstract public LinkedHashMap<String, SimpleOrComplex> getComplex();
    }
    
    public static class Simple extends SimpleOrComplex {
        String string;

        public Simple(String string) {
            this.string = string;
        }
        
        @Override
        public java.lang.String getSimple() {
            return string;
        }

        @Override
        public LinkedHashMap<String, SimpleOrComplex> getComplex() {
            return null;
        }
        
        public String toString() {
            return this.string;
        }
    }

    public static class Complex extends SimpleOrComplex {
        LinkedHashMap<String, SimpleOrComplex> complex;

        public Complex(LinkedHashMap<String, SimpleOrComplex> complex) {
            this.complex = complex;
        }

        @Override
        public String getSimple() {
            if(1 != this.complex.size()) {
                return null;
            }

            return this.complex.values().iterator().next().getSimple();
        }

        @Override
        public LinkedHashMap<String, SimpleOrComplex> getComplex() {
            return complex;
        }
        
        public String toString() {
            return this.complex.toString();
        }
    }

    LinkedHashMap<String, SimpleOrComplex> getPairs(Context context,
            boolean simple) throws ObjectEvaluateException;
}
