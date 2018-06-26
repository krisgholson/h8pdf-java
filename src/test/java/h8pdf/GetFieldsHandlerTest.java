package h8pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class GetFieldsHandlerTest {


    static final Logger log = Logger.getLogger(GetFieldsHandlerTest.class);

    String input = "src/test/resources/Demo_Document_fillable.pdf";
    String output = "build/test-output/Demo_Document_fillable.pdf";
//    "Demo_Document_fillable.pdf"
//    "pdf.fields.test.pdf"
//    "fw4.filled.pdf"


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

        List<Field> fields = PdfFieldParser.parse(input);
//        assertEquals(342, fields.size());

        fields.forEach(field -> {
            log.debug("name: " + field.getName());
            log.debug("value: " + field.getValue());
            log.debug("type: " + field.getType());
            log.debug("readOnly: " + field.isReadOnly());
            log.debug("required: " + field.isRequired());
            log.debug("==========");
        });

    }

    @Test
    void fillFormFields() throws Exception {


        List<Field> fieldData = new ArrayList<>();
        fieldData.add(new Field("witness1_name", "hello world"));
        fieldData.add(new Field("witness1_date", "12/24/72"));
        fieldData.add(new Field("lien_holder", "false"));
        fieldData.add(new Field("mortagee", "false"));
        fieldData.add(new Field("loss_payee", "false"));

        createFile(output);

        PDDocument pdfDocument = PDDocument.load(new File(input));
        PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();

        for (Field field : fieldData) {
            PDField pdField = acroForm.getField(field.getName());
            if (pdField == null) {
                continue;
            }
            String pdFieldType = pdField.getFieldType();
            if ("Btn".equals(pdFieldType)) {
                if (Boolean.valueOf(field.getValue())) {
                    pdField.setValue("Yes");
                } else {
                    pdField.setValue("Off");
                }
            } else if ("Tx".equals(pdFieldType)) {
                pdField.setValue(field.getValue());
            }
        }

        // Save and close the filled out form.
        pdfDocument.save(output);

    }
}
