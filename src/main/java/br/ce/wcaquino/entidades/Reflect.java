package br.ce.wcaquino.entidades;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflect {

	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		
		Class<Filme> filmeReflect = Filme.class;
		
		System.out.println(filmeReflect.getName());
		
		// Constructor
		Constructor<Filme> constructor = filmeReflect.getConstructor(null);
		System.out.println(constructor.getName());
		
		constructor = filmeReflect.getConstructor(String.class, Integer.class, Double.class);
		System.out.println(constructor.getParameterCount());
		
		// Attributes
		Field[] fields = filmeReflect.getDeclaredFields();
		for(Field field: fields) {
			System.out.println("Field: " + field.getName());
		}
		
		// Methods
		Method[] declaredMethods = filmeReflect.getDeclaredMethods();
		for(Method method: declaredMethods) {
			System.out.println("Method: " + method.getName());
		}
		
	}
	
}
