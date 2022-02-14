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
 * Query parameter appended to the URL.
 *
 *
 * Values are converted to strings using [Retrofit.stringConverter] (or
 * [Object.toString], if no matching string converter is installed) and then URL encoded.
 * `null` values are ignored. Passing a [List][java.util.List] or array will result in a
 * query parameter for each non-`null` item.
 *
 *
 * Simple Example:
 *
 * <pre>`
 * &#64;GET("/friends")
 * Call<ResponseBody> friends(@Query("page") int page);
`</pre> *
 *
 * Calling with `foo.friends(1)` yields `/friends?page=1`.
 *
 *
 * Example with `null`:
 *
 * <pre>`
 * &#64;GET("/friends")
 * Call<ResponseBody> friends(@Query("group") String group);
`</pre> *
 *
 * Calling with `foo.friends(null)` yields `/friends`.
 *
 *
 * Array/Varargs Example:
 *
 * <pre>`
 * &#64;GET("/friends")
 * Call<ResponseBody> friends(@Query("group") String... groups);
`</pre> *
 *
 * Calling with `foo.friends("coworker", "bowling")` yields `/friends?group=coworker&group=bowling`.
 *
 *
 * Parameter names and values are URL encoded by default. Specify [encoded=true][.encoded]
 * to change this behavior.
 *
 * <pre>`
 * &#64;GET("/friends")
 * Call<ResponseBody> friends(@Query(value="group", encoded=true) String group);
`</pre> *
 *
 * Calling with `foo.friends("foo+bar"))` yields `/friends?group=foo+bar`.
 *
 * @see QueryMap
 *
 * @see QueryName
 */
@Documented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class Query(
	/** The query parameter name.  */
	val value: String,
	/**
	 * Specifies whether the parameter [name][.value] and value are already URL encoded.
	 */
	val encoded: Boolean = false
)