package com.example.demo.service.implement;

import com.example.demo.config.FileStorageProperties;
import com.example.demo.exception.FileException;
import com.example.demo.exception.FileNotFoundException;
import com.example.demo.service.FileStorageService;
import com.example.demo.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImp implements FileStorageService {

    private final Path fileLocation;

    /* Constructor
    *  and create directory to save file
    */
    @Autowired
    public FileStorageServiceImp(FileStorageProperties storageProperties) {
        this.fileLocation = Paths.get(storageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        } catch (Exception ex){
            throw new FileException(AppConstants.FILE_STORAGE_NOT_FOUND_PATH, ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (!(file.getOriginalFilename().endsWith(AppConstants.PNG_FILE_FORMAT) || file.getOriginalFilename().endsWith(AppConstants.JPG_FILE_FORMAT) || file.getOriginalFilename().endsWith(AppConstants.JPEG_FILE_FORMAT))){
            throw new FileException(AppConstants.INVALID_FILE_FORMAT);
        }

        File f = new File(AppConstants.TEMP_DIR + file.getOriginalFilename());

        f.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();

        BufferedImage image = ImageIO.read(f);

        if (f.exists())
            f.delete();

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try{
            if (fileName.contains(AppConstants.INVALID_FILE_DELIMITER)){
                throw new FileException(AppConstants.INVALID_FILE_PATH_NAME + fileName);
            }
            String newFileName = System.currentTimeMillis() + AppConstants.FILE_SEPERATOR + fileName;
            Path targetLocation = this.fileLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        } catch (IOException ex) {
            throw new FileException(String.format(AppConstants.FILE_STORAGE_EXCEPTION, fileName), ex);
        }
    }

    //download File
    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()){
                return resource;
            } else {
                throw new FileNotFoundException(AppConstants.FILE_NOT_FOUND + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException(AppConstants.FILE_NOT_FOUND, ex);
        }
    }
}
