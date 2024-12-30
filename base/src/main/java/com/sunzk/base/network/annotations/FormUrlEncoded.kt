/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunzk.base.network.annotations

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Denotes that the request body will use form URL encoding. Fields should be declared as parameters
 * and annotated with [@Field][Field].
 *
 *
 * Requests made with this annotation will have `application/x-www-form-urlencoded` MIME
 * type. Field names and values will be UTF-8 encoded before being URI-encoded in accordance to [RFC-3986](http://tools.ietf.org/html/rfc3986).
 */
@Documented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
	RetentionPolicy.RUNTIME)
annotation class FormUrlEncoded 