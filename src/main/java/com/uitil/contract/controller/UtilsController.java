package com.uitil.contract.controller;

import com.uitil.contract.service.IConvertService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;

/**
 * 工具
 */
@RestController
public class UtilsController {

    @Resource
    private IConvertService iConvertService;

    /**
     * 将docx文件转换为html
     * @param destFile
     * @return
     */
    @RequestMapping(value = "/docx2html.do", method = {RequestMethod.GET, RequestMethod.PUT})
    public String docxConvert(String destFile) {
        if (StringUtils.isEmpty(destFile))  {
            return "error, file is empty !";
        }
        boolean flag = false;
        try {
            File file = new File(destFile);
            if (file.isDirectory()) {
                //批量处理文件
                String[] fileNames = file.list();
                for (int i = 0; i < fileNames.length; i++) {
                    String fileName = fileNames[i];
                    //具体子目录
                    String detailFile = destFile + File.separator + fileName;
                    File file1 = new File(detailFile);
                    if (file1.isFile() && fileName.contains("docx")) {
                         flag = iConvertService.convertDoc2Html(detailFile);
                    }
                }
            } else {
                flag = iConvertService.convertDoc2Html(destFile);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        if (flag) {
            return "success";
        }
        return "false";
    }
}
