package com.team766.framework.annotations;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({
    "com.team766.framework.annotations.PreventReservableFields",
    "com.team766.framework.annotations.ReservableAnnotation",
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class RobotSystemsProcessor extends AbstractProcessor {
    public static CharSequence getSystemReservationInterfaceName(
            CharSequence robotSystemClassName) {
        return robotSystemClassName + "_GeneratedReservation";
    }

    private Messager messager;
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Set<? extends Element> preventReservableFieldsElements =
                roundEnv.getElementsAnnotatedWith(PreventReservableFields.class);
        final var reservableType = elementUtils
                .getTypeElement("com.team766.framework.resources.Reservable")
                .asType();
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

        final Set<? extends Element> reservableElements =
                roundEnv.getElementsAnnotatedWith(ReservableAnnotation.class);
        for (var annotatedElement : reservableElements) {
            final var annotatedClass = (TypeElement) annotatedElement;
            if (annotatedClass.getModifiers().contains(Modifier.ABSTRACT)) {
                continue;
            }
            try {
                var packageName = elementUtils.getPackageOf(annotatedClass).getQualifiedName();
                var className = annotatedClass.getSimpleName();
                var visibility = ProcessorUtils.getVisibilityString(annotatedClass);
                var generatedClassName = getSystemReservationInterfaceName(className);
                var builderFile = filer.createSourceFile(
                        packageName + "." + generatedClassName, annotatedClass);
                try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                    out.println("package %s;".formatted(packageName));
                    out.println("");
                    out.println("import com.team766.framework.GenericRobotSystemProvider;");
                    out.println("import edu.wpi.first.wpilibj2.command.Subsystem;");
                    out.println("import java.util.function.Consumer;");
                    out.println("");
                    out.println("%s interface %s extends GenericRobotSystemProvider {"
                            .formatted(visibility, generatedClassName));
                    out.println("    default %1$s get%1$s() {".formatted(className));
                    out.println("        return getRobotSystem(%s.class);".formatted(className));
                    out.println("    }");
                    out.println("");
                    out.println("    static void addReservations(%s r, Consumer<Subsystem> add) {"
                            .formatted(generatedClassName));
                    out.println("        add.accept(r.get%1$s());".formatted(className));
                    out.println("    }");
                    out.println("}");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return false;
    }
}
