package com.holonplatform.datastore.mongo.reactor.test.data;

import java.io.Serializable;

import com.holonplatform.core.beans.Identifier;

public class TestProjectionBean3 implements Serializable {

	private static final long serialVersionUID = 1L;

	@Identifier
	private String id;

	private String str;

	private EnumValue enm;

	private TestNestedBean1 n1;

	private TestNestedBean2 n2;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public EnumValue getEnm() {
		return enm;
	}

	public void setEnm(EnumValue enm) {
		this.enm = enm;
	}

	public TestNestedBean1 getN1() {
		return n1;
	}

	public void setN1(TestNestedBean1 n1) {
		this.n1 = n1;
	}

	public TestNestedBean2 getN2() {
		return n2;
	}

	public void setN2(TestNestedBean2 n2) {
		this.n2 = n2;
	}

}
