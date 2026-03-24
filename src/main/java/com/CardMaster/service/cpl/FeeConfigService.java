//package com.CardMaster.service.cpl;
//
//import com.CardMaster.dto.cpl.FeeConfigRequestDto;
//import com.CardMaster.dto.cpl.FeeConfigResponseDto;
//import com.CardMaster.mapper.cpl.FeeConfigMapper;
//import com.CardMaster.model.cpl.CardProduct;
//import com.CardMaster.model.cpl.FeeConfig;
//import com.CardMaster.dao.cpl.CardProductRepository;
//import com.CardMaster.dao.cpl.FeeConfigRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class FeeConfigService {
//
//    private final FeeConfigRepository feeRepo;
//    private final CardProductRepository productRepo;
//    private final FeeConfigMapper mapper;
//
//    public FeeConfigResponseDto addFee(FeeConfigRequestDto req) {
//        // NOTE: We must load product first to pass as 2nd argument to mapper
//        CardProduct product = productRepo.findById(req.getProductId())
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        FeeConfig entity = mapper.toEntity(req, product); // <-- 2 arguments
//        FeeConfig saved = feeRepo.save(entity);
//        return mapper.toResponse(saved);
//    }
//
//    public List<FeeConfigResponseDto> getFeesByProduct(Long productId) {
//        return feeRepo.findByProduct_ProductId(productId)
//                .stream()
//                .map(mapper::toResponse)
//                .toList();
//    }
//}
package com.CardMaster.service.cpl;

import com.CardMaster.dto.cpl.FeeConfigRequestDto;
import com.CardMaster.dto.cpl.FeeConfigResponseDto;
import com.CardMaster.mapper.cpl.FeeConfigMapper;
import com.CardMaster.model.cpl.CardProduct;
import com.CardMaster.model.cpl.FeeConfig;
import com.CardMaster.dao.cpl.CardProductRepository;
import com.CardMaster.dao.cpl.FeeConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeConfigService {

    private final FeeConfigRepository feeRepo;
    private final CardProductRepository productRepo;
    private final FeeConfigMapper mapper;

    public FeeConfigResponseDto addFee(FeeConfigRequestDto req) {
        // NOTE: We must load product first to pass as 2nd argument to mapper
        CardProduct product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        FeeConfig entity = mapper.toEntity(req, product); // <-- 2 arguments
        FeeConfig saved = feeRepo.save(entity);
        return mapper.toResponse(saved);
    }

    public List<FeeConfigResponseDto> getFeesByProduct(Long productId) {
        return feeRepo.findByProduct_ProductId(productId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}