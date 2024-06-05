package com.team766.framework.annotations;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

class ProcessorUtils {
    public static String getVisibilityString(Element element) {
        var modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PUBLIC)) {
            return "public";
        }
        if (modifiers.contains(Modifier.PROTECTED)) {
            return "protected";
        }
        if (modifiers.contains(Modifier.PRIVATE)) {
            return "private";
        }
        return "";
    }

    private static List<Element> getAllContainedElementsWithAnnotation(
            List<Element> result,
            Element root,
            Class<? extends Annotation> annotationType,
            boolean includeSupers) {
        if (root.getAnnotation(annotationType) != null) {
            result.add(root);
        }

        for (var element : root.getEnclosedElements()) {
            getAllContainedElementsWithAnnotation(result, element, annotationType, includeSupers);
        }

        if (includeSupers && root instanceof TypeElement type) {
            var superClass = type.getSuperclass();
            if (superClass.getKind() != TypeKind.NONE) {
                getAllContainedElementsWithAnnotation(
                        result,
                        ((DeclaredType) superClass).asElement(),
                        annotationType,
                        includeSupers);
            }
            for (var intf : type.getInterfaces()) {
                getAllContainedElementsWithAnnotation(
                        result, ((DeclaredType) intf).asElement(), annotationType, includeSupers);
            }
        }

        return result;
    }

    public static List<Element> getAllContainedElementsWithAnnotation(
            Element root, Class<? extends Annotation> annotationType, boolean includeSupers) {
        return getAllContainedElementsWithAnnotation(
                new ArrayList<>(), root, annotationType, includeSupers);
    }

    public static DeclaredType findSuperclass(
            Types typeUtils, TypeMirror clazz, TypeMirror superclass) {
        while (!typeUtils.isSameType(clazz, superclass)) {
            var supers = typeUtils.directSupertypes(clazz);
            if (supers.isEmpty()) {
                return null;
            }
            clazz = supers.get(0);
        }
        if (clazz.getKind() != TypeKind.DECLARED) {
            return null;
        }
        return (DeclaredType) clazz;
    }

    public static List<? extends TypeMirror> getClassesValue(Supplier<Class<?>[]> accessor) {
        try {
            accessor.get();
        } catch (javax.lang.model.type.MirroredTypesException ex) {
            return ex.getTypeMirrors();
        }
        throw new RuntimeException("Expected MirroredTypesException was not thrown");
    }
}
