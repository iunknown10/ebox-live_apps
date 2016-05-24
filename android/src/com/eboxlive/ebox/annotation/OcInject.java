package com.eboxlive.ebox.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(
{ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
public @interface OcInject
{
	boolean optional() default false;
}
