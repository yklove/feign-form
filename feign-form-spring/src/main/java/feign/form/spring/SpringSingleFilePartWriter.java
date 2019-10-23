/*
 * Copyright 2019 the original author or authors.
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

package feign.form.spring;

import feign.codec.EncodeException;
import feign.form.multipart.AbstractWriter;
import feign.form.multipart.Output;
import lombok.val;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author qinggeng
 */
public class SpringSingleFilePartWriter extends AbstractWriter {

  static String CONTENT_TYPE = "content-type";

  @Override
  public boolean isApplicable (Object value) {
    return value instanceof FilePart;
  }

  @Override
  protected void write (Output output, String key, Object value) throws EncodeException {
    val file = (FilePart) value;
    writeFileMetadata(output, key, file.filename(), getContentType(file));

    file.content().subscribe(dataBuffer -> {
      byte[] bytes = new byte[dataBuffer.readableByteCount()];
      dataBuffer.read(bytes);
      DataBufferUtils.release(dataBuffer);
      output.write(bytes);
    });
  }

  private String getContentType(FilePart filePart) {
    List<String> contentList = filePart.headers().get(CONTENT_TYPE);
    return CollectionUtils.isEmpty(contentList) ? null : contentList.get(0);
  }
}
