/*
 * Copyright (C) 2014 Square, Inc.
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
 * Query parameter keys and values appended to the URL.
 *
 *
 * Values are converted to strings using [Retrofit.stringConverter] (or
 * [Object.toString], if no matching string converter is installed).
 *
 *
 * Simple Example:
 *
 * <pre>`
 * &#64;GET("/friends")
 * Call<ResponseBody> friends(@QueryMap Map<String, String> filters);
`</pre> *
 *
 * Calling with `foo.friends(ImmutableMap.of("group", "coworker", "age", "42"))` yields `/friends?group=coworker&age=42`.
 *
 *
 * Map keys and values representing parameter values are URL encoded by default. Specify [ ][.encoded] to change this behavior.
 *
 * <pre>`
 * &#64;GET("/friends")
 * Call<ResponseBody> friends(@QueryMap(encoded=true) Map<String, String> filters);
`</pre> *
 *
 * Calling with `foo.list(ImmutableMap.of("group", "coworker+bowling"))` yields `/friends?group=coworker+bowling`.
 *
 *
 * A `null` value for the map, as a key, or as a value is not allowed.
 *
 * @see Query
 *
 * @see QueryName
 */
@Documented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class QueryMap(
	/** Specifies whether parameter names and values are already URL encoded.  */
	val encoded: Boolean = false
)