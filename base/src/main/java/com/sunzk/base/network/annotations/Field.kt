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
 * Named pair for a form-encoded request.
 *
 *
 * Values are converted to strings using [Retrofit.stringConverter] (or
 * [Object.toString], if no matching string converter is installed) and then form URL
 * encoded. `null` values are ignored. Passing a [List][java.util.List] or array will
 * result in a field pair for each non-`null` item.
 *
 *
 * Simple Example:
 *
 * <pre>`
 * &#64;FormUrlEncoded
 * &#64;POST("/")
 * Call<ResponseBody> example(
 * &#64;Field("name") String name,
 * &#64;Field("occupation") String occupation);
`</pre> *
 *
 * Calling with `foo.example("Bob Smith", "President")` yields a request body of `name=Bob+Smith&occupation=President`.
 *
 *
 * Array/Varargs Example:
 *
 * <pre>`
 * &#64;FormUrlEncoded
 * &#64;POST("/list")
 * Call<ResponseBody> example(@Field("name") String... names);
`</pre> *
 *
 * Calling with `foo.example("Bob Smith", "Jane Doe")` yields a request body of `name=Bob+Smith&name=Jane+Doe`.
 *
 * @see FormUrlEncoded
 *
 * @see FieldMap
 */
@Documented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class Field(
	val value: String,
	/** Specifies whether the [name][.value] and value are already URL encoded.  */
	val encoded: Boolean = false
)