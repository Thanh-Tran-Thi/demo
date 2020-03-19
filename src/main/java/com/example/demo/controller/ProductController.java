package com.example.demo.controller;

import com.example.demo.entity.ApiResponse;
import com.example.demo.entity.Product;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.ProductService;
import com.example.demo.utils.AppConstants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    FileStorageService fileService;

    ObjectMapper objectMapper = new ObjectMapper();

//    @GetMapping(value = AppConstants.PRODUCT_URI)
//    public List<Product> listAllProducts(){
//        return productService.listProducts();
//    }

    @GetMapping(value = AppConstants.PRODUCT_URI)
    public ApiResponse<List<Product>> listAll(){
        return new ApiResponse<List<Product>>(AppConstants.SUCCESS_CODE, AppConstants.CREATE_SUCCESS_MSG, productService.listProducts());
    }

    @RequestMapping(value = AppConstants.PRODUCT_URI, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse createProduct(@RequestParam(value = AppConstants.PRODUCT_JSON_PARAM, required = true) String proJson,
                                    @RequestParam(required = true, value = AppConstants.FILE_PARAM) MultipartFile file)
            throws JsonParseException, JsonMappingException, IOException {
        String fileName = fileService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path(AppConstants.DOWNLOAD_PATH).path(fileName).toUriString();
        Product product = objectMapper.readValue(proJson,Product.class);
        product.setImagePath(fileDownloadUri);
        productService.createProducts(product);

        return new ApiResponse(AppConstants.SUCCESS_CODE, AppConstants.CREATE_SUCCESS_MSG);
    }
}
