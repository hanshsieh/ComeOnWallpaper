package org.comeonwallpaper.conf;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class DataSource {
  private static final Gson gson = new Gson();
  private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private JsonElement jsonData;

  private DataSource(@NonNull JsonElement jsonData) {
    this.jsonData = jsonData;
  }

  @NonNull
  public static DataSource fromJson(@NonNull JsonElement jsonData) {
    return new DataSource(jsonData);
  }

  @NonNull
  public static DataSource fromFile(@NonNull File file) throws IOException {
    try (FileInputStream inputFile = new FileInputStream(file)) {
      InputStreamReader reader = new InputStreamReader(
          new BufferedInputStream(inputFile), StandardCharsets.UTF_8);
      return new DataSource(JsonParser.parseReader(reader));
    }
  }

  public <T> T asType(Class<T> type) throws IllegalArgumentException, ConstraintViolationException {
    T data;
    try {
      data = gson.fromJson(jsonData, type);
    } catch (Exception ex) {
      throw new IllegalArgumentException("Unable to convert the data as type " + type, ex);
    }
    Set<ConstraintViolation<T>> violations = validator.validate(data);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    return data;
  }

  @NonNull
  private String buildViolationMsg(Set<ConstraintViolation<Config>> violations) {
    StringBuilder strBuilder = new StringBuilder();
    for (ConstraintViolation<?> violation : violations) {
      strBuilder.append(violation.getMessage());
      strBuilder.append('\n');
    }
    return strBuilder.toString();
  }
}
