package com.proofpoint.anomalytics.rtstats;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/")
public class IndexResource
{
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getIndex()
            throws IOException
    {
        return Resources.toString(Resources.getResource("index.html"), Charsets.UTF_8);
    }
}
