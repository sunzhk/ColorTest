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
 * Denotes a single part of a multi-part request.
 *
 *
 * The parameter type on which this annotation exists will be processed in one of three ways:
 *
 *
 *  * If the type is [okhttp3.MultipartBody.Part] the contents will be used directly. Omit
 * the name from the annotation (i.e., `@Part MultipartBody.Part part`).
 *  * If the type is [RequestBody][okhttp3.RequestBody] the value will be used directly with
 * its content type. Supply the part name in the annotation (e.g., `@Part("foo")
 * RequestBody foo`).
 *  * Other object types will be converted to an appropriate representation by using [       ]. Supply the part name in the annotation (e.g., `@Part("foo")
 * Image photo`).
 *
 *
 *
 * Values may be `null` which will omit them from the request body.
 *
 *
 *
 *
 * <pre>`
 * &#64;Multipart
 * &#64;POST("/")
 * Call<ResponseBody> example(
 * &#64;Part("description") String description,
 * &#64;Part(value = "image", encoding = "8-bit") RequestBody image);
`</pre> *
 *
 *
 * Part parameters may not be `null`.
 */
@Documented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class Part(
	/**
	 * The name of the part. Required for all parameter types except [ ].
	 */
	val value: String = "",
	/** The `Content-Transfer-Encoding` of this part.  */
	val encoding: String = "binary"
)