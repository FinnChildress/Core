package dev.itzrozzadev.core.exception;

import lombok.Getter;

public class BaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Getter
	private final String[] messages;

	public BaseException(final String... messages) {
		super("");

		this.messages = messages;
	}
}
