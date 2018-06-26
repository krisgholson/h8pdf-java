package h8pdf;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PdfFieldParser {

    private static final Logger log = Logger.getLogger(PdfFieldParser.class);


    static List<Field> parse(String path) throws IOException {
        return parse(new FileInputStream(path));
    }

    static List<Field> parse(InputStream inputStream) throws IOException {
        List<Field> fields = new ArrayList<>();
        try {
            PDDocument pdfDocument = PDDocument.load(inputStream);
            PDDocumentCatalog documentCatalog = pdfDocument.getDocumentCatalog();
            PDAcroForm acroForm = documentCatalog.getAcroForm();
            acroForm.getFields().forEach(pdField -> {
                fields.add(new Field(pdField.getFullyQualifiedName(), pdField.getValueAsString(), pdField.getFieldType(), pdField.isReadOnly(), pdField.isRequired()));
            });
        } catch (Exception e) {
            log.error("could not load provided input stream " + inputStream, e);
        } finally {
            return fields;
        }
    }

}
