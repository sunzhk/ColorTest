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

/** Make a DELETE request.  */
@Documented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
	RetentionPolicy.RUNTIME)
annotation class DELETE(
	/**
	 * A relative or absolute path, or full URL of the endpoint. This value is optional if the first
	 * parameter of the method is annotated with [@Url][Url].
	 *
	 *
	 * See [base URL][retrofit2.Retrofit.Builder.baseUrl] for details of how
	 * this is resolved against a base URL to create the full endpoint URL.
	 */
	val value: String = ""
)