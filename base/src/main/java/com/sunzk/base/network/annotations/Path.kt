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
 * Named replacement in a URL path segment. Values are converted to strings using [ ][Retrofit.stringConverter] (or [Object.toString], if no matching
 * string converter is installed) and then URL encoded.
 *
 *
 * Simple example:
 *
 * <pre>`
 * &#64;GET("/image/{id}")
 * Call<ResponseBody> example(@Path("id") int id);
`</pre> *
 *
 * Calling with `foo.example(1)` yields `/image/1`.
 *
 *
 * Values are URL encoded by default. Disable with `encoded=true`.
 *
 * <pre>`
 * &#64;GET("/user/{name}")
 * Call<ResponseBody> encoded(@Path("name") String name);
 *
 * &#64;GET("/user/{name}")
 * Call<ResponseBody> notEncoded(@Path(value="name", encoded=true) String name);
`</pre> *
 *
 * Calling `foo.encoded("John+Doe")` yields `/user/John%2BDoe` whereas `foo.notEncoded("John+Doe")` yields `/user/John+Doe`.
 *
 *
 * Path parameters may not be `null`.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path(
	val value: String,
	/**
	 * Specifies whether the argument value to the annotated method parameter is already URL encoded.
	 */
	val encoded: Boolean = false
)