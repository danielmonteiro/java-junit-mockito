package br.ce.wcaquino.servicos;

import java.util.Calendar;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class DateMatcher extends TypeSafeMatcher<Date> {

	private Integer diaSemana;
	
	public DateMatcher(Integer diaSemana) {
		this.diaSemana = diaSemana;
	}
	
	public void describeTo(Description description) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean matchesSafely(Date item) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(item);
				
		return calendar.get(calendar.DAY_OF_WEEK) == diaSemana;
	}

}
