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
 * Denotes name and value parts of a multi-part request.
 *
 *
 * Values of the map on which this annotation exists will be processed in one of two ways:
 *
 *
 *  * If the type is [RequestBody][okhttp3.RequestBody] the value will be used directly with
 * its content type.
 *  * Other object types will be converted to an appropriate representation by using [       ].
 *
 *
 *
 *
 *
 * <pre>`
 * &#64;Multipart
 * &#64;POST("/upload")
 * Call<ResponseBody> upload(
 * &#64;Part("file") RequestBody file,
 * &#64;PartMap Map<String, RequestBody> params);
`</pre> *
 *
 *
 * A `null` value for the map, as a key, or as a value is not allowed.
 *
 * @see Multipart
 *
 * @see Part
 */
@Documented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class PartMap(
	/** The `Content-Transfer-Encoding` of the parts.  */
	val encoding: String = "binary"
)