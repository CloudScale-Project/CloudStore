package eu.cloudscale.showcase.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

public class ProbabilityTest 
{
	static int cumulativeProb = 0;
	Random rand = new Random();
	
	static final int steps = 5000;
	
	static ArrayList<Item> items = new ArrayList<Item>(){{
			add(new Item("Home", 29)); 
			add(new Item("New products", 11)); 
			add(new Item("Best Sellers", 11));
			add(new Item("Product Detail", 21)); 
			add(new Item("Search Request", 12));
			add(new Item("Search Results", 11)); 
			add(new Item("Shopping Cart", 2));
			add(new Item("Customer Registration", 0.82));
			add(new Item("Buy Request", 0.75));
			add(new Item("Buy Confirm", 0.69));
			add(new Item("Order Inquiry", 0.30));
			add(new Item("Order Display", 0.25));
			add(new Item("Admin Request", 0.10));
			add(new Item("Admin Confirm", 0.09));
	}};
	
	public ProbabilityTest()
	{
		items = this.transfromProbs();
		for(Item item : items)
		{
			cumulativeProb = cumulativeProb + (int) item.probability;
		}
		
		
		
	}
	
	public Item getRandom()
	{
		int index = rand.nextInt(cumulativeProb);
		int sum=0;
		int i=0;
		while(sum < index)
		{
			sum = sum + (int) items.get(i++).probability;
		}
		return items.get(Math.max(0, i-1));
	}

	public Item generate()
	{
		Random random = new Random();
		int p = random.nextInt(items.size());		
		
		return items.get(p);
	}
	
	public ArrayList<Item> transfromProbs()
	{
		ArrayList<Item> newItems = new ArrayList<Item>();
		
		for(Item i : items)
		{
			newItems.add(new Item(i.operation, i.probability * 100));
		}
		
		return newItems;
	}
	
	public int maxProb()
	{
		int max=-1;
		for(Item item : items)
		{
			if(item.probability > max )
			{
				max = (int) item.probability;
			}
		}
		return max;
	}
	
	public int test1()
	{
		final int ran = new Random().nextInt(100);
		if (ran > 50) { return 3; }
		else if (ran > 20) { return 2; } 
		else { return 1; }
	}
	
	public static void main(String[] args)
	{
		ArrayList<Pair<Item, Double>> itemWeights = new ArrayList<Pair<Item, Double>>();
		for(Item i : items)
		{
			itemWeights.add(new Pair(i, i.probability));
		}
		
		

		ProbabilityTest probTest = new ProbabilityTest();
		
		HashMap<String, Integer> probs = new HashMap<String, Integer>();
		for( int i=0; i < steps; i++)
		{
			Item generatedItem = (Item) new EnumeratedDistribution(itemWeights).sample();
//			Item generatedItem = probTest.getRandom();
			if (!probs.containsKey(generatedItem.operation))
			{
				probs.put(generatedItem.operation, 1);
			}
			
			probs.put(generatedItem.operation, probs.get(generatedItem.operation)+1);
			
//			String generatedItem = String.valueOf(probTest.test1());
//			if (!probs.containsKey(generatedItem))
//			{
//				probs.put(generatedItem, 1);
//			}
//			
//			probs.put(generatedItem, probs.get(generatedItem)+1);
		}
		
		for (String operation : probs.keySet())
		{
			double percentage = ((probs.get(operation)*100)/(steps*1.0));
			double deviation = (probTest.getProbability(operation) - (percentage*100))/100;
			double percentageReal = probTest.getProbability(operation)/100;
			System.out.println(operation + " -> " + probs.get(operation) + " (" +  percentage + "%), deviation = " + deviation + " (" + (0.05 * percentageReal ) +")");
		}
		
	}	
	
	private double getProbability(String operation)
	{
		for( Item i : items)
		{
			if( i.operation.equals(operation))
			{
				return i.probability;
			}
		}
		return -1000;
	}
}

class Item
{
	String operation;
	double probability;
	
	public Item(String operation, double probability)
	{
		this.operation = operation;
		this.probability = probability;
	}
}
