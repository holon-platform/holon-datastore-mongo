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

import com.holonplatform.core.property.PropertyBoxProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;

@SuppressWarnings("unused")
public class ExampleMapping {

	private interface Mapping1 {
		// tag::mapping1[]
		static final StringProperty NAME = StringProperty.create("name");
		static final StringProperty STREET = StringProperty.create("address.street");
		static final StringProperty CITY = StringProperty.create("address.city");

		static final PropertySet<?> SUBJECT = PropertySet.of(NAME, STREET, CITY);
		// end::mapping1[]
	}

	private interface Mapping2 {
		// tag::mapping2[]
		static final StringProperty NAME = StringProperty.create("name");

		static final StringProperty STREET = StringProperty.create("street");
		static final StringProperty CITY = StringProperty.create("city");
		static final PropertyBoxProperty ADDRESS = PropertyBoxProperty.create("address", STREET, CITY);

		static final PropertySet<?> SUBJECT = PropertySet.of(NAME, ADDRESS);
		// end::mapping2[]
	}

}