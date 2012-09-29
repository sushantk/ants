package ants.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigurableMethod {

    boolean required() default false;
    String defaultClass() default "";

    String defaultListItemClass() default "";
    String listItemTag() default "";
}
