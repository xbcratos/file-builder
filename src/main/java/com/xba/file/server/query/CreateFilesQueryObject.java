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

package com.xba.file.server.query;

import com.google.gson.Gson;
import com.xba.file.common.Field;
import com.xba.file.common.FileNameIncrementalType;
import com.xba.file.common.FileType;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class CreateFilesQueryObject {

  String baseDirectory;
  String namePrefix;
  String nameSuffix;
  FileNameIncrementalType fileNameIncrementalType;
  List<Field> fields;
  FileType fileType;
  int numRows;
  int numFiles;

  public String getBaseDirectory() {
    return baseDirectory;
  }

  public String getNamePrefix() {
    return namePrefix;
  }

  public String getNameSuffix() {
    return nameSuffix;
  }

  public FileNameIncrementalType getFileNameIncrementalType() {
    return fileNameIncrementalType;
  }

  public List<Field> getFields() {
    return fields;
  }

  public FileType getFileType() {
    return fileType;
  }

  public int getNumRows() {
    return numRows;
  }

  public int getNumFiles() {
    return numFiles;
  }

  public void setBaseDirectory(String baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  public void setNamePrefix(String namePrefix) {
    this.namePrefix = namePrefix;
  }

  public void setNameSuffix(String nameSuffix) {
    this.nameSuffix = nameSuffix;
  }

  public void setFileNameIncrementalType(FileNameIncrementalType fileNameIncrementalType) {
    this.fileNameIncrementalType = fileNameIncrementalType;
  }

  public void setFields(List<Field> fields) {
    this.fields = fields;
  }

  public void setFileType(FileType fileType) {
    this.fileType = fileType;
  }

  public void setNumRows(int numRows) {
    this.numRows = numRows;
  }

  public void setNumFiles(int numFiles) {
    this.numFiles = numFiles;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
