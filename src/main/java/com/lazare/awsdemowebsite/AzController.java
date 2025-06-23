package com.lazare.awsdemowebsite;

import com.amazonaws.util.EC2MetadataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AzController {


    @GetMapping
    public Object getStatus(){
        String availabilityZone = EC2MetadataUtils.getAvailabilityZone();
        String region = EC2MetadataUtils.getEC2InstanceRegion();
        return Map.of("AZ", availabilityZone, "region", region);
    }



}
