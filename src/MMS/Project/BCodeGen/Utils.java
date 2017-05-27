package MMS.Project.BCodeGen;

import java.time.LocalTime;
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
}
