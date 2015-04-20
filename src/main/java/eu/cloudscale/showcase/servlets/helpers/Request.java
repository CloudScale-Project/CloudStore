package eu.cloudscale.showcase.servlets.helpers;

import java.net.URL;
import java.util.concurrent.Callable;

import eu.cloudscale.showcase.servlets.helpers.Response;

public class Request implements Callable<Response> {
    private URL url;

    public Request(URL url) {
        this.url = url;
    }

    @Override
    public Response call() throws Exception {
        return new Response(url.openStream());
    }
}