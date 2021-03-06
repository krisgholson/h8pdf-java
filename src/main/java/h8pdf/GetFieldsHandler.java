package h8pdf;

import java.io.*;
import java.util.*;

import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class GetFieldsHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger log = Logger.getLogger(GetFieldsHandler.class);
    private static Map<String, String> headers = Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless");

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        input.keySet().forEach(key -> {
            log.debug("key:" + key);
            if (!key.equals("body")) {
                log.debug("value:" + input.get(key));
            }
        });
        String body = (String) input.get("body");
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
        log.debug("encodingType:" + encodingType);
        log.debug("boundary:" + boundary);

        log.info("first 200 characters of the body:\n" + body.substring(0, 200));

        try {
            File temporaryPdf = File.createTempFile("temp-", ".pdf");
            MultipartFormDataParser.parse(body, boundary, temporaryPdf);
            List<Field> detectedFields = PdfFieldParser.parse(temporaryPdf.getAbsolutePath());
            Map<String, Object> data = new HashMap<>();
            data.put("fields", detectedFields);
            temporaryPdf.delete();

            Response successResponse = new Response("Go Serverless v1.x! Your function executed successfully!", data);
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(successResponse)
                    .setHeaders(headers)
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
