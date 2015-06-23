package eu.cloudscale.showcase.test;

import java.util.Random;
import java.util.HashMap;

public class ProbabilityTest2 
{
    static HashMap<String, Integer> foo = new HashMap<String, Integer>();

    static String[] operations = {
    		"None",
            "/admin-confirm",
            "/admin-request",
            "/best-sellers",
            "/buy-confirm",
            "/buy",
            "/customer-registration",
            "/home",
            "/new-products",
            "/order-display",
            "/order-inquiry",
            "/product-detail",
            "/search-request",
            "/search-result",
            "/shopping-cart"
    };

	public static void main(String[] args) throws Exception
	{
        ProbabilityTest2 pt2 = new ProbabilityTest2();
        int steps = 1000000;
        for(int i=0; i<steps; i++)
        {
            int op = pt2.getOperation();
            int val = 1;

            if(foo.containsKey(operations[op]))
            {
                val = foo.get(operations[op])+1;
            }
            foo.put(operations[op], val);
        }
        
        double sum = 0;
        for(String op : foo.keySet())
        {
            double prob = ((foo.get(op)*100)/(steps*1.0));
            sum += prob;
            System.out.println(op + " = " + prob);
        }
        System.out.println("--------------------------");
        System.out.println("PROB SUM = " + sum);
    }

    private int getOperation() throws Exception
    {
   		int[] browsingMix = {
   				0, // None
				9, // admin confirm
				10, // admin request
				1100, // best-sellers
				69, // buy-confirm
				75, //buy
				82, //customer-registration
				2900, //home
				1100, // new-products
				25, // order-display	
				30, // order-inquiry
				2100, // product-detail
				1200, // search-request
				1100, // search-result
				200 // shopping-cart
			};


        int probSum = 0;

        for( int i=0; i < browsingMix.length; i++)
        {
            probSum += browsingMix[i];
        }

        int[] range = new int[2 * browsingMix.length];

        for( int i=0; i < browsingMix.length; i++)
        {
            int sum = 0;
            for( int j=0; j<i; j++)
             {
                 sum += browsingMix[j];
             }

            range[i] = sum;
            range[i+1] = range[i] + browsingMix[i];
            //System.out.println(range[i] + " - " + range[i+1]);
        }
        Random rand = new Random();

        int randNum = rand.nextInt(probSum);
//        System.out.println("randNum = " + randNum);
        for(int i=0; i < browsingMix.length; i++)
        {
            if( randNum >= range[i] && randNum < range[i+1] )
            {
                //System.out.println("operation = " + operations[i]);
                return i;
            }	
        }
        throw new Exception("DREK NA PALCI");
	}
}
