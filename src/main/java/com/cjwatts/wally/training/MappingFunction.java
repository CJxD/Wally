package com.cjwatts.wally.training;

import java.io.Serializable;

public interface MappingFunction extends Serializable {
	public double eval(double x);
}
