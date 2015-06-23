package eu.cloudscale.showcase.test;

import java.util.HashMap;
import java.util.Random;

public class Tresholds
{

	enum Operation{
		ADMIN_CONFIRM,
		ADMIN_REQUEST,
		BEST_SELLERS,
		BUY_CONFIRM,
		BUY_REQUEST,
		CUSTOMER_REGISTRATION,
		HOME,
		NEW_PRODUCTS,
		ORDER_DISPLAY,
		ORDER_INQUIRY,
		PRODUCT_DETAIL,
		SEARCH_REQUEST,
		SEARCH_RESULTS,
		SHOPPING_CART
	}

	/*
	 * See http://www.tpc.org/tpcw/spec/tpcw_v16.pdf page 94
	 */
	public static final int[][] tresholdsMatrix = {
		{0, 0, 0, 0, 0, 0, 9952, 0, 0, 0, 0, 9999, 0, 0},
		{8999, 0, 0, 0, 0, 0, 9999, 0, 0, 0, 0, 0, 0, 0},
		{0,0,0,0,0, 0, 167, 0,0,0, 472, 9927,0,9999},
		{0,0,0,0,0, 0, 84, 0,0,0,0,9999,0,0},
		{0,0,0,4614,0, 0, 6546, 0,0,0,0,0,0,9999},
		{0,0,0,0,8666, 0, 8760, 0,0,0,0,9999,0,0},
		{0,0,3124,0,0, 0, 0, 6249,0, 6718,0,7026,0,9999},
		{0,0,0,0,0, 0, 156, 0,0,0,9735,9784,0,9999},
		{0,0,0,0,0, 0, 69, 0,0,0,0,9999,0,0},
		{0,0,0,0,0, 0, 72, 0,8872,0,0,9999,0,0},
		{0,58,0,0,0, 0, 832, 0,0,0,1288,8603,0,9999},
		{0,0,0,0,0, 0, 635, 0,0,0,0,0,9135,9999},
		{0,0,0,0,0, 0, 2657, 0,0,0,9294,9304,0,9999},
		{0,0,0,0,0, 2585, 9552, 0,0,0,0,0,0,9999},
	};
	public static void main(String[] args)
	{
		Tresholds tresholds = new Tresholds();
		int incomeOperation = Operation.HOME.ordinal();
		
		HashMap<Integer, Integer> foo = new HashMap<Integer, Integer>();
		int steps = 100;
		
		for( int i = 0; i < steps; i++)
		{
			int outcomeOperation = tresholds.nextOperation(incomeOperation);
			
			int val = 0;
			if(foo.containsKey(outcomeOperation))
			{
				val = foo.get(outcomeOperation)+1;
			}
			
			foo.put(outcomeOperation, val);
			incomeOperation = outcomeOperation;
		}
		
		double percentageSum = 0;
		for( int i : foo.keySet())
		{
			double percentage = (foo.get(i)*100) / (steps*1.0);
			System.out.println(Operation.values()[i] + " = " + percentage);
			percentageSum += percentage;
		}
		System.out.println(percentageSum);
	}
	
	private int nextOperation(int currentOperation)
	{
		int newOperation = -1;
		
		Random rand = new Random();
		int r = 1 + rand.nextInt(9999);
		
		for( int i = 0; i < tresholdsMatrix[currentOperation].length; i++)
		{
			int t = tresholdsMatrix[currentOperation][i];
			if( r <= t)
				return i;
		}
		
		return newOperation;
	}
}