package ru.progwards.lms.main;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestResult {
	
	private final static String dmErrorMessage = "не пройден."; 
	private final static String dmOkMessage = "пройден успешно."; 
	private final static String dmErrorPrefix = "ERROR"; 
	private final static String dmOkPrefix = "OK"; 
	private final static String dmTotalOkMessage = "TOTAL: В целом зачет, решение принято."; 
	private final static String dmTotalErrorMessage = "TOTAL: В целом не зачет, решение возвращено не доработку."; 
	private final static String dmPercentMessage = "Задание выролнено на"; 
	private final static String dmExeptionMessage = "Во время выполнения возникло исключение "; 
	// массив с данными для процессинга результата теста
	// 0: имя теста, как в assert, лучше всего ставить имя функции
	private final static int tdName = 0; 
	// 1: описание теста словами
	private final static int tdTitle = 1; 
	// 2: баллы за эту часть теста
	private final static int tdGrade = 2; 
	// 3: * отмечены обязательные части теста
	private final static int tdObligatory = 3; 
	
	public boolean pass;
	public double grade;
	public String comment;
	
	public TestResult(boolean pass, double grade, String comment) 
	{
		this.pass = pass;
		this.grade = grade;
		this.comment = comment;
	}

	private static double calcSumGrade(String[][] data) {
		double grade = 0;
		for(int i=1; i<data.length; i++)
			grade += Double.parseDouble(data[i][tdGrade]);
		return grade;
	}
	
	private static String getName(String[] tst)
	{
		if( tst[tdTitle].equals(""))
			return tst[tdName];
		else
			return tst[tdTitle];
	}

	private static String parseTestName(String name) {
		int comma = name.indexOf('(');
		return comma < 0 ? name : name.substring(0, comma);
	}
	
	private static String parsePackageName(String name) {
		int n = name.indexOf('(');
		String str = n < 0 ? name : name.substring(n+1);
		n = str.lastIndexOf('.');
		return n < 0 ? str : str.substring(0, n+1);
	}

	private static String exception2string(Failure fail) {
		String result = fail.getException().toString();
		String pack = parsePackageName(fail.getTestHeader());
		boolean header = true;
		for(StackTraceElement el: fail.getException().getStackTrace()) {
			if (!el.getClassName().contains(pack))
				if (header) {
					header = false;
					continue;
				}
				else
					break;
			else {
				header = false;
				result += "\n" + el.toString();
			}
		}
		return result;
	}
	
	public static TestResult defaultResult(Result result, String[][] data)
	{
		if (result != null) {
			double passingGrade = Double.parseDouble(data[0][tdGrade]);
			double maxGrade = calcSumGrade(data);
			double grade = 0;
			boolean testPassed =  true;
			String message = "";
			
			// для каждой проверки будет выдана строка в лог
			// data[0] содержит информацию о всем тесте, поэтому первую строку пропускаем
			for(int i=1; i<data.length; i++) {
				boolean passed = true;
				String[] tst = data[i];
				for(Failure fail : result.getFailures()) {
					if (parseTestName(fail.getTestHeader()).equals(tst[tdName])) {
						message += dmErrorPrefix+": Тест \""+getName(tst)+"\" "+ dmErrorMessage + " ";
						if (fail.getException().getClass() == java.lang.AssertionError.class)
							message +=  (fail.getMessage() == null ? "" : fail.getMessage()) + "\n";
						else
							message += (fail.getException() == null ? "" : dmExeptionMessage+ exception2string(fail)) +"\n";
						
						if (tst[tdObligatory].equals("*"))
							testPassed = false;
						passed = false;
						break;
					}
				}
				if (passed) {
					message += dmOkPrefix+": Тест \""+getName(tst)+"\" "+ dmOkMessage + "\n";
					grade += Double.parseDouble(tst[tdGrade]);
				}
			}
			
			// проверка на прходной балл, если задан
			if (passingGrade > 0 && grade < passingGrade)
				testPassed = false;
			double percent = 0;
			if (maxGrade != 0)
				percent = grade/maxGrade *100;
			if (testPassed)
				message += dmTotalOkMessage+" "+dmPercentMessage+" "+String.format("%3.2f", percent)+"%\n";
			else
				message += dmTotalErrorMessage+" "+dmPercentMessage+" "+String.format("%3.2f", percent)+"%\n";
			
			return new TestResult(testPassed, grade, message);
		}
		return null;
	}

}
