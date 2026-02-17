package dev.aira.lambda.feedback.repository;

import dev.aira.lambda.feedback.domain.Feedback;

public interface FeedbackRepository{
    void save(Feedback feedback);
}
