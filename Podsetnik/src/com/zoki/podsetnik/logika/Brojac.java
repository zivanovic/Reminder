package com.zoki.podsetnik.logika;

import java.util.concurrent.TimeUnit;

public class Brojac {

	long starts;

	public static Brojac start() {
		return new Brojac();
	}

	private Brojac() {
		reset();
	}

	public Brojac reset() {
		starts = System.currentTimeMillis();
		return this;
	}

	public long time() {
		long ends = System.currentTimeMillis();
		return ends - starts;
	}

	public long time(TimeUnit unit) {
		return unit.convert(time(), TimeUnit.MILLISECONDS);
	}

}