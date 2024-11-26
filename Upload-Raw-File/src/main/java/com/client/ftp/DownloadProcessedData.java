package com.client.ftp;

import com.client.config.ClientConfig;
import com.client.config.FTPConfig;
import com.client.config.ProcessCodes;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class DownloadProcessedData {

    Logger LOGGER = LoggerFactory.getLogger(DownloadProcessedData.class);

    @Autowired
    FTPConfig ftpConfig;

    @Autowired
    ClientConfig clientConfig;

    @Autowired
    RestTemplate template;

    String host ;
    int port ;
    String user ;
    String opener ;

    FTPClient ftpClient = new FTPClient();
    InputStream inputStream = null;

    @Scheduled(cron = "10 12 * * MON")
    void downloadProcessedFile() throws InterruptedException
    {
        LOGGER.info("[{} downloadProcessedFile - Entry]",DownloadProcessedData.class.getName());
        try{

            /***************************************************************************************************************/
            /**  AMDOX has exposed a Rest API for polling to check if the file has been processed or not                  **/
            /**  if Rest API response is positive then AMDOX has placed the processed file on FTP configured path.        **/
            /***************************************************************************************************************/

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(clientConfig.getDateFormat());
            String formattedDate = dateFormat.format(date);

            LOGGER.info("[{} downloadProcessedFile - Calling mdox REST API to check current file status.]",DownloadProcessedData.class.getName());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity entity = new HttpEntity<>("parameters",headers);
            com.client.entity.ResponseEntity responseEntity = template.getForObject(clientConfig.getRestURL().replace("[PARAMETER]",formattedDate), com.client.entity.ResponseEntity.class);
            LOGGER.info("[{} downloadProcessedFile - mdox REST API success. Message {}]",DownloadProcessedData.class.getName(), responseEntity.getResponse());
           if(ProcessCodes.UPLOADED_TO_FTP.equalsIgnoreCase(responseEntity.getResponse()))
            {
                /********************************/
                /** POLL FTP TO DOWNLOAD FILE  **/
                /********************************/
                LOGGER.info("[{} downloadProcessedFile - polling ftp URL '{}' to download the processed file.]",DownloadProcessedData.class.getName(),ftpConfig.getHost());
                host = ftpConfig.getHost();
                port = ftpConfig.getPort();
                user = ftpConfig.getUsername();
                opener = ftpConfig.getOpener();

                ftpClient.connect(host, port);
                ftpClient.login(user, opener);
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                LOGGER.info("[{} downloadProcessedFile] - connected with ftp '{}'",DownloadProcessedData.class.getName(),ftpConfig.getHost());

                /*********************************************/
                /**  DOWNLOAD PROCESSED DATA FILE FROM FTP' **/
                /*********************************************/

                String processedfileName =  ftpConfig.getProcessedDataFileName().replace("[DATE]",formattedDate);
                File downloadFile = new File(clientConfig.getProcessedDataDir()+clientConfig.getProcessedDataFileName().replace("[DATE]",formattedDate));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                ftpClient.changeWorkingDirectory(ftpConfig.getProcessedDataDir());
                LOGGER.info("[{} downloadProcessedFile - processed file name '{}'.]",DownloadProcessedData.class.getName(),processedfileName);
                LOGGER.info("[{} downloadProcessedFile - downloading file with name '{}'.]",DownloadProcessedData.class.getName(),downloadFile);
                boolean success = ftpClient.retrieveFile(processedfileName, outputStream);

                outputStream.close();

                if (success) {
                    LOGGER.info("[{} downloadProcessedFile - Successfully downloaded file with name '{}'.]",DownloadProcessedData.class.getName(),downloadFile);
                }
                else{
                    LOGGER.info("[{} downloadProcessedFile - Couldn't download file with name '{}' from FTP.]",DownloadProcessedData.class.getName(),downloadFile);
                }
                boolean completed = ftpClient.completePendingCommand();
                if (completed) {
                    LOGGER.info("[{} downloadProcessedFile - pending operations at FTP completed.]",DownloadProcessedData.class.getName());
                }
            }
           else if(ProcessCodes.UPLOADING_TO_FTP.equalsIgnoreCase(responseEntity.getResponse()) ||
                    ProcessCodes.PROCESSING.equalsIgnoreCase(responseEntity.getResponse()))
            {
                /**
                 * BUSINESS LOGIC TO WAIT FOR PROCESS TO GET COMPLETED
                 * **/
            }
            else if(ProcessCodes.NOT_PROCESSED.equalsIgnoreCase(responseEntity.getResponse()) )
            {
                /**
                 * BUSINESS LOGIC TO TAKE FURTHER STEP
                 * **/
            }
            else if(ProcessCodes.UPLOADED_TO_FTP_FAILED.equalsIgnoreCase(responseEntity.getResponse())||
                    ProcessCodes.PROCESSING_FAILED.equalsIgnoreCase(responseEntity.getResponse()))
            {
                /**
                 * BUSINESS LOGIC TO STOP PROCESS
                 * **/
            }
            else if(ProcessCodes.PROCESSED.equalsIgnoreCase(responseEntity.getResponse()))
            {
                /**
                 * BUSINESS LOGIC TO TAKE FURTHER STEP
                 * **/
            }

        }
        catch (Exception e)
        {
            LOGGER.error("[{} downloadProcessedFile - Exception while downloading processed file from ftp.]",DownloadProcessedData.class.getName());
            LOGGER.error("[{} downloadProcessedFile - Exception: {}]",DownloadProcessedData.class.getName(),e.getMessage());
        }


        finally {
            LOGGER.error("[{} downloadProcessedFile - closing ftp connections.]",DownloadProcessedData.class.getName());
            try {
                // inputStream.close();
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                    LOGGER.error("[{} downloadProcessedFile - Connections closed.]",DownloadProcessedData.class.getName());
                }

            } catch (Exception ex) {
                LOGGER.error("[{} downloadProcessedFile - Exception while closing ftp connections.]",DownloadProcessedData.class.getName());
                LOGGER.error("[{} downloadProcessedFile - Exception: {}]",DownloadProcessedData.class.getName(),ex.getMessage());
            }
        }
    }

}
