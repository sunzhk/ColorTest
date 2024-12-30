/*
 * Copyright (C) 2019 Square, Inc.
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
 * Adds the argument instance as a request tag using the type as the key.
 *
 * <pre>`
 * &#64;GET("/")
 * Call<ResponseBody> foo(@Tag String tag);
`</pre> *
 *
 * Tag arguments may be `null` which will omit them from the request. Passing a parameterized
 * type such as `List<String>` will use the raw type (i.e., `List.class`) as the key.
 * Duplicate tag types are not allowed.
 */
@Documented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class Tag 