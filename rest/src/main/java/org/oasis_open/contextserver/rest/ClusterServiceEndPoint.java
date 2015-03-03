package org.oasis_open.contextserver.rest;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.oasis_open.contextserver.api.ClusterNode;
import org.oasis_open.contextserver.api.services.ClusterService;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebService
@Produces(MediaType.APPLICATION_JSON)
@CrossOriginResourceSharing(
        allowAllOrigins = true,
        allowCredentials = true
)
public class ClusterServiceEndPoint {

    @Context
    private MessageContext messageContext;

    private ClusterService clusterService;

    public ClusterServiceEndPoint() {
        System.out.println("Initializing cluster service endpoint...");
    }

    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    public void setMessageContext(MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    @GET
    @Path("/")
    public List<ClusterNode> getClusterNodes() {
        return clusterService.getClusterNodes();
    }

    @GET
    @Path("/purge/{date}")
    public void purge(@PathParam("date") String date) {
        try {
            clusterService.purge(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
