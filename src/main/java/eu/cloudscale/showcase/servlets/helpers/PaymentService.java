/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.servlets.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service("paymentService")
public class PaymentService{
	public static final String BASE_URL = "https://arcane-meadow-6418.herokuapp.com/";	
	
	@Async
	public Future<String> callPaymentService(String distribution, String attr1, String attr2, String attr3)
	{
		try {
			ExecutorService executor = Executors.newFixedThreadPool(1);
			String url = this.getUrl(distribution, attr1, attr2, attr3);
			Future<Response> response = executor.submit(new Request(new URL(url)));
			InputStream input = response.get().getBody();
			executor.shutdown();
		
			String body = IOUtils.toString(input, "UTF-8");
			return new AsyncResult<String>(body);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getUrl(String distribution, String attr1, String attr2, String attr3)
	{
		String url = "";
		if (distribution.equals("gauss")) {
            url = "?mu=" + attr1 + "&sigma=" + attr2 + "&k=0";
        }
        if (distribution.equals("expo")) {
            url = "?lambda=" + attr1 + "&k=" + attr3;
        }
        if (distribution.equals("gamma")) {
            url = "?alpha=" + attr1 + "&beta=" + attr2 + "&k=" + attr3;
        }
        if (distribution.equals("log")) {
            url = "?mu=" + attr1 + "&sigma=" + attr2 + "&k=" + attr3;
        }
        if (distribution.equals("pareto")) {
            url = "?alpha=" + attr1 + "&k=" + attr3;
        }
        if (distribution.equals("weibull")) {
            url = "?alpha=" + attr1 + "&beta=" + attr2 + "&k=" + attr3;
        }
        if (distribution.equals("uniform")){
            url = "?a=" + attr1 + "&b=" + attr2;
        }
        if (distribution.equals("constant"))
        {
            url = "?c=" + attr1;
        }
        return BASE_URL + distribution + url + "&test=false";
	}
}