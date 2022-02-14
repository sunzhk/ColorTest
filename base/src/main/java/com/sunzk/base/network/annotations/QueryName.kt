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
 * Query parameter appended to the URL that has no value.
 *
 *
 * Passing a [List][java.util.List] or array will result in a query parameter for each
 * non-`null` item.
 *
 *
 * Simple Example:
 *
 * <pre>`
 * &#64;GET("/friends")
 * Call<ResponseBody> friends(@QueryName String filter);
`</pre> *
 *
 * Calling with `foo.friends("contains(Bob)")` yields `/friends?contains(Bob)`.
 *
 *
 * Array/Varargs Example:
 *
 * <pre>`
 * &#64;GET("/friends")
 * Call<ResponseBody> friends(@QueryName String... filters);
`</pre> *
 *
 * Calling with `foo.friends("contains(Bob)", "age(42)")` yields `/friends?contains(Bob)&age(42)`.
 *
 *
 * Parameter names are URL encoded by default. Specify [encoded=true][.encoded] to change
 * this behavior.
 *
 * <pre>`
 * &#64;GET("/friends")
 * Call<ResponseBody> friends(@QueryName(encoded=true) String filter);
`</pre> *
 *
 * Calling with `foo.friends("name+age"))` yields `/friends?name+age`.
 *
 * @see Query
 *
 * @see QueryMap
 */
@Documented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class QueryName(
	/** Specifies whether the parameter is already URL encoded.  */
	val encoded: Boolean = false
)