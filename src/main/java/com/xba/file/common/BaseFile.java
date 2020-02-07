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

public abstract class BaseFile {

  protected final String baseDirectory;
  protected final String namePrefix;
  protected final String nameSuffix;
  protected final String randomIncrementalPrefix;

  public BaseFile(String baseDirectory, String namePrefix, String nameSuffix, String randomIncrementalPrefix) {
    this.baseDirectory = baseDirectory;
    this.namePrefix = namePrefix;
    this.nameSuffix = nameSuffix;
    this.randomIncrementalPrefix = randomIncrementalPrefix;
  }

  /**
   * Create the file with corresponding fields
   *
   * @throws FileBuilderException If file cannot be created
   */
  public abstract void create() throws FileBuilderException;

}
