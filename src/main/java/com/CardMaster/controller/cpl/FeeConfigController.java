//package com.CardMaster.controller.cpl;
//
//import com.CardMaster.dto.cpl.FeeConfigRequestDto;
//import com.CardMaster.dto.cpl.FeeConfigResponseDto;
//import com.CardMaster.service.cpl.FeeConfigService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/fees")
//@RequiredArgsConstructor
//public class FeeConfigController {
//
//    private final FeeConfigService service;
//
//    @PostMapping
//    public ResponseEntity<FeeConfigResponseDto> addFee(@RequestBody FeeConfigRequestDto req) {
//        // Controller passes exactly 1 argument to service.addFee(...)
//        return ResponseEntity.ok(service.addFee(req));
//    }
//
//    @GetMapping("/product/{productId}")
//    public ResponseEntity<List<FeeConfigResponseDto>> getFeesByProduct(@PathVariable Long productId) {
//        return ResponseEntity.ok(service.getFeesByProduct(productId));
//    }
//}

package com.CardMaster.controller.cpl;

import com.CardMaster.dto.cpl.FeeConfigRequestDto;
import com.CardMaster.dto.cpl.FeeConfigResponseDto;
import com.CardMaster.service.cpl.FeeConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeeConfigController {

    private final FeeConfigService service;

    @PostMapping
    public ResponseEntity<FeeConfigResponseDto> addFee(@RequestBody FeeConfigRequestDto req) {
        // Controller passes exactly 1 argument to service.addFee(...)
        return ResponseEntity.ok(service.addFee(req));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<FeeConfigResponseDto>> getFeesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getFeesByProduct(productId));
    }
}
