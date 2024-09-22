package com.team766.framework.annotations;

import com.google.auto.service.AutoService;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({
    "com.team766.framework.annotations.PreventReservableFields",
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class RobotSystemsProcessor extends AbstractProcessor {
    private Messager messager;
    private Types typeUtils;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Set<? extends Element> preventReservableFieldsElements =
                roundEnv.getElementsAnnotatedWith(PreventReservableFields.class);
        final var reservableType =
                elementUtils.getTypeElement("com.team766.framework.resources.Reservable").asType();
        for (var annotatedElement : preventReservableFieldsElements) {
            final var annotatedClass = (TypeElement) annotatedElement;
            for (var element : annotatedClass.getEnclosedElements()) {
                if (!element.getKind().isField()) {
                    continue;
                }
                final var fieldType = ((VariableElement) element).asType();
                if (typeUtils.isAssignable(fieldType, reservableType)) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            fieldType.toString()
                                    + " cannot be contained in another RobotSystem. Try changing it to a RobotSubsystem.",
                            element);
                }
            }
        }

        return false;
    }
}
