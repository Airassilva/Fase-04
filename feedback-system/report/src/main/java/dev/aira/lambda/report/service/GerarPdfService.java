package dev.aira.lambda.report.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import dev.aira.lambda.report.domain.Relatorio;
import dev.aira.lambda.report.dto.DadosRelatorioSemanal;
import dev.aira.lambda.report.dto.DiaRelatorio;
import dev.aira.lambda.report.exception.ErroGerarPdfException;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.ByteArrayOutputStream;

@ApplicationScoped
public class GerarPdfService {

    public byte[] gerarPdf(DadosRelatorioSemanal dados) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();

            document.add(new Paragraph("RELATÓRIO SEMANAL"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Resumo por Dia:"));
            document.add(new Paragraph(" "));

            for (DiaRelatorio dia : dados.getDias()) {
                document.add(new Paragraph(
                        dia.getData() + " - Quantidade: " + dia.getQuantidade()
                ));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Resumo por Urgência:"));
            document.add(new Paragraph(" "));

            for (Relatorio urg : dados.getUrgencias()) {
                document.add(new Paragraph(
                        urg.getChave() + " - Quantidade: " + urg.getQuantidade()
                ));
            }

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new ErroGerarPdfException(e);
        }
    }
}
