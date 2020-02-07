package com.xba.file.builder;

import com.xba.file.common.Errors;
import com.xba.file.common.Field;
import com.xba.file.common.FileBuilderException;
import com.xba.file.common.FileNameIncrementalType;
import com.xba.file.common.FileType;
import com.xba.file.csv.CsvFileBuilder;

import java.util.List;

public class FileBuilderFactory {

  public FileBuilder create(
      String baseDirectory,
      String namePrefix,
      String nameSuffix,
      FileNameIncrementalType fileNameIncrementalType,
      List<Field> fields,
      FileType fileType,
      int numRows
  ) throws FileBuilderException {
    switch (fileType) {
      case CSV:
        return new CsvFileBuilder(baseDirectory, namePrefix, nameSuffix, fileNameIncrementalType, fields, numRows);
      default:
        throw new FileBuilderException(Errors.FILE_BUILDER_00.getErrorMessage(), fileType.getFileType());
    }
  }

}
