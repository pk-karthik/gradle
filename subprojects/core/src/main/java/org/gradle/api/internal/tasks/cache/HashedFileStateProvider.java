/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.tasks.cache;

import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class HashedFileStateProvider implements FileStateProvider {
    @Override
    public FileState getFileState(File file) throws IOException {
        if (!file.exists()) {
            return MissingFileState.INSTANCE;
        }
        if (file.isDirectory()) {
            return DirState.INSTANCE;
        }
        Hasher hasher = Hashing.md5().newHasher();
        Files.asByteSource(file).copyTo(Funnels.asOutputStream(hasher));
        HashCode hashCode = hasher.hash();
        return new HashedFileState(file.lastModified(), hashCode);
    }
}