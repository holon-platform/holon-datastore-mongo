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
package com.holonplatform.datastore.mongo.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.datastore.mongo.core.document.DocumentIdResolver;
import com.holonplatform.datastore.mongo.core.exceptions.InvalidDocumentIdentifierException;

public class TestDocumentIdResolver {

	@Test
	public void testIdentifierString() {

		final StringProperty ID = StringProperty.create("theId");
		final BooleanProperty BOOL = BooleanProperty.create("bool");

		final PropertySet<?> SET = PropertySet.builderOf(ID, BOOL).identifier(ID).build();

		Optional<Property<?>> docId = DocumentIdResolver.getDefault().resolveDocumentIdProperty(SET);

		assertTrue(docId.isPresent());
		assertEquals(ID, docId.get());

	}

	@Test
	public void testIdentifierBigInteger() {

		final NumericProperty<BigInteger> ID = NumericProperty.bigIntegerType("theId");
		final BooleanProperty BOOL = BooleanProperty.create("bool");

		final PropertySet<?> SET = PropertySet.builderOf(ID, BOOL).identifier(ID).build();

		Optional<Property<?>> docId = DocumentIdResolver.getDefault().resolveDocumentIdProperty(SET);

		assertTrue(docId.isPresent());
		assertEquals(ID, docId.get());

	}

	@Test
	public void testIdentifierObjectId() {

		final PathProperty<ObjectId> ID = PathProperty.create("theId", ObjectId.class);
		final BooleanProperty BOOL = BooleanProperty.create("bool");

		final PropertySet<?> SET = PropertySet.builderOf(ID, BOOL).identifier(ID).build();

		Optional<Property<?>> docId = DocumentIdResolver.getDefault().resolveDocumentIdProperty(SET);

		assertTrue(docId.isPresent());
		assertEquals(ID, docId.get());

	}

	@Test
	public void testDefaultDocumentIdPathString() {

		final StringProperty ID = StringProperty.create(DocumentIdResolver.DEFAULT_DOCUMENT_ID_PATH_NAME);
		final BooleanProperty BOOL = BooleanProperty.create("bool");

		final PropertySet<?> SET = PropertySet.of(ID, BOOL);

		Optional<Property<?>> docId = DocumentIdResolver.getDefault().resolveDocumentIdProperty(SET);

		assertTrue(docId.isPresent());
		assertEquals(ID, docId.get());

	}

	@Test
	public void testDefaultDocumentIdPathBigInteger() {

		final NumericProperty<BigInteger> ID = NumericProperty
				.bigIntegerType(DocumentIdResolver.DEFAULT_DOCUMENT_ID_PATH_NAME);
		final BooleanProperty BOOL = BooleanProperty.create("bool");

		final PropertySet<?> SET = PropertySet.of(ID, BOOL);

		Optional<Property<?>> docId = DocumentIdResolver.getDefault().resolveDocumentIdProperty(SET);

		assertTrue(docId.isPresent());
		assertEquals(ID, docId.get());

	}

	@Test
	public void testDefaultDocumentIdPathObjectId() {

		final PathProperty<ObjectId> ID = PathProperty.create(DocumentIdResolver.DEFAULT_DOCUMENT_ID_PATH_NAME,
				ObjectId.class);
		final BooleanProperty BOOL = BooleanProperty.create("bool");

		final PropertySet<?> SET = PropertySet.of(ID, BOOL);

		Optional<Property<?>> docId = DocumentIdResolver.getDefault().resolveDocumentIdProperty(SET);

		assertTrue(docId.isPresent());
		assertEquals(ID, docId.get());

	}

	@Test
	public void testDocumentIdObjectId() {

		final PathProperty<ObjectId> ID = PathProperty.create("docId", ObjectId.class);
		final BooleanProperty BOOL = BooleanProperty.create("bool");

		final PropertySet<?> SET = PropertySet.of(ID, BOOL);

		Optional<Property<?>> docId = DocumentIdResolver.getDefault().resolveDocumentIdProperty(SET);

		assertTrue(docId.isPresent());
		assertEquals(ID, docId.get());

	}

	@Test(expected = InvalidDocumentIdentifierException.class)
	public void testFailOnMultipleIdentifier() {

		final StringProperty ID = StringProperty.create("theId");
		final BooleanProperty BOOL = BooleanProperty.create("bool");

		final PropertySet<?> SET = PropertySet.builderOf(ID, BOOL).identifier(ID).identifier(BOOL).build();

		DocumentIdResolver.getDefault().resolveDocumentIdProperty(SET);

	}

	@Test(expected = InvalidDocumentIdentifierException.class)
	public void testFailOnIdentifierType() {

		final PathProperty<Date> ID = PathProperty.create("theId", Date.class);
		final BooleanProperty BOOL = BooleanProperty.create("bool");

		final PropertySet<?> SET = PropertySet.builderOf(ID, BOOL).identifier(ID).build();

		DocumentIdResolver.getDefault().resolveDocumentIdProperty(SET);

	}

	@Test(expected = InvalidDocumentIdentifierException.class)
	public void testFailOnMultipleDefaultDocumentIdPath() {

		final StringProperty ID1 = StringProperty.create(DocumentIdResolver.DEFAULT_DOCUMENT_ID_PATH_NAME);
		final NumericProperty<BigInteger> ID2 = NumericProperty
				.bigIntegerType(DocumentIdResolver.DEFAULT_DOCUMENT_ID_PATH_NAME);

		final PropertySet<?> SET = PropertySet.of(ID1, ID2);

		DocumentIdResolver.getDefault().resolveDocumentIdProperty(SET);

	}

}
