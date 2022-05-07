package dev.itzrozzadev.core.number;


import lombok.experimental.UtilityClass;

@UtilityClass
public class Number {

	public String formatTwoDecimalPlaces(double number) {
		return String.format("%.2f", number);
	}
}
