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
 * Named key/value pairs for a form-encoded request.
 *
 *
 * Simple Example:
 *
 * <pre>`
 * &#64;FormUrlEncoded
 * &#64;POST("/things")
 * Call<ResponseBody> things(@FieldMap Map<String, String> fields);
`</pre> *
 *
 * Calling with `foo.things(ImmutableMap.of("foo", "bar", "kit", "kat")` yields a request body
 * of `foo=bar&kit=kat`.
 *
 *
 * A `null` value for the map, as a key, or as a value is not allowed.
 *
 * @see FormUrlEncoded
 *
 * @see Field
 */
@Documented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class FieldMap(
	/** Specifies whether the names and values are already URL encoded.  */
	val encoded: Boolean = false
)