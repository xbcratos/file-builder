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

package com.xba.file.common;

public enum Errors {

  FILE_BUILDER_00("Unknown file type: {}"),
  FILE_BUILDER_01("Field '{}' has an unknown field type: {}"),
  FILE_BUILDER_02("Error when creating File Writer for file type: {}"),
  FILE_BUILDER_03("Error creating file: {}. Created files: {}. Error files: {}"),
  ;

  private final String errorMessage;

  Errors(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
