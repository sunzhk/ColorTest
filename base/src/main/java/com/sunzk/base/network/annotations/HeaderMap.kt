/*
 * Copyright (C) 2016 Square, Inc.
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
 * Adds headers specified in the [Map] or [okhttp3.Headers].
 *
 *
 * Values in the map are converted to strings using [Retrofit.stringConverter] (or [Object.toString], if no matching string converter is installed).
 *
 *
 * Simple Example:
 *
 * <pre>
 * &#64;GET("/search")
 * void list(@HeaderMap Map&lt;String, String&gt; headers);
 *
 * ...
 *
 * // The following call yields /search with headers
 * // Accept: text/plain and Accept-Charset: utf-8
 * foo.list(ImmutableMap.of("Accept", "text/plain", "Accept-Charset", "utf-8"));
</pre> *
 *
 * @see Header
 *
 * @see Headers
 */
@Documented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class HeaderMap 