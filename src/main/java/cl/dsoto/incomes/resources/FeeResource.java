package cl.dsoto.incomes.resources;

import cl.dsoto.incomes.dtos.FeeDTO;
import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.entities.Year;
import cl.dsoto.incomes.services.FeeService;
import cl.dsoto.incomes.services.YearService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
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
@Path("fees")
@RolesAllowed({"ADMIN","USER"})
//@PermitAll
public class FeeResource {

    @Inject
    FeeService feeService;

    String errorMsg;

    static private final Logger logger = Logger.getLogger(FeeResource.class.getName());


    @GET
    @Path("{id}")
    public Response getFeeById(@PathParam("id") int id) {
        try {
            Fee fee = feeService.getFeeById(id);
            return Response.ok(fee).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @GET
    @Path("unpaid/{id}")
    public Response getFeesHistory(@PathParam("id") int id) {
        try {
            List<Fee> fees = feeService.getUnpaidFees(id);
            return Response.ok(fees).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @GET
    @Path("payment/{id}")
    public Response getFeesByPayment(@PathParam("id") int id) {
        try {
            List<Fee> fees = feeService.getFeesByPayment(id);
            return Response.ok(fees).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @GET
    @Path("house/{houseNumber}")
    public Response getFeesByHouse(@PathParam("houseNumber") int houseId) {
        try {
            List<Fee> fees = feeService.getFeeByHouse(houseId);
            return Response.ok(fees).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @POST
    @Path("save")
    public Response savePayment(Fee fee) {
        try {
            Fee newFee = feeService.saveFee(fee);
            return Response.ok(newFee).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            errorMsg = getRootCause(e);
        }
        return Response.serverError().entity(errorMsg).build();
    }

    private String getRootCause(Exception e) {
        Throwable cause = e.getCause();

        while(cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getMessage();
    }
}
