package com.client.ftp;

import com.client.config.MdoxConfig;
import com.client.config.FTPConfig;
import com.client.config.ProcessCodes;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;

import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;



@Configuration
@EnableScheduling
public class DownloadRawData {
    Logger LOGGER = LoggerFactory.getLogger(DownloadRawData.class);

    @Autowired
    FTPConfig ftpConfig;

    @Autowired
    MdoxConfig mdoxConfig;



    String host ;
    int port ;
    String user ;
    String opener ;

    FTPClient ftpClient = new FTPClient();
    InputStream inputStream = null;

    @Scheduled(cron = "5 12 * * MON")
    void downloadRawFile() throws InterruptedException
    {
        LOGGER.info("[{} downloadRawFile - Entry]",DownloadRawData.class.getName());
        try{


            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(mdoxConfig.getDateFormat());
            String formattedDate = dateFormat.format(date);
            LOGGER.info("[{} downloadRawFile - Connecting with ftp host at '{}']",DownloadRawData.class.getName(),ftpConfig.getHost());
                host = ftpConfig.getHost();
                port = ftpConfig.getPort();
                user = ftpConfig.getUsername();
                opener = ftpConfig.getOpener();

                ftpClient.connect(host, port);
                ftpClient.login(user, opener);
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            LOGGER.info("[{} downloadRawFile - Connection successful with ftp host at '{}']",DownloadRawData.class.getName(),ftpConfig.getHost());
                /***************************************************************/
                /** DOWNLOAD RAW DATA FILE FROM FTP MACHINE TO MDOX's RAW DIR **/
                /***************************************************************/

                String rawfileName =  ftpConfig.getRawDataFileName().replace("[DATE]",formattedDate);
                File downloadFile = new File(mdoxConfig.getRawDataDir()+ mdoxConfig.getRawDataFileName().replace("[DATE]",formattedDate));
                OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile));
                ftpClient.changeWorkingDirectory(ftpConfig.getRawDataDir());
            LOGGER.info("[{} downloadRawFile - Current ftp directory '{}']",DownloadRawData.class.getName(),ftpConfig.getRawDataDir());
            LOGGER.info("[{} downloadRawFile - downloading raw file for processing, File Name: '{}']",DownloadRawData.class.getName(),ftpConfig.getRawDataFileName());

            boolean success = ftpClient.retrieveFile(rawfileName, outputStream1);
                outputStream1.close();

                if (success) {
                    LOGGER.info("[{} downloadRawFile -successfully downloaded raw file for processing, File path: '{}']",DownloadRawData.class.getName(),downloadFile);

                    if(ProcessCodes.PROCESSED.equalsIgnoreCase(processRawDataFile(downloadFile,formattedDate)))
                    {
                        /***************************************************************/
                        /** UPLOAD PROCSSED DATA FILE TO FTP FROM MDOX's PROCESSED DIR **/
                        /***************************************************************/
                        LOGGER.info("[{} downloadRawFile - uploading the processed file to FTP at :'{}']",DownloadRawData.class.getName(), ftpConfig.getProcessedDataDir());
                        String processedFilePath = mdoxConfig.getProcessedDataDir() + mdoxConfig.getProcessedDataFileName().replace("[DATE]",formattedDate);;
                        String ftpFileName = mdoxConfig.getProcessedDataFileName().replace("[DATE]",formattedDate);


                        File processedFileObject = new File(processedFilePath);
                        inputStream = new FileInputStream(processedFileObject);

                        ftpClient.changeWorkingDirectory(ftpConfig.getProcessedDataDir());
                        LOGGER.info("[{} downloadRawFile - current FTP dir :'{}']",DownloadRawData.class.getName(), ftpConfig.getProcessedDataDir());
                        boolean done = ftpClient.storeFile(ftpFileName, inputStream);

                        if (done) {

                            /**
                             * Updating a centralized  status for client's machine to know the current status of RAW_DATA file.
                             */
                            LOGGER.info("[{} downloadRawFile - processed file uploaded successfully']",DownloadRawData.class.getName());
                            FileWriter output= new FileWriter(mdoxConfig.getProcessedFileStatusPath());
                            output.write(ProcessCodes.UPLOADED_TO_FTP);
                            output.close();
                            LOGGER.info("[{} downloadRawFile - successfully updated the status file as:'{}']",DownloadRawData.class.getName(), ProcessCodes.UPLOADED_TO_FTP);
                        }
                    }
                }
                boolean completed = ftpClient.completePendingCommand();
                if (completed) {
                    LOGGER.info("[{} downloadRawFile - completed all pending ftp actions]",DownloadRawData.class.getName());
                }


                // ENDs
 }
        catch (Exception e)
        {

            LOGGER.error("[{} downloadRawFile - Exception while downloading/Uploading processed file from ftp.]",DownloadRawData.class.getName());
            LOGGER.error("[{} downloadRawFile - Exception: {}]",DownloadRawData.class.getName(),e.getMessage());
        }
        finally {
            LOGGER.error("[{} downloadRawFile - closing ftp connections.]",DownloadRawData.class.getName());
            try {
                // inputStream.close();
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                    LOGGER.error("[{} downloadRawFile - Connections closed.]",DownloadRawData.class.getName());
                }

            } catch (Exception ex) {
                LOGGER.error("[{} downloadRawFile - Exception while closing ftp connections.]",DownloadRawData.class.getName());
                LOGGER.error("[{} downloadRawFile - Exception: {}]",DownloadRawData.class.getName(),ex.getMessage());
            }
        }


    }

    /**
     *
     * This function have following main tasks
     * 1. Process the raw data file.
     * 2. Change file name to Processed
     * 3. Copy the processed file in processed folder.
     * 4. update the status in file through which Client is getting information about current status of file with REST API calls.
     *
     * **/
    private String processRawDataFile(File filePath, String formattedDate)
    {
        /**
         * BUSINESS LOGIC TO PROCESS RAW DATA FILE.
         * */
        LOGGER.info("[{} downloadRawFile - successfully processed raw file.]",DownloadRawData.class.getName());

        /**Changing file name to PROCESSED_DATA_[DATE].AMDOX after completing the data processing logic*/
        File source = new File(filePath.toString());
        File dest = new File(mdoxConfig.getProcessedDataDir()+mdoxConfig.getProcessedDataFileName().replace("[DATE]",formattedDate));
        LOGGER.info("[{} downloadRawFile - successfully changed the name for processed file. Name:{}]",DownloadRawData.class.getName(), filePath.toString());

        try {
            /**Placing PROCESSED_DATA_[DATE].AMDOX to mdox process folder*/
            Files.copy(source.toPath(), dest.toPath());
            LOGGER.info("[{} downloadRawFile - successfully copied the processed file to dir:'{}']",DownloadRawData.class.getName(), dest);


            /**
             * Updating a centralized  status for client's machine to know the current status of RAW_DATA file.
             */

            FileWriter output= new FileWriter(mdoxConfig.getProcessedFileStatusPath());
            output.write(ProcessCodes.PROCESSED);
            output.close();
            LOGGER.info("[{} downloadRawFile - successfully updated the status file as:'{}']",DownloadRawData.class.getName(), ProcessCodes.PROCESSED);

        } catch (IOException e) {
            LOGGER.error("[{} downloadRawFile - Exception while downloading processed file from ftp.]",DownloadRawData.class.getName());
            LOGGER.error("[{} downloadRawFile - Exception: {}]",DownloadRawData.class.getName(),e.getMessage());
            return ProcessCodes.PROCESSING_FAILED;
        }

        return ProcessCodes.PROCESSED;

    }

}
