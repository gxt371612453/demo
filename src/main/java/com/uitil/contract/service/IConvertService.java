package com.uitil.contract.service;

import java.io.IOException;

public interface IConvertService {

    boolean convertDoc2Html(String srcFile) throws IOException;
}
