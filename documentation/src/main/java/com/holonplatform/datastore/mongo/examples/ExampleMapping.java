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

import java.math.BigInteger;

import org.bson.types.ObjectId;

import com.holonplatform.core.property.ListPathProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBoxProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.SetPathProperty;
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

	private interface Mapping3 {
		// tag::mapping3[]
		static final StringProperty NAME = StringProperty.create("name");

		static final ListPathProperty<String> SKILLS = ListPathProperty.create("skills", String.class); // <1>
		static final SetPathProperty<EnumValue> QUALIFICATIONS = SetPathProperty.create("qualifications",
				EnumValue.class); // <2>

		static final PropertySet<?> SUBJECT = PropertySet.of(NAME, SKILLS, QUALIFICATIONS);
		// end::mapping3[]
	}

	private interface Mapping4 {
		// tag::mapping4[]
		static final PathProperty<ObjectId> ID = PathProperty.create("_id", ObjectId.class);
		// end::mapping4[]
	}

	private interface Mapping5 {
		// tag::mapping5[]
		static final StringProperty ID = StringProperty.create("_id");
		// end::mapping5[]
	}

	private interface Mapping6 {
		// tag::mapping6[]
		static final NumericProperty<BigInteger> ID = NumericProperty.bigIntegerType("_id");
		// end::mapping6[]
	}

	private interface Mapping7 {
		// tag::mapping7[]
		static final StringProperty ID = StringProperty.create("my_document_id");
		static final StringProperty NAME = StringProperty.create("name");

		static final PropertySet<?> SUBJECT = PropertySet.builderOf(ID, NAME).withIdentifier(ID).build(); // <1>
		// end::mapping7[]
	}

	private interface Mapping8 {
		// tag::mapping8[]
		static final StringProperty ID = StringProperty.create("_id");
		static final StringProperty NAME = StringProperty.create("name");

		static final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME); // <1>
		// end::mapping8[]
	}

	private static enum EnumValue {

		FIRST, SECOND, THIRD;

	}

}
