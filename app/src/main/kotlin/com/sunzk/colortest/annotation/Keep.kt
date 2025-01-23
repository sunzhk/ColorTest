package com.sunzk.colortest.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 在混淆时过滤掉带有该注解的类(不包括类里面的方法、参数)、方法、参数等。
 */
@Retention(RetentionPolicy.CLASS)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD
)
annotation class Keep 