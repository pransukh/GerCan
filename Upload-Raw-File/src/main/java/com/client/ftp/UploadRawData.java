package com.client.ftp;

import com.client.config.ClientConfig;
import com.client.config.FTPConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@EnableScheduling

public class UploadRawData {

    Logger LOGGER = LoggerFactory.getLogger(DownloadProcessedData.class);

    @Autowired
    FTPConfig ftpConfig;

    @Autowired
    ClientConfig clientConfig;

    String host ;
    int port ;
    String user ;
    String opener ;

    FTPClient ftpClient = new FTPClient();
    InputStream inputStream = null;

    @Scheduled(cron = "0 12 * * MON")
    void uploadRawFile() throws InterruptedException
    {
        LOGGER.info("[{} uploadRawFile] - Entry",UploadRawData.class.getName());
        try{
            LOGGER.info("[{} uploadRawFile - polling ftp URL '{}' to upload the raw file.]",UploadRawData.class.getName(),ftpConfig.getHost());

            host = ftpConfig.getHost();
            port = ftpConfig.getPort();
            user = ftpConfig.getUsername();
            opener = ftpConfig.getOpener();

            ftpClient.connect(host, port);
            ftpClient.login(user, opener);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            LOGGER.info("[{} uploadRawFile] - connected with ftp '{}'",UploadRawData.class.getName(),ftpConfig.getHost());


            /********************************************************************************/
            /** UPLOAD RAW DATA FILE FROM CLIENT'S MACHINE TO FTP DIRECTORY NAMED 'RAW'    **/
            /********************************************************************************/

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(clientConfig.getDateFormat());
            String formattedDate = dateFormat.format(date);

            String clientFilePath = clientConfig.getRawDataDir() + clientConfig.getRawDataFileName().replace("[DATE]",formattedDate);;
            String ftpFileName = clientConfig.getRawDataFileName().replace("[DATE]",formattedDate);
            LOGGER.info("[{} uploadRawFile - raw file name '{}'.]",UploadRawData.class.getName(),clientFilePath);
            LOGGER.info("[{} uploadRawFile - uploading file with name '{}'.]",UploadRawData.class.getName(),ftpFileName);

            File fileNametoUpload = new File(clientFilePath); //File Size is More than 3GB
            inputStream = new FileInputStream(fileNametoUpload);

            ftpClient.changeWorkingDirectory(ftpConfig.getRawDataDir());
            LOGGER.info("[{} uploadRawFile - ftp current working directory: '{}'.]",UploadRawData.class.getName(),ftpConfig.getRawDataDir());

            boolean done = ftpClient.storeFile(ftpFileName, inputStream);

            if (done) {
                LOGGER.info("[{} uploadRawFile - Successfully uploaded file with name '{}'.]",UploadRawData.class.getName(),clientConfig.getRawDataFileName().replace("[DATE]",formattedDate));
            }else
            {
                LOGGER.info("[{} uploadRawFile - couldn't uploaded file with name '{}'.]",UploadRawData.class.getName(),clientConfig.getRawDataFileName().replace("[DATE]",formattedDate));
            }
        }
        catch (Exception e)
        {
            LOGGER.error("[{} uploadRawFile - Exception while uploading raw file to ftp.]",UploadRawData.class.getName());
            LOGGER.error("[{} uploadRawFile - Exception: {}]",UploadRawData.class.getName(),e.getMessage());
        }
        finally {
            LOGGER.error("[{} uploadRawFile - closing ftp connections.]",UploadRawData.class.getName());
            try {
                // inputStream.close();
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                    LOGGER.error("[{} uploadRawFile - Connections closed.]",UploadRawData.class.getName());
                }

            } catch (Exception ex) {
                LOGGER.error("[{} uploadRawFile - Exception while closing ftp connections.]",UploadRawData.class.getName());
                LOGGER.error("[{} uploadRawFile - Exception: {}]",UploadRawData.class.getName(),ex.getMessage());
            }
        }


    }

}



