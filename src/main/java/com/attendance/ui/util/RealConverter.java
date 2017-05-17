package com.attendance.ui.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToBigDecimalConverter;

/**
 * A converter that adds/removes the euro sign and formats currencies with two
 * decimal places.
 */
public class RealConverter extends StringToBigDecimalConverter {

	private static final long serialVersionUID = 1L;

	public RealConverter() {
		super("Não foi possível converter o valor para um número");
	}

	@Override
	public Result<BigDecimal> convertToModel(String value, ValueContext context) {
		value = value.replaceAll("[R$\\s]", "").trim();
		if ("".equals(value)) {
			value = "0";
		}
		return super.convertToModel(value, context);
	}

	@Override
	protected NumberFormat getFormat(Locale locale) {
		// Always display currency with two decimals
		NumberFormat format = super.getFormat(locale);
		if (format instanceof DecimalFormat) {
			((DecimalFormat) format).setMaximumFractionDigits(2);
			((DecimalFormat) format).setMinimumFractionDigits(2);
		}
		return format;
	}

	@Override
	public String convertToPresentation(BigDecimal value, ValueContext context) {
		return value == null ? "" : "R$ " + super.convertToPresentation(value, context);
	}
}
