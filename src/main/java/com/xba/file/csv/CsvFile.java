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

import com.xba.file.common.BaseFile;
import com.xba.file.common.Errors;
import com.xba.file.common.FileBuilderException;
import com.xba.file.common.FileType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvFile extends BaseFile {

  protected List<Row> fileRows;
  protected String[] headers;

  public CsvFile(
      String baseDirectory,
      String namePrefix,
      String nameSuffix,
      String randomIncrementalPrefix,
      List<Row> fileRows,
      String[] headers
  ) {
    super(baseDirectory, namePrefix, nameSuffix, randomIncrementalPrefix);
    this.fileRows = fileRows;
    this.headers = headers;
  }

  private String addSlashIfNeeded(String baseDirectory) {
    return baseDirectory.endsWith("/")? baseDirectory : baseDirectory + "/";
  }

  @Override
  public void create() throws FileBuilderException {
    String fullFileName = String.format(
        "%s%s-%s.%s",
        addSlashIfNeeded(baseDirectory),
        randomIncrementalPrefix,
        namePrefix,
        nameSuffix
    );
    try (FileWriter fileWriter = new FileWriter(fullFileName)) {
      try (CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(headers))) {
        for (Row row : fileRows) {
          printer.printRecord(row.toString());
        }
      }
    } catch (IOException e) {
      throw new FileBuilderException(Errors.FILE_BUILDER_02.getErrorMessage(), FileType.CSV.getFileType());
    }

  }

}
