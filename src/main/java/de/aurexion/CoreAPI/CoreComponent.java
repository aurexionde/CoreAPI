package de.aurexion.CoreAPI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a core component for auto-registration
 * Supports: Commands (BaseCommand), Listeners (Listener)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CoreComponent {
    /**
     * Name of the initialization method to call after registration
     * @return Method name (default: "init")
     */
    String initMethod() default "init";
}

