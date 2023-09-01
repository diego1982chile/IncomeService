package cl.dsoto.incomes.resources;

import cl.dsoto.incomes.entities.Debt;
import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.services.DebtService;
import cl.dsoto.incomes.services.FeeService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
@Produces(APPLICATION_JSON)
@Path("debts")
@RolesAllowed({"ADMIN","USER"})
//@PermitAll
public class DebtResource {

    @Inject
    DebtService debtService;

    String errorMsg;

    static private final Logger logger = Logger.getLogger(DebtResource.class.getName());


    @GET
    @Path("{id}")
    public Response getDebtById(@PathParam("id") int id) {
        try {
            Debt debt = debtService.getDebtById(id);
            return Response.ok(debt).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @GET
    @Path("house/{houseNumber}")
    public Response getDebtsByHouse(@PathParam("houseNumber") int houseId) {
        try {
            List<Debt> debts = debtService.getDebtByHouse(houseId);
            return Response.ok(debts).build();
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
            List<Debt> debts = debtService.getDebtsByPayment(id);
            return Response.ok(debts).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @GET
    @Path("house/{houseNumber}/payment/{id}")
    public Response getDebtsByHouseAndPayment(@PathParam("houseNumber") int houseId, @PathParam("id") int id) {
        try {
            List<Debt> debts = debtService.getDebtByHouse(houseId);
            if (id > 0) {
                debts.addAll(debtService.getDebtsByPayment(id));
            }
            return Response.ok(debts).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

}
