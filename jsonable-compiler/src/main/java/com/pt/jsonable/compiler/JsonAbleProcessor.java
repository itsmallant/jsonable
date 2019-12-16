package com.pt.jsonable.compiler;

import com.google.auto.service.AutoService;
import com.pt.jsonable.annotation.Exclude;
import com.pt.jsonable.annotation.JSONAble;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @desc:
 * @author: ningqiang.zhao
 * @time: 2019-12-11 13:56
 **/
@AutoService(Processor.class)
public class JsonAbleProcessor extends AbstractProcessor {
    private static final ClassName JSONOBJECT_CLASSNAME = ClassName.get("org.json", "JSONObject");
    private static final String COLLECTION_TYPE = Collection.class.getCanonicalName();
    private static final String STRING_TYPE = String.class.getCanonicalName();
    private static final ClassName JSONARRAY_CLASSNAME = ClassName.get(" org.json", "JSONArray");

    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(JSONAble.class.getCanonicalName());
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
        Map<String, List<TypeElement>> packageNameJsonAbleTypeElementMap = new HashMap<>();
        for (Element jsonAbleElement : env.getElementsAnnotatedWith(JSONAble.class)) {
            TypeElement jsonAbleTypeElement = (TypeElement) jsonAbleElement;
            String packageName = getPackageName(jsonAbleTypeElement);
            List<TypeElement> typeElements = packageNameJsonAbleTypeElementMap.get(packageName);
            if (typeElements == null) {
                typeElements = new ArrayList<>();
                typeElements.add(jsonAbleTypeElement);
                packageNameJsonAbleTypeElementMap.put(packageName, typeElements);
            } else {
                typeElements.add(jsonAbleTypeElement);
            }
        }
        for (String packageName : packageNameJsonAbleTypeElementMap.keySet()) {
            List<TypeElement> typeElements = packageNameJsonAbleTypeElementMap.get(packageName);
            List<MethodSpec> methodList = new ArrayList<>();
            for (TypeElement typeElement : typeElements) {
                //generate toJson method
                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("toJson")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(ClassName.get(typeElement.asType()), "source")
                        .returns(JSONOBJECT_CLASSNAME);
                methodSpecBuilder.addStatement("JSONObject json = new JSONObject()");
                methodSpecBuilder.addCode("try{\n");
                List<? extends Element> members = elementUtils.getAllMembers(typeElement);
                for (Element item : members) {
                    if (item.getKind() == ElementKind.FIELD) {
                        if (item.getAnnotation(Exclude.class) != null) {
                            continue;
                        }
                        Set<Modifier> modifiers = item.getModifiers();
                        if (modifiers.contains(Modifier.TRANSIENT)) {
                            continue;
                        }
                        TypeMirror itemTypeMirror = item.asType();
                        TypeMirror componentType = null;
                        boolean isArrayOrCollection = true;
                        if (itemTypeMirror.getKind() == TypeKind.ARRAY) {
                            ArrayType arrayType = (ArrayType) itemTypeMirror;
                            componentType = arrayType.getComponentType();
                        } else if (isSubtypeOfCollection(itemTypeMirror)) {
                            DeclaredType declaredType = (DeclaredType) itemTypeMirror;
                            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                            componentType = typeArguments.size() == 1 ? typeArguments.get(0) : null;
                        } else {
                            isArrayOrCollection = false;
                        }
                        if (isArrayOrCollection) {
                            if (componentType != null && elementUtils.getTypeElement(componentType.toString()).getAnnotation(JSONAble.class) != null) {
                                if (modifiers.isEmpty() || modifiers.contains(Modifier.PRIVATE)) {
                                    Element itemGetMethod = findGetMethod(members, item);
                                    methodSpecBuilder.addStatement("$T $LList = source.$L", item, item, itemGetMethod);
                                } else {
                                    methodSpecBuilder.addStatement("$T $LList = source.$L", item, item, item);
                                }

                                methodSpecBuilder.addStatement("$T $LJA = new JSONArray()", JSONARRAY_CLASSNAME, item);
                                methodSpecBuilder.addCode("for ($T item : $LList){\n", componentType, item);
                                String code = String.format("$LJA.put(%s.JsonAbleUtil.toJson(item))", getPackageName(elementUtils.getTypeElement(componentType.toString())));
                                methodSpecBuilder.addStatement(code, item.getSimpleName());
                                methodSpecBuilder.addCode("}\n");

                                methodSpecBuilder.addStatement("json.put($S,$LJA)", item.getSimpleName(), item.getSimpleName());
                                continue;
                            } else {
                                if (componentType != null && (componentType.getKind().isPrimitive() || STRING_TYPE.equals(componentType.toString()))){
                                    if (modifiers.isEmpty() || modifiers.contains(Modifier.PRIVATE)) {
                                        Element itemGetMethod = findGetMethod(members, item);
                                        methodSpecBuilder.addStatement("json.put($S,new $T(source.$L))", item.getSimpleName(), JSONARRAY_CLASSNAME, itemGetMethod);
                                    } else {
                                        methodSpecBuilder.addStatement("json.put($S,new $T(source.$L))", item.getSimpleName(), JSONARRAY_CLASSNAME, item);
                                    }
                                    continue;
                                }
                            }
                        }
                        TypeElement itemTypeElement = elementUtils.getTypeElement(itemTypeMirror.toString());
                        if (itemTypeElement != null && itemTypeElement.getAnnotation(JSONAble.class) != null) {
                            if (modifiers.isEmpty() || modifiers.contains(Modifier.PRIVATE)) {
                                Element itemGetMethod = findGetMethod(members, item);
                                String code = String.format("json.put($S,%s.JsonAbleUtil.toJson(source.$L))", getPackageName(itemTypeElement));
                                methodSpecBuilder.addStatement(code, item.getSimpleName(), itemGetMethod);
                            } else {
                                String code = String.format("json.put($S,%s.JsonAbleUtil.toJson(source.$L))", getPackageName(itemTypeElement));
                                methodSpecBuilder.addStatement(code, item.getSimpleName(), item);
                            }
                        } else {
                            if (modifiers.isEmpty() || modifiers.contains(Modifier.PRIVATE)) {
                                Element itemGetMethod = findGetMethod(members, item);
                                methodSpecBuilder.addStatement("json.put($S,source.$L)", item.getSimpleName(), itemGetMethod);
                            } else {
                                methodSpecBuilder.addStatement("json.put($S,source.$L)", item.getSimpleName(), item);
                            }
                        }
                    }
                }
                methodSpecBuilder.addCode("} catch (Exception e) {\ne.printStackTrace();\n}\n");
                methodSpecBuilder.addStatement("return json");
                methodList.add(methodSpecBuilder.build());
            }
            //generate java file
            TypeSpec typeSpec = TypeSpec.classBuilder("JsonAbleUtil")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethods(methodList)
                    .build();

            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Uses both {@link Types#erasure} and string manipulation to strip any generic types.
     */
    private String doubleErasure(TypeMirror elementType) {
        String name = typeUtils.erasure(elementType).toString();
        int typeParamStart = name.indexOf('<');
        if (typeParamStart != -1) {
            name = name.substring(0, typeParamStart);
        }
        return name;
    }


    private boolean isSubtypeOfCollection(TypeMirror typeMirror) {
        String erasedType = doubleErasure(typeMirror);
        if (COLLECTION_TYPE.equals(erasedType)) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfCollection(superType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfCollection(interfaceType)) {
                return true;
            }
        }
        return false;
    }


    private Element findGetMethod(List<? extends Element> members, Element item) {
        for (Element member : members) {
            TypeMirror memberTypeMirror = member.asType();
            String itemName = item.toString();

            String getMethod = String.format("get%s()", itemName);
            String isMethod;
            if (itemName.startsWith("is")) {
                isMethod = String.format("%s()", itemName);
            } else {
                isMethod = String.format("is%s()", itemName);
            }

            if (memberTypeMirror.getKind() == TypeKind.EXECUTABLE && member.toString().equalsIgnoreCase(getMethod) || member.toString().equalsIgnoreCase(isMethod)) {
                return member;
            }
        }
        logParsingError(item);
        return null;

    }

    private void logParsingError(Element element) {
        StringWriter stackTrace = new StringWriter();
        error(element, "%s Property must public or have get/is method.\n\n%s", element.getSimpleName(), stackTrace);
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }


    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
