package dev.aira.lambda.feedback.resource;

import dev.aira.lambda.feedback.dto.FeedbackRequest;
import dev.aira.lambda.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/avaliacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    private final FeedbackService feedbackService;

    public FeedbackResource(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @POST
    public Response enviarFeedback(@Valid FeedbackRequest request) {
       var feedback = feedbackService.executar(request);
       return Response.status(Response.Status.CREATED).entity(feedback).build();
    }
}
