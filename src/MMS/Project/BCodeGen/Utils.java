package MMS.Project.BCodeGen;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by x19de on 27.05.2017.
 */
public class Utils {
	
	public static void log(String msg, Level level, Object source){
		
		String scr = source
				.getClass()
				.getName()
				.substring(
						source
								.getClass()
								.getName()
								.lastIndexOf('.') + 1);
		
		System.out.printf("[%s][%s][%s]: %s%n",
		                  LocalTime.now().toString(),
		                  scr,
		                  level.toString(),
		                  msg);
	}
	
	public static void logErr(String msg, Level level, Object source){
		
		String scr = source
				.getClass()
				.getName()
				.substring(
						source
								.getClass()
								.getName()
								.lastIndexOf('.') + 1);
		
		System.err.printf("[%s][%s][%s]: %s%n",
		                  LocalTime.now().toString(),
		                  scr,
		                  level.toString(),
		                  msg);
	}
	
	public static void logEx(Exception ex, Level level, Object source){
		
		String scr = source
				.getClass()
				.getName()
				.substring(
						source
								.getClass()
								.getName()
								.lastIndexOf('.') + 1);
		
		String time = LocalTime.now().toString();
		
		System.err.printf("[%s][%s][%s]: %s - %s%n",
		                  time,
		                  scr,
		                  level.toString(),
		                  ex.toString(),
		                  ex.getMessage());
		
		String off = " > ";
		
		for(int i = 0; i < (time.length() + 2 + scr.length() + 2 + level.toString().length() + 2 + 1 + 1) - 3; i++ ){
			
			off += " ";
		}
		
		for(int i = 0; i < ex.getStackTrace().length; i++){
			
			System.err.println(off + ex.getStackTrace()[i]);
		}
	}
}
