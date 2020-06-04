package com.ht.project.exception;

public class NeedLoginException extends RuntimeException {
	public NeedLoginException(String msg) {
		super(msg);
	}
}
