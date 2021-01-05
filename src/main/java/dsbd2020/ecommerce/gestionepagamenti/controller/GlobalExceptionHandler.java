package dsbd2020.ecommerce.gestionepagamenti.controller;

import dsbd2020.ecommerce.gestionepagamenti.exception.CustomException;
import dsbd2020.ecommerce.gestionepagamenti.exception.ExceptionResponse;
import dsbd2020.ecommerce.gestionepagamenti.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private KafkaTemplate<String, Map> dataKafkaTemplate;

    private Map<String, Object> kafka_msg = new HashMap<>();
    private Map<String, Object> value_msg = new HashMap<>();
    private String ipAddress;

    @Autowired
    GlobalExceptionHandler(KafkaTemplate<String, Map> dataKafkaTemplate) {
        this.dataKafkaTemplate = dataKafkaTemplate;
    }

    private void common(HttpServletRequest request) {
        ipAddress = request.getHeader("X-FORWARDED-FOR");
        kafka_msg.put("key", "http_errors");
        value_msg.put("timestamp", Instant.now().getEpochSecond());
        value_msg.put("sourceIp", ipAddress);
        value_msg.put("service", "gestionepagamenti");
        value_msg.put("request", request.getRequestURI() + "|" + request.getMethod());
    }

    void sendCustomMessage(Map<String, Object> data, String topicName) {
        LOG.info("---------------------------------");
        LOG.info("Sending Json Serializer : {}", data);
        LOG.info("--------------------------------");

        dataKafkaTemplate.send(topicName, data);
    }

    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    public ResponseEntity IPNVerifyException(HttpStatusCodeException e, HttpServletRequest request) {
        common(request);
        value_msg.put("error", e.getRawStatusCode());
        kafka_msg.put("value", value_msg);
        sendCustomMessage(kafka_msg, "logging");

        return ResponseEntity.status(e.getStatusCode()).body("Error in IPN verify request");
    }

    @ExceptionHandler({MissingRequestHeaderException.class, CustomException.class})
    public ResponseEntity<ExceptionResponse> BadRequest(Exception e, HttpServletRequest request) {
        common(request);
        value_msg.put("error", 400);
        kafka_msg.put("value", value_msg);
        sendCustomMessage(kafka_msg, "logging");

        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("400 BAD REQUEST");
        response.setErrorMessage(e.getMessage());
        response.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> Unauthorized(UnauthorizedException e, HttpServletRequest request) {
        common(request);
        value_msg.put("error", 401);
        kafka_msg.put("value", value_msg);
        sendCustomMessage(kafka_msg, "logging");

        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("401 UNAUTHORIZED");
        response.setErrorMessage(e.getMessage());
        response.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> ResourceNotFound(NoHandlerFoundException e, HttpServletRequest request) {
        common(request);
        value_msg.put("error", 404);
        kafka_msg.put("value", value_msg);
        sendCustomMessage(kafka_msg, "logging");

        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("404 NOT_FOUND");
        response.setErrorMessage(e.getMessage());
        response.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> ResourceNotFound(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        common(request);
        value_msg.put("error", 415);
        kafka_msg.put("value", value_msg);
        sendCustomMessage(kafka_msg, "logging");

        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("415 UNSUPPORTED MEDIA TYPE");
        response.setErrorMessage(e.getMessage());
        response.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<ExceptionResponse> InternalServerError(UnsupportedEncodingException e, HttpServletRequest request) {
        common(request);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        value_msg.put("error", sw.toString());
        kafka_msg.put("value", value_msg);
        sendCustomMessage(kafka_msg, "logging");

        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("500 INTERNAL SERVER ERROR");
        response.setErrorMessage(sw.toString());
        response.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
