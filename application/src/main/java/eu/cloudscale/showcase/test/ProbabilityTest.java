package eu.cloudscale.showcase.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ProbabilityTest 
{
	static int cumulativeProb = 0;
	Random rand = new Random();
	
	ArrayList<Item> items = new ArrayList<Item>(){{
			add(new Item("Home", 29)); 
			add(new Item("New products", 11)); 
			add(new Item("Best Sellers", 11));
			add(new Item("Product Detail", 21)); 
			add(new Item("Search Request", 12));
			add(new Item("Search Results", 11)); 
			add(new Item("Order", 5));
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
	
	private ArrayList<Item> createNewItemsArray() {
		int sum = this.sumProbs();
		ArrayList<Item> newItems = new ArrayList<Item>(sum);
		for(Item item : items)
		{
			for(int i=0; i<item.probability; i++)
			{
				newItems.add(new Item(item.operation, item.probability));
			}
		}
		return newItems;
	}

	private int sumProbs()
	{
		int sum = 0;
		for(Item i : items)
		{
			sum+=i.probability;
		}
		
		return sum;
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
	
	public static void main(String[] args)
	{
		ProbabilityTest probTest = new ProbabilityTest();
		
		HashMap<String, Integer> probs = new HashMap<String, Integer>();
		for( int i=0; i < 10000; i++)
		{
			Item generatedItem = probTest.getRandom();
			if (!probs.containsKey(generatedItem.operation))
			{
				probs.put(generatedItem.operation, 1);
			}
			
			probs.put(generatedItem.operation, probs.get(generatedItem.operation)+1);
		}
		
		for (String operation : probs.keySet())
		{
			System.out.println(operation + " -> " + probs.get(operation) + " (" + ((probs.get(operation)*100)/10000.0) + "%)");
		}
		
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
