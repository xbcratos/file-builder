/*
 * Copyright 2020 Xavier Baques
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xba.file.csv;

import com.xba.file.builder.FileBuilder;
import com.xba.file.common.BaseFile;
import com.xba.file.common.Errors;
import com.xba.file.common.Field;
import com.xba.file.common.FileNameIncrementalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

public class CsvFileBuilder implements FileBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(CsvFileBuilder.class);

  private final String baseDirectory;
  private final String namePrefix;
  private final String nameSuffix;
  private final FileNameIncrementalType fileNameIncrementalType;
  private final List<Field> fields;
  private final int numRows;
  private final Random random;

  private int incrementalInt;
  private List<String> headers;

  public CsvFileBuilder(
      String baseDirectory,
      String namePrefix,
      String nameSuffix,
      FileNameIncrementalType fileNameIncrementalType,
      List<Field> fields,
      int numRows
  ) {
    this.baseDirectory = baseDirectory;
    this.namePrefix = namePrefix;
    this.nameSuffix = nameSuffix;
    this.fileNameIncrementalType = fileNameIncrementalType;
    this.fields = fields;
    this.numRows = numRows;
    this.random = new Random();
    this.incrementalInt = 0;
    this.headers = new ArrayList<>();
  }

  private Field getNewField(Field originalField) {
    Field field = new Field(originalField.getName(), originalField.getFieldType(), originalField.getValue());

    // If original field value is null we create a random value otherwise we keep original field value so we can have
    // this way static values
    if (field.getValue() == null) {
      switch (field.getFieldType()) {
        case STRING:
          field.setValue(UUID.randomUUID().toString());
          break;
        case BOOLEAN:
          field.setValue(random.nextBoolean());
          break;
        case INTEGER:
          field.setValue(random.nextInt());
          break;
        default:
          LOG.warn(Errors.FILE_BUILDER_01.getErrorMessage(), field.getName(), field.getFieldType().getFieldType());
          field = null;
      }
    }

    return field;
  }

  private void getHeadersFromFieldsList() {
    fields.forEach(field -> headers.add(field.getName()));
  }

  private List<Row> generateRows() {
    List<Row> rows = new ArrayList<>();

    for (int i = 0; i < numRows; ++i) {
      Row row = new Row();

      fields.forEach(field -> {
        Field newField = getNewField(field);
        row.addField(newField);
      });

      rows.add(row);
    }

    return rows;
  }

  @Override
  public BaseFile build() {
    String incrementalValue;
    switch (fileNameIncrementalType) {
      case UUID:
        incrementalValue = UUID.randomUUID().toString();
        break;
      case TIMESTAMP:
        incrementalValue = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().toString();
        break;
      case COUNTER:
      default:
        incrementalValue = Integer.toString(incrementalInt);
        ++incrementalInt;
    }
    return new CsvFile(
        baseDirectory,
        namePrefix,
        nameSuffix,
        incrementalValue,
        generateRows(),
        headers.toArray(new String[0])
    );
  }
}
