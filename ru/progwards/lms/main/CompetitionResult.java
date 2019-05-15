package ru.progwards.lms.main;

public class CompetitionResult {
	String name;
	double points;
	
	public CompetitionResult(String name, double points) {
		this.name = name;
		this.points = points;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getPoints() {
		return points;
	}
	
	public void setResult(double points) {
		this.points = points;
	}
}
