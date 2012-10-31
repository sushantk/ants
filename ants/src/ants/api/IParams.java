package ants.api;

import java.util.LinkedHashMap;

import ants.exception.EvaluateException;

public interface IParams {

    public static abstract class Type {
        public String getSimple() {
            return null;
        }

        public LinkedHashMap<String, Type> getComplex() {
            return null;
        }
        
        public Object getCustom() {
            return null;
        }
    }
    
    public static class Simple extends Type {
        String string;

        public Simple(String string) {
            this.string = string;
        }
        
        @Override
        public java.lang.String getSimple() {
            return string;
        }

        public String toString() {
            return this.string;
        }
    }

    public static class Complex extends Type {
        LinkedHashMap<String, Type> complex;

        public Complex(LinkedHashMap<String, Type> complex) {
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
        public LinkedHashMap<String, Type> getComplex() {
            return complex;
        }
        
        public String toString() {
            return this.complex.toString();
        }
    }

    public static class Custom extends Type {
        Object object;

        public Custom(Object object) {
            this.object = object;
        }
        
        public String toString() {
            return (null == this.object ? null : this.object.toString());
        }
    }

    LinkedHashMap<String, Type> getPairs(ContextModule context,
            boolean simple) throws EvaluateException;
}
