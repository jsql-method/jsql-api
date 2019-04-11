package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.SchemaFieldResponse;
import pl.jsql.api.dto.response.SchemaResponse;
import pl.jsql.api.security.annotation.Security;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@CrossOrigin
@RestController
@RequestMapping("/api/schema")
public class SchemaController extends ValidateController {

    @Security(requireActiveSession = false)
    @GetMapping("/{requestClassName}")
    public BasicResponse<SchemaResponse> getSchema(@PathVariable("requestClassName") String requestClassName) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        SchemaResponse schemaResponse = this.createSchemaForClass(Class.forName("pl.jsql.api.dto.request." + requestClassName));

        return new BasicResponse<>(200, schemaResponse);

    }

    private List<String> filterNames = Arrays.asList("flags", "groups", "payload");

    @Autowired
    private MessageSource messageSource;

    private SchemaResponse createSchemaForClass(Class<?> clazz) throws IllegalAccessException, InvocationTargetException {

        SchemaResponse schemaDTO = new SchemaResponse();

        for (Field field : clazz.getDeclaredFields()) {

            schemaDTO.schema.computeIfAbsent(field.getName(), k -> new HashMap<>());

            for (Annotation annotation : field.getDeclaredAnnotations()) {

                Method[] methods = annotation.annotationType().getDeclaredMethods();

                for (Method annotationMethod : methods) {

                    if (filterNames.contains(annotationMethod.getName())) {
                        continue;
                    }

                    Object value = annotationMethod.invoke(annotation);

                    if(annotationMethod.getName().equals("message")){
                        String message = (String) value;
                        message = message.replace("$", "").replace("}","").replace("{", "");
                        value = messageSource.getMessage(message, null, Locale.ROOT);
                    }

                    SchemaFieldResponse schemaFieldDTO = new SchemaFieldResponse();
                    schemaFieldDTO.type = this.mapType(annotationMethod.getReturnType().getSimpleName());
                    schemaFieldDTO.name = annotationMethod.getName();
                    schemaFieldDTO.value = value;

                    schemaDTO.schema.get(field.getName()).put(annotationMethod.getName(), schemaFieldDTO);

                }

            }

        }

        return schemaDTO;

    }

    private String mapType(String type) {

        switch (type) {
            case "String":
                return "text";
            case "int":
                return "number";
            case "Boolean":
                return "boolean";
        }

        return type;

    }


}
