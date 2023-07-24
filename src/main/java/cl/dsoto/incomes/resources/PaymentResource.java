package cl.dsoto.incomes.resources;

import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Payment;
import cl.dsoto.incomes.services.FeeService;
import cl.dsoto.incomes.services.HouseService;
import cl.dsoto.incomes.services.PaymentService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
@Produces(APPLICATION_JSON)
@Path("payments")
@RolesAllowed({"ADMIN","USER"})
//@PermitAll
public class PaymentResource {

    @Inject
    PaymentService paymentService;

    @Inject
    FeeService feeService;

    String errorMsg;

    static private final Logger logger = Logger.getLogger(PaymentResource.class.getName());

    @GET
    @Path("fee/{feeId}")
    public Response getPayments(@PathParam("feeId") int feeId) {
        try {
            List<Payment> payments = paymentService.findPaymentsByFee(feeId);
            return Response.ok(payments).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }


    @GET
    @Path("new/{feeId}")
    public Response getNewPayment(@PathParam("feeId") int feeId) {
        try {
            Payment payment = Payment.builder().number(feeService.generatePaymentNumber(feeId)).build();
            return Response.ok(payment).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }


    @GET
    @Path("new/list/{feeId}")
    public Response getNewPaymentList(@PathParam("feeId") int feeId) {
        try {
            List<Payment> payments = Collections.singletonList(Payment.builder().build());
            return Response.ok(payments).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }


    @GET
    @Path("{id}")
    public Response getPaymentById(@PathParam("id") int id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return Response.ok(payment).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    @GET
    @Path("list/{id}")
    public Response getPaymentListById(@PathParam("id") int id) {
        try {
            List<Payment> payments = Collections.singletonList(paymentService.getPaymentById(id));
            return Response.ok(payments).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }


    @POST
    @Path("save")
    public Response savePayment(Payment payment, @QueryParam("fees") List<Integer> fees) {
        try {
            Payment newPayment = paymentService.savePayment(payment, fees);
            return Response.ok(newPayment).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            errorMsg = getRootCause(e);
        }
        return Response.serverError().entity(errorMsg).build();
    }

    @DELETE
    @Path("delete/{id}")
    public Response deletePayment(@PathParam("id") long id) {
        try {
            paymentService.deletePayment(id);
            return Response.ok(id).build();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return Response.serverError().build();
    }

    private String getRootCause(Exception e) {
        Throwable cause = e.getCause();

        while(cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getMessage();
    }
}
