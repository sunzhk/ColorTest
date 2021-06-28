package com.sunzk.colortest.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author yongjie created on 2019/3/25.
 * 一个标记注解。目的就是进行组件化开发的时候,不同的Module创建服务统一到一个地方，
 * 用此注解标记，表示是服务创建的地方
 */
@Retention(RetentionPolicy.SOURCE)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class NetworkService 