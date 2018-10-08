package com.holonplatform.datastore.mongo.async.test.data;

import java.io.Serializable;
import java.math.BigInteger;

import com.holonplatform.core.beans.DataPath;

public class TestProjectionBean2 implements Serializable {

	private static final long serialVersionUID = 7323766007354763956L;

	@DataPath("_id")
	private BigInteger code;

	@DataPath("str")
	private String text;

	public BigInteger getCode() {
		return code;
	}

	public void setCode(BigInteger code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
