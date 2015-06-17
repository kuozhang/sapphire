package org.eclipse.sapphire.ui.cspext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CspShapeAdd {
    Class<? extends CspShapeAddHandler> handler();
}
