package h8pdf;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;


public class FillFieldsHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger log = Logger.getLogger(FillFieldsHandler.class);
    private static Map<String, String> headers = Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless");

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        input.keySet().forEach(key -> {
            log.debug("key:" + key);
            if (!key.equals("body")) {
                log.debug("value:" + input.get(key));
            }
        });
        log.debug("body class:" + input.get("body").getClass());
        String body = input.get("body").toString();
        if (body == null) {
            Response errorResponse = new Response("Expected, but did not receive, a pdf file.", Collections.EMPTY_MAP);
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody(errorResponse)
                    .setHeaders(headers)
                    .build();
        }

        Map<String, String> requestHeaders = (LinkedHashMap) input.get("headers");
        String contentType = requestHeaders.get("content-type");
        String[] contentTypeParts = contentType.split("; boundary=");
        String encodingType = contentTypeParts[0];
        String boundary = contentTypeParts[1];
        log.debug("contentType:|" + contentType + "|");
        log.debug("encodingType:|" + encodingType + "|");
        log.debug("boundary:|" + boundary + "|");

        log.info("first 500 characters of the body:\n" + body.substring(0, 500));
        log.info("last 500 characters of the body:\n" + body.substring(body.length() - 501, body.length() - 1));
        int marker = body.indexOf("/Filter /FlateDecode");
        log.info("potentially corrupt stream data:\n" + body.substring(marker - 25, marker + 300));

        try {
            File temporaryPdf = File.createTempFile("temp-", ".pdf");
            Map<String, String> formData = MultipartFormDataParser.parse(body, boundary, temporaryPdf);
//            File filledPdf = PdfFormFiller.addFieldData(formData, temporaryPdf.getAbsolutePath());
//            temporaryPdf.delete();


            Map<String, String> pdfHeaders = new HashMap<>(2);
            pdfHeaders.put("Content-Type", "application/force-download");
            pdfHeaders.put("Content-Disposition", "attachment; filename=\"filled.pdf\"");
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setRawBody(FileUtils.readFileToString(temporaryPdf, "UTF-8"))
                    .setHeaders(pdfHeaders)
                    .build();

        } catch (IOException e) {

            Response errorResponse = new Response("Failed to process the file.", Collections.EMPTY_MAP);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(errorResponse)
                    .setHeaders(headers)
                    .build();

        }


    }
}
