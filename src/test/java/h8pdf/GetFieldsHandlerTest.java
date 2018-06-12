package h8pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

class GetFieldsHandlerTest {


    static final Logger log = Logger.getLogger(GetFieldsHandlerTest.class);

    String input = "src/test/resources/pdf.fields.test.pdf";
    String output = "build/test-output/pdf.fields.test.filled.pdf";

    String w4Input = "src/test/resources/fw4.pdf";
    String w4Output = "build/test-output/fw4.filled.pdf";


    @BeforeAll
    static void performanceTweak() {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }

    void createFile(String name) throws Exception {
        File outputFile = new File(name);
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        }
    }

    @Test
    void getFormFields() throws Exception {

        PDDocument pdfDocument = PDDocument.load(new File(input));
        PDDocumentCatalog documentCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
        acroForm.getFields().forEach(pdField -> {
            log.debug("FullyQualifiedName: " + pdField.getFullyQualifiedName());
            log.debug("FieldType: " + pdField.getFieldType());

            log.debug("==========");

        });
    }

    @Test
    void writeFormField() throws Exception {

        createFile(output);

        PDDocument pdfDocument = PDDocument.load(new File(input));
        PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
        PDTextField field = (PDTextField) acroForm.getField("Term_Months");
//        "Name1" // No glyph for U+0033 in font PQFAYY+Arial
//        "Member_Full_Legal_Name" // No glyph for U+0033 in font PQFAYY+Arial
//        "Term_Months" // OK
        // fails with No glyph for U+0074 in font PQFAYY+Arial
        // at org.apache.pdfbox.pdmodel.font.PDCIDFontType2.encode(PDCIDFontType2.java:363)
        // ..
        // at h8pdf.GetFieldsHandlerTest.writeFormField(GetFieldsHandlerTest.java:50)
        field.setValue("36");


        // Save and close the filled out form.
        pdfDocument.save(output);

    }
}
