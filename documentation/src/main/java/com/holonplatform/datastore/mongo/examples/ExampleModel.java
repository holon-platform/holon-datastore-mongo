/*
 * Copyright 2016-2018 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.datastore.mongo.examples;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;

public class ExampleModel {

	// tag::model[]
	final static StringProperty ID = StringProperty.create("_id"); // <1>
	final static StringProperty NAME = StringProperty.create("name"); // <2>

	final static PropertySet<?> SUBJECT = PropertySet.builderOf(ID, NAME).identifier(ID).build(); // <3>

	final static DataTarget<?> TARGET = DataTarget.named("my_collection"); // <4>
	// end::model[]

}
