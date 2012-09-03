package net.frontlinesms.plugins.patientview.utils;

public interface MapFunction<O,A> {
	void map(O object, A accumulator);
}