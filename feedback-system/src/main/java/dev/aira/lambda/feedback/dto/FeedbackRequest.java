package dev.aira.lambda.feedback.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FeedbackRequest {
    @NotNull
    @Min(0)
    @Max(10)
    public Integer nota;

    @NotBlank
    public String descricao;

    public FeedbackRequest(Integer nota, String descricao) {
        this.nota = nota;
        this.descricao = descricao;
    }
}
