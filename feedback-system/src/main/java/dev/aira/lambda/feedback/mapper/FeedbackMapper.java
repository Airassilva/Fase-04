package dev.aira.lambda.feedback.mapper;

import dev.aira.lambda.feedback.domain.Feedback;
import dev.aira.lambda.feedback.dto.FeedbackResponse;


public class FeedbackMapper {

    private FeedbackMapper() {
    }

    public static FeedbackResponse toResponse (Feedback feedback){
        return new FeedbackResponse(
                    feedback.getId(),
                    feedback.getUrgencia(),
                    feedback.getCriadoEm(),
                    feedback.getDescricao()
        );
    }
}
