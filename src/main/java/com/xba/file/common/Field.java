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

public class Field<T> {

  private String name;
  private FieldType fieldType;
  private T value;

  public Field() {
    // empty public method needed to handle JSON object deserialization from incoming HTTP requests
  }

  public Field(String name, FieldType fieldType, T value) {
    this.name = name;
    this.fieldType = fieldType;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public FieldType getFieldType() {
    return fieldType;
  }

  public T getValue() {
    return value;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setFieldType(FieldType fieldType) {
    this.fieldType = fieldType;
  }

  public void setValue(T value) {
    this.value = value;
  }
}
