package ru.progwards.lms.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CompetitionHelper {

	static class CompetitionComparator implements Comparator<CompetitionResult> {
	    @Override
	    public int compare(CompetitionResult r1, CompetitionResult r2) {
	        return -((Double)r1.getPoints()).compareTo(r2.getPoints());
	    }
	}	
	
	public static String getResultJSON(String name, ArrayList<CompetitionResult> results) {
		String str = "";
		int n = 0;
		Collections.sort(results, new CompetitionComparator());
		for(CompetitionResult result : results) {
			str += 	"      {\"name\": "+result.getName()+", \"number\": "+(++n)+", \"points\": "+result.getPoints()+"}";
			if (n < results.size())
				str += ",";
			str += "\r\n";
		}
		return "{\"competition\": {\r\n" + 				"  \"name\": "+name+",\r\n" + 
		"  \"results\": {\r\n" + 
		"    \"student\": [\r\n" +
		str+
		"    ]\r\n" + 
		"  }\r\n" + 
		"}}";
	}
}
