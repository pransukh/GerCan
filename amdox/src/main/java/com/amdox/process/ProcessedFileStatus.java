package com.amdox.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/checkStatus")
public class ProcessedFileStatus {

    Logger logger = LoggerFactory.getLogger(ProcessedFileStatus.class);


    @Autowired
    Config config;

    @GetMapping(value = "/{date}")
    @ResponseBody
    public ResponseEntity checkStatus(@PathVariable("date") String date){
        logger.info("[GET - {}], Entry. data: {} ",ProcessedFileStatus.class,date);
        String response = "";

        File file = new File(config.getProcessedFileStatusPath());
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (Exception e) {
            logger.error("[GET - {}], Exception while reading status file. Message: {} ",ProcessedFileStatus.class,e.getMessage());
        }

        while (sc.hasNextLine())
        {
            response = sc.nextLine();
            logger.info("[GET - {}], Response retrieved from status file. Message: {} ",ProcessedFileStatus.class,response);
        }
        logger.info("[GET - {}], Exit with no error. ",ProcessedFileStatus.class);
        return new ResponseEntity(response);

    }
}

class ResponseEntity
{
    private String response;

    public ResponseEntity(String response)
    {
        this.response = response;
    }
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
