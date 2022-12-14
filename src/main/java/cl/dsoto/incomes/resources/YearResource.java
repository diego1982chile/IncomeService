package cl.dsoto.incomes.resources;

import cl.dsoto.incomes.entities.Year;
import cl.dsoto.incomes.services.YearService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
@Produces(APPLICATION_JSON)
@Path("years")
@RolesAllowed({"ADMIN","USER"})
//@PermitAll
public class YearResource {

    @Inject
    YearService yearService;

    @Inject
    Principal principal;

    static private final Logger logger = Logger.getLogger(YearResource.class.getName());

    @GET
    public Response getAllYears() {
        try {
            List<Year> years = yearService.getYears();
            return Response.ok(years).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }
}
