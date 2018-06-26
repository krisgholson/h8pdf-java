package h8pdf;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class MultipartFormDataParser {

    private static final Logger log = Logger.getLogger(MultipartFormDataParser.class);

    static Map<String, String> parse(String multipartFormData, String boundary, File pdfFile) throws IOException {
        Map<String, String> formData = new HashMap<>();
        MultipartStream multipartStream = new MultipartStream(new ByteArrayInputStream(multipartFormData.getBytes(StandardCharsets.UTF_8)), boundary.getBytes(StandardCharsets.UTF_8));
        boolean nextPart = multipartStream.skipPreamble();


        while (nextPart) {
            String multipartHeaders = multipartStream.readHeaders();

            // handle pdf file
            if (multipartHeaders.contains("Content-Type: application/pdf")) {
                FileOutputStream temporaryPdfOutputStream = new FileOutputStream(pdfFile);
                multipartStream.readBodyData(temporaryPdfOutputStream);
                temporaryPdfOutputStream.flush();
                IOUtils.closeQuietly(temporaryPdfOutputStream);
            }
            // handle "regular" form field information
            else {
                ByteArrayOutputStream fieldData = new ByteArrayOutputStream(32);
                String fieldName = StringUtils.substringBetween(multipartHeaders, "form-data; name=\"", "\"");
                multipartStream.readBodyData(fieldData);
                formData.put(fieldName, fieldData.toString());
                IOUtils.closeQuietly(fieldData);
            }

            nextPart = multipartStream.readBoundary();
        }
        return formData;
    }

}
