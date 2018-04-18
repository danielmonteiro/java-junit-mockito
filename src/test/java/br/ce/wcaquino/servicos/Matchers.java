package br.ce.wcaquino.servicos;

import java.util.Calendar;
import java.util.Date;

import org.hamcrest.Matcher;

public class Matchers {

	public static DateMatcher ehSegunda() {
		return new DateMatcher(Calendar.MONDAY);
	}

	public static DateMatcher ehHoje() {
		return new DateMatcher(Calendar.MONDAY);
	}

}
