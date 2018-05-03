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
package com.holonplatform.datastore.mongo.core.exceptions;

/**
 * An exception used to notify invalid document identifier representation or data type.
 *
 * @since 5.2.0
 */
public class InvalidDocumentIdentifierException extends RuntimeException {

	private static final long serialVersionUID = -5769017369255911812L;

	/**
	 * Constructor with error message.
	 * @param message The error message
	 */
	public InvalidDocumentIdentifierException(String message) {
		super(message);
	}

	/**
	 * Constructor with error message and cause.
	 * @param message The error message
	 * @param cause The cause
	 */
	public InvalidDocumentIdentifierException(String message, Throwable cause) {
		super(message, cause);
	}

}
