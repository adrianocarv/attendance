package com.attendance.ui.util;

import java.sql.Date;
import java.time.LocalDate;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

public class LocalDateToSqlDateConverter implements Converter<LocalDate, Date> {

	private static final long serialVersionUID = 1L;

	@Override
	public Result<Date> convertToModel(LocalDate localDate, ValueContext context) {
		if (localDate == null) {
			return Result.ok(null);
		}

		return Result.ok(Date.valueOf(localDate));
	}

	@Override
	public LocalDate convertToPresentation(Date date, ValueContext context) {
		if (date == null) {
			return null;
		}

		return date.toLocalDate();
	}

}
