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

import com.xba.file.common.Field;

import java.util.ArrayList;
import java.util.List;

public class Row {

  private List<Field> rowFields;

  public Row() {
    rowFields = new ArrayList<>();
  }

  public void addField(Field field) {
    this.rowFields.add(field);
  }

  @Override
  public String toString() {
    StringBuilder rowStringBuilder = new StringBuilder();

    rowFields.forEach(field -> {
      rowStringBuilder.append(field.getValue());
      rowStringBuilder.append(',');
    });

    return rowStringBuilder.toString();
  }
}
