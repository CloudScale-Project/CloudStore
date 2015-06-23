package eu.cloudscale.showcase.test;

import java.util.HashMap;
import java.util.Random;

public class Prob {
	public static void main(String[] args) {

		String string = "";
		Random r = new Random();
		HashMap<String, Integer> foo = new HashMap<String, Integer>();
		for (int i = 0; i < 100; i++) {
			int rand = r.nextInt(100);
			if (rand < 44) {
				string = "a";
			} else if (rand >= 44 & rand < 66) {
				string = "b";
			} else if (rand >= 66 && rand < 96) {
				string = "c";
			} else if (rand >= 96) {
				string = "d";
			}
			
			int val = 0;
			if(foo.containsKey(string))
			{
				val = foo.get(string);
			}
			foo.put(string, val+1);
		}
		
		int probSum = 0;
		
		for(String s : foo.keySet())
		{
			probSum+=foo.get(s);
			System.out.println(s + " -> " + foo.get(s));
		}
		
		System.out.println("sum = " + probSum);
	}
}