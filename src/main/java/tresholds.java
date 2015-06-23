import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;



public class tresholds
{
	static final int HOME_INTERACTION_ID = 0;
	static final int BEST_SELLERS_INTERACTION_ID = 1;
	static final int NEW_PRODUCTS_INTERACTION_ID = 2;
	static final int PRODUCT_DETAIL_INTERACTION_ID = 3;
	static final int SEARCH1_INTERACTION_ID = 4;
	static final int SEARCH2_INTERACTION_ID = 5;
	static final int SEARCH3_INTERACTION_ID = 6;
	static final int SHOPPING_CART1_INTERACTION_ID = 7;
	static final int SHOPPING_CART2_INTERACTION_ID = 8;
	static final int BUY_INTERACTION_ID = 9;
	static final int ORDER_INQUIRY_INTERACTION_ID = 10;
	static final int BUY_CONFIRM_INTERACTION_ID = (Integer) null;

	public static void main(String[] args)
	{
		HashMap<Integer, LinkedHashMap<Integer, Integer[]>> tresholds = new HashMap<Integer, LinkedHashMap<Integer, Integer[]>>();
		
		LinkedHashMap<Integer, Integer[]> homeMap = new LinkedHashMap<Integer, Integer[]>();
		homeMap.put(9999, new Integer[] {SHOPPING_CART1_INTERACTION_ID, SHOPPING_CART2_INTERACTION_ID});
		homeMap.put(9559, new Integer[] {SEARCH1_INTERACTION_ID, SEARCH2_INTERACTION_ID, SEARCH3_INTERACTION_ID});
		homeMap.put(7688, new Integer[] {ORDER_INQUIRY_INTERACTION_ID});
		homeMap.put(7585, new Integer[] {NEW_PRODUCTS_INTERACTION_ID});
		homeMap.put(3792, new Integer[] {BEST_SELLERS_INTERACTION_ID});
		
		LinkedHashMap<Integer, Integer[]> bestSellersMap = new LinkedHashMap<Integer, Integer[]>();
		bestSellersMap.put(9999, new Integer[] {SHOPPING_CART1_INTERACTION_ID, SHOPPING_CART2_INTERACTION_ID});
		bestSellersMap.put(9942, new Integer[] {SEARCH1_INTERACTION_ID, SEARCH2_INTERACTION_ID, SEARCH3_INTERACTION_ID});
		bestSellersMap.put(4607, new Integer[] {HOME_INTERACTION_ID});
		
		LinkedHashMap<Integer, Integer[]> buyConfirmMap = new LinkedHashMap<Integer, Integer[]>();
		buyConfirmMap.put(9999, new Integer[] {SEARCH1_INTERACTION_ID, SEARCH2_INTERACTION_ID, SEARCH3_INTERACTION_ID});
		buyConfirmMap.put(342, new Integer[] {HOME_INTERACTION_ID});

		LinkedHashMap<Integer, Integer[]> buyRequestMap = new LinkedHashMap<Integer, Integer[]>();
		buyRequestMap.put(9999, new Integer[] {SHOPPING_CART1_INTERACTION_ID, SHOPPING_CART2_INTERACTION_ID});
		buyRequestMap.put(9595, new Integer[] {HOME_INTERACTION_ID});
		buyRequestMap.put(9199, new Integer[] {BUY_CONFIRM_INTERACTION_ID});
				
		LinkedHashMap<Integer, Integer[]> customerRegistrationMap = new LinkedHashMap<Integer, Integer[]>();
		customerRegistrationMap.put(9999, new Integer[] {SEARCH1_INTERACTION_ID, SEARCH2_INTERACTION_ID, SEARCH3_INTERACTION_ID});
		customerRegistrationMap.put(9619, new Integer[] {HOME_INTERACTION_ID});
		customerRegistrationMap.put(9145, new Integer[] {BUY_INTERACTION_ID});
		
		LinkedHashMap<Integer, Integer[]> newProductsMap = new LinkedHashMap<Integer, Integer[]>();
		newProductsMap.put( 9999, new Integer[] {SHOPPING_CART1_INTERACTION_ID, SHOPPING_CART2_INTERACTION_ID});
		newProductsMap.put(9941, new Integer[] {SEARCH1_INTERACTION_ID, SEARCH2_INTERACTION_ID, SEARCH3_INTERACTION_ID});
		newProductsMap.put(9867, new Integer[] {PRODUCT_DETAIL_INTERACTION_ID});
		newProductsMap.put(299, new Integer[] {HOME_INTERACTION_ID});
		
		LinkedHashMap<Integer, Integer[]> orderDisplayMap = new LinkedHashMap<Integer, Integer[]>();
		orderDisplayMap.put(9999, new Integer[] {SEARCH1_INTERACTION_ID, SEARCH2_INTERACTION_ID, SEARCH3_INTERACTION_ID});
		orderDisplayMap.put(802, new Integer[] {HOME_INTERACTION_ID});
		
		
		
		tresholds.put(HOME_INTERACTION_ID, homeMap);
	}
}
