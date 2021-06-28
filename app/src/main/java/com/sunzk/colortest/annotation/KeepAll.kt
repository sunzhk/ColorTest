package com.sunzk.colortest.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 在混淆时过滤掉带有该注解的整个类。
 * 需要在混淆过滤文件中添加
 * #尝试用注解防止混淆
 * -keep @com.chances.base.annotation.Keep class *
 * -keep @com.chances.base.annotation.KeepAll class * {*;}
 * -keep,allowobfuscation @interface com.chances.base.annotation.Keep
 * -keep,allowobfuscation @interface com.chances.base.annotation.KeepAll
 * -keepclassmembers class * {
 * @com.chances.base.annotation.Keep *;
 * }
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
annotation class KeepAll 