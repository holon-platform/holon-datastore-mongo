package com.holonplatform.datastore.mongo.core.test.data;

import java.io.Serializable;

import com.holonplatform.core.beans.DataPath;
import com.holonplatform.core.beans.Identifier;

public class TestProjectionBean implements Serializable {

	private static final long serialVersionUID = 7323766007354763956L;

	@Identifier
	private String id;

	@DataPath("str")
	private String text;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
