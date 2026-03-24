//package com.CardMaster.service.cpl;
//
//import com.CardMaster.dto.cpl.CardProductRequestDto;
//import com.CardMaster.dto.cpl.CardProductResponseDto;
//import com.CardMaster.mapper.cpl.CardProductMapper;
//import com.CardMaster.model.cpl.CardProduct;
//import com.CardMaster.dao.cpl.CardProductRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class CardProductService {
//
//    private final CardProductRepository repository;
//    private final CardProductMapper mapper;
//
//    public CardProductResponseDto create(CardProductRequestDto request) {
//        CardProduct entity = mapper.toEntity(request);
//        CardProduct saved = repository.save(entity);
//        return mapper.toResponse(saved);
//    }
//
//    public List<CardProductResponseDto> findAll() {
//        return repository.findAll()
//                .stream()
//                .map(mapper::toResponse)
//                .toList();
//    }
//
//    public CardProductResponseDto findById(Long id) {
//        CardProduct entity = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//        return mapper.toResponse(entity);
//    }
//}


package com.CardMaster.service.cpl;

import com.CardMaster.Enum.cpl.ProductStatus;
import com.CardMaster.dto.cpl.CardProductRequestDto;
import com.CardMaster.dto.cpl.CardProductResponseDto;
import com.CardMaster.mapper.cpl.CardProductMapper;
import com.CardMaster.model.cpl.CardProduct;
import com.CardMaster.dao.cpl.CardProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardProductService {

    private final CardProductRepository repository;
    private final CardProductMapper mapper;

    public CardProductResponseDto create(CardProductRequestDto request) {
        CardProduct entity = mapper.toEntity(request);

        // Set default status since request does NOT carry it anymore
        entity.setStatus(ProductStatus.ACTIVE);   // ✅ default status

        CardProduct saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<CardProductResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CardProductResponseDto findById(Long id) {
        CardProduct entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapper.toResponse(entity);
    }
}