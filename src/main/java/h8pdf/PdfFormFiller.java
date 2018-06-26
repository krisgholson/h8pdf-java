package h8pdf;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.*;
import java.util.Map;

public final class PdfFormFiller {

    private static final Logger log = Logger.getLogger(PdfFormFiller.class);


    static File addFieldData(Map<String, String> fieldData, String path) throws IOException {
        return addFieldData(fieldData, new FileInputStream(path));
    }

    static File addFieldData(Map<String, String> fieldData, InputStream inputStream) throws IOException {

        File filledPdf = File.createTempFile("temp-", ".pdf");
        PDDocument pdfDocument = PDDocument.load(inputStream);
        PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();

        for (String name : fieldData.keySet()) {
            PDField pdField = acroForm.getField(name);
            if (pdField == null) {
                continue;
            }
            String pdFieldType = pdField.getFieldType();
            String value = fieldData.get(name);
            if ("Btn".equals(pdFieldType)) {
                if (Boolean.valueOf(value)) {
                    pdField.setValue("Yes");
                } else {
                    pdField.setValue("Off");
                }
            } else if ("Tx".equals(pdFieldType)) {
                pdField.setValue(value);
            }
        }

        pdfDocument.save(filledPdf);
        IOUtils.closeQuietly(inputStream);
        return filledPdf;
    }

}
