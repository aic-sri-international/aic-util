package com.sri.ai.util.compilation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.tools.SimpleJavaFileObject;

public class JavaByteObject extends SimpleJavaFileObject {
    private ByteArrayOutputStream outputStream;

    protected JavaByteObject(String name) throws URISyntaxException {
        super(URI.create("bytes:///"+name + name.replaceAll("\\.", "/")), Kind.CLASS);
        outputStream = new ByteArrayOutputStream();
    }

    // overriding this to provide our OutputStream to which the
    // byte code can be written.
    @Override
    public OutputStream openOutputStream() throws IOException {
        return outputStream;
    }

    public byte[] getBytes() {
        return outputStream.toByteArray();
    }
}

