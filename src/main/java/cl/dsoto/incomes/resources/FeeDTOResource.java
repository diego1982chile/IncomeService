package cl.dsoto.incomes.resources;

import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.services.FeeService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
@Produces(APPLICATION_JSON)
@Path("feesDTO")
@RolesAllowed({"ADMIN","USER"})
//@PermitAll
public class FeeDTOResource {

    @Inject
    FeeService feeService;

    static private final Logger logger = Logger.getLogger(FeeDTOResource.class.getName());

    @GET
    @Path("{year}")
    public Response getAllFees(@PathParam("year") int year) {
        try {
            List<Map<String, Object>> fees = feeService.getFees(year);
            return Response.ok(fees).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

}
