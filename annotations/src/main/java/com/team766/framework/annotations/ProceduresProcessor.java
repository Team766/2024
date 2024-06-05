package com.team766.framework.annotations;

import com.google.auto.service.AutoService;
import com.sun.source.tree.NewClassTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({
    "com.team766.framework.annotations.CollectReservations",
    "com.team766.framework.annotations.Reserve",
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class ProceduresProcessor extends AbstractProcessor {
    private record FieldInfo(CharSequence name, CharSequence type) {}

    public static CharSequence getProcedureReservationsInterfaceName(
            CharSequence robotSystemClassName) {
        return robotSystemClassName + "_Reservations";
    }

    private Messager messager;
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        trees = Trees.instance(processingEnv);
    }

    private class NewExpressionsScanner extends TreePathScanner<List<TypeElement>, Trees> {
        private List<TypeElement> classList = new ArrayList<>();

        public List<TypeElement> scan(Element element, Trees trees) {
            return this.scan(trees.getPath(element), trees);
        }

        @Override
        public List<TypeElement> scan(TreePath treePath, Trees trees) {
            super.scan(treePath, trees);
            return this.classList;
        }

        @Override
        public List<TypeElement> visitNewClass(NewClassTree node, Trees p) {
            if (trees.getElement(getCurrentPath()) instanceof ExecutableElement constructor) {
                TypeElement clazz = (TypeElement) constructor.getEnclosingElement();
                classList.add(clazz);
            }
            return super.visitNewClass(node, p);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // check that all Reserve elements are contained within a CollectReservations class.
        final Set<? extends Element> reserveElements =
                roundEnv.getElementsAnnotatedWith(Reserve.class);
        final var collectReservationsType = elementUtils
                .getTypeElement(CollectReservations.class.getCanonicalName())
                .asType();
        for (var annotatedField : reserveElements) {
            var clazz = annotatedField.getEnclosingElement();
            if (!hasAnnotation(clazz, collectReservationsType)) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "@Reserve can only be used inside procedure classes annotated with @CollectReservations.",
                        annotatedField);
            }
        }

        final Set<? extends Element> collectReservationsElements =
                roundEnv.getElementsAnnotatedWith(CollectReservations.class);
        final var reservableAnnotationType = elementUtils
                .getTypeElement(ReservableAnnotation.class.getCanonicalName())
                .asType();
        final var magicProcedureType = elementUtils
                .getTypeElement("com.team766.framework.MagicProcedure")
                .asType();
        for (var annotatedElement : collectReservationsElements) {
            final var annotatedClass = (TypeElement) annotatedElement;
            Set<CharSequence> reservations = new HashSet<>();
            List<FieldInfo> fields = new ArrayList<>();
            for (var element : annotatedClass.getEnclosedElements()) {
                if (!element.getKind().isField()) {
                    continue;
                }
                var field = (VariableElement) element;
                if (field.getAnnotation(Reserve.class) == null) {
                    continue;
                }
                if (field.asType() instanceof DeclaredType type
                        && hasAnnotation(type.asElement(), reservableAnnotationType)) {
                    reservations.add(RobotSystemsProcessor.getSystemReservationInterfaceName(
                            type.toString()));
                    fields.add(new FieldInfo(
                            field.getSimpleName(), type.asElement().getSimpleName()));
                } else {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "@Reserve can only be applied to RobotSystem (or other Reservable) fields.",
                            field);
                }
            }
            var annotation = annotatedClass.getAnnotation(CollectReservations.class);
            for (var inherit : ProcessorUtils.getClassesValue(() -> annotation.value())) {
                if (hasAnnotation(typeUtils.asElement(inherit), collectReservationsType)) {
                    reservations.add(getProcedureReservationsInterfaceName(inherit.toString()));
                } else if (hasAnnotation(typeUtils.asElement(inherit), reservableAnnotationType)) {
                    reservations.add(RobotSystemsProcessor.getSystemReservationInterfaceName(
                            inherit.toString()));
                } else {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "Invalid reservation: %s. @CollectReservations can only use RobotSystem (or other Reservable) types, and other classes annotated with CollectReservations."
                                    .formatted(inherit.toString()),
                            annotatedClass);
                }
            }
            var superClass = typeUtils.asElement(annotatedClass.getSuperclass());
            boolean superClassHasReservations = hasAnnotation(superClass, collectReservationsType);
            if (superClassHasReservations) {
                reservations.add(getProcedureReservationsInterfaceName(superClass.toString()));
            }
            NewExpressionsScanner scanner = new NewExpressionsScanner();
            var instantiatedClasses = scanner.scan(annotatedClass, this.trees);
            for (var classElement : instantiatedClasses) {
                if (hasAnnotation(classElement, collectReservationsType)) {
                    reservations.add(
                            getProcedureReservationsInterfaceName(classElement.toString()));
                }
            }

            var packageName = elementUtils.getPackageOf(annotatedClass).getQualifiedName();
            var className = annotatedClass.getSimpleName();
            var generatedInterfaceName = getProcedureReservationsInterfaceName(className);
            var generatedInterfaceQualifiedName = packageName + "." + generatedInterfaceName;

            var genericWildcards = "";
            if (annotatedClass.getTypeParameters().size() > 0) {
                genericWildcards =
                        "<" + "?, ".repeat(annotatedClass.getTypeParameters().size() - 1) + "?>";
            }

            if (!annotatedClass.getModifiers().contains(Modifier.ABSTRACT)) {
                var magicProcedure = ProcessorUtils.findSuperclass(
                        typeUtils, annotatedClass.asType(), magicProcedureType);
                if (magicProcedure == null
                        || magicProcedure.getTypeArguments().size() != 1
                        || !magicProcedure
                                .getTypeArguments()
                                .get(0)
                                .toString()
                                .equals(generatedInterfaceName)) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "%s should inherit from MagicProcedure<%s>"
                                    .formatted(className, generatedInterfaceName),
                            annotatedClass);
                }
            }

            try {
                var builderFile =
                        filer.createSourceFile(generatedInterfaceQualifiedName, annotatedClass);
                try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                    out.println("package %s;".formatted(packageName));
                    out.println("");
                    out.println("import com.team766.framework.GenericRobotSystemProvider;");
                    out.println("import com.team766.framework.ReservationsInterface;");
                    out.println("import com.team766.framework.resources.Reservable;");
                    out.println("import edu.wpi.first.wpilibj2.command.Subsystem;");
                    out.println("import java.util.Set;");
                    out.println("import java.util.function.Consumer;");
                    out.println("import java.util.function.Function;");
                    out.println("");
                    out.println("@SuppressWarnings(\"unused\")");
                    out.print("public interface %s".formatted(generatedInterfaceName));
                    out.print(" extends GenericRobotSystemProvider, ReservationsInterface");
                    for (var reservation : reservations) {
                        out.print(",\n        ");
                        out.print(reservation);
                    }
                    out.println(" {");
                    out.println(
                            "    class EntryPoint implements ReservationsInterface.EntryPoint<%s, %s%s> {"
                                    .formatted(
                                            generatedInterfaceName, className, genericWildcards));
                    out.println("        @Override");
                    out.println(
                            "        public %s makeImplementation(GenericRobotSystemProvider provider) {"
                                    .formatted(generatedInterfaceName));
                    out.println("            return new %s() {".formatted(generatedInterfaceName));
                    out.println("                @Override");
                    out.println(
                            "                public <T extends Subsystem & Reservable> T getRobotSystem(Class<T> clazz) {");
                    out.println("                    return provider.getRobotSystem(clazz);");
                    out.println("                }");
                    out.println("            };");
                    out.println("        }");
                    out.println("        @Override");
                    out.println(
                            "        public void addReservations(%s r, Consumer<Subsystem> add) {"
                                    .formatted(generatedInterfaceName));
                    out.println("            %s.addReservations(r, add);"
                            .formatted(generatedInterfaceName));
                    out.println("        }");
                    out.println("        @Override");
                    out.println(
                            "        public void applyReservations(%s r, %s%s p, Set<Subsystem> s) {"
                                    .formatted(
                                            generatedInterfaceName, className, genericWildcards));
                    out.println("            %s.applyReservations(r, p, s);"
                            .formatted(generatedInterfaceName));
                    out.println("        }");
                    out.println("    }");
                    out.println("");
                    out.println("    static void addReservations(%s r, Consumer<Subsystem> add) {"
                            .formatted(generatedInterfaceName));
                    for (var intf : reservations) {
                        out.println("        %s.addReservations(r, add);".formatted(intf));
                    }
                    out.println("    }");
                    out.println("");
                    out.println(
                            "    static void applyReservations(%s r, %s%s p, Set<Subsystem> s) {"
                                    .formatted(
                                            generatedInterfaceName, className, genericWildcards));
                    if (superClassHasReservations) {
                        out.println("        %s.applyReservations(r, p, s);"
                                .formatted(getProcedureReservationsInterfaceName(
                                        superClass.toString())));
                    }
                    for (var field : fields) {
                        out.println(
                                "        p.%s = r.get%s();".formatted(field.name(), field.type()));
                    }
                    out.println("        %s.addReservations(r, s::add);"
                            .formatted(generatedInterfaceName));
                    out.println("    }");
                    out.println("}");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return false;
    }

    private boolean hasAnnotation(Element element, TypeMirror annotationType) {
        return elementUtils.getAllAnnotationMirrors(element).stream()
                .anyMatch(a -> typeUtils.isSameType(annotationType, a.getAnnotationType()));
    }
}
