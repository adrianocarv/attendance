package com.attendance.ui.util;

import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToIntegerConverter;

/**
 * A converter that adds/removes the euro sign and formats currencies with two
 * decimal places.
 */
public class IntegerConverter extends StringToIntegerConverter {

	private static final long serialVersionUID = 1L;

	public IntegerConverter() {
		super("Não foi possível converter o valor para um número");
	}

	@Override
	public Result<Integer> convertToModel(String value, ValueContext context) {
		return super.convertToModel(value, context);
	}

	@Override
	public String convertToPresentation(Integer value, ValueContext context) {
		return value == null ? "" : super.convertToPresentation(value, context);
	}
}
